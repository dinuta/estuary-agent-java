package com.github.dinuta.estuary.agent.component;

import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.model.ProcessState;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import com.github.dinuta.estuary.agent.model.api.CommandDetails;
import com.github.dinuta.estuary.agent.model.api.CommandParallel;
import com.github.dinuta.estuary.agent.model.api.CommandStatus;
import com.github.dinuta.estuary.agent.utils.CommandStatusThread;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.dinuta.estuary.agent.constants.DefaultConstants.*;
import static com.github.dinuta.estuary.agent.constants.EnvConstants.COMMAND_TIMEOUT;

@Component
public class CommandRunner {
    private static final Logger log = LoggerFactory.getLogger(CommandRunner.class);

    private static final String EXEC_WIN = "cmd.exe";
    private static final String ARGS_WIN = "/c";
    private static final String EXEC_LINUX = "/bin/sh";
    private static final String ARGS_LINUX = "-c";

    private static final float DENOMINATOR = 1000F;

    @Autowired
    private final VirtualEnvironment environment;

    public CommandRunner(VirtualEnvironment environment) {
        this.environment = environment;
    }

    /**
     * Runs a single system command
     *
     * @param command The command to be executed
     * @return The details of the command
     * @throws IOException if the process could not be started
     */
    public CommandDetails runCommand(String command) throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = getPlatformCommand();
        String commandWithSingleSpaces = command.trim().replaceAll("\\s+", " ");

        if (isWindows) {
            for (String cmd : commandWithSingleSpaces.split(" ")) {
                fullCommand.add(cmd);
            }
        } else {
            fullCommand.add(command);
        }

        return this.getCommandDetails(fullCommand.toArray(new String[0]));
    }

    /**
     * Runs the system commands sequentially, one after the other
     *
     * @param commands The system commands to be executed
     * @return The description of all commands
     * @throws IOException if the process could not be started
     */
    public CommandDescription runCommands(String[] commands) throws IOException {
        LinkedHashMap commandsStatus = new LinkedHashMap<String, CommandStatus>();
        CommandDescription commandDescription = CommandDescription.builder()
                .startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .started(true)
                .finished(false)
                .pid(ProcessHandle.current().pid())
                .build();


        for (String cmd : commands) {
            CommandStatus commandStatus = new CommandStatus();
            commandStatus.setStartedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandStatus.setDetails(this.runCommand(cmd));
            commandStatus.setFinishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandStatus.setDuration(Duration.between(
                    LocalDateTime.parse(commandStatus.getStartedat(), DateTimeConstants.PATTERN),
                    LocalDateTime.parse(commandStatus.getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
            commandStatus.setStatus("finished");
            commandsStatus.put(cmd, commandStatus);
            commandDescription.setCommands(commandsStatus);
        }

        commandDescription.setFinishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
        commandDescription.setDuration(Duration.between(
                LocalDateTime.parse(commandDescription.getStartedat(), DateTimeConstants.PATTERN),
                LocalDateTime.parse(commandDescription.getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
        commandDescription.setFinished(true);
        commandDescription.setStarted(false);

        return commandDescription;
    }

    /**
     * Runs the commands through the start.py script of the original python implementation.
     * Ref: https://github.com/dinuta/estuary-agent/releases.
     * This start.py is platform dependent and it must be downloaded in the same path along with this jar
     *
     * @param command The commands to be executed separated by semicolon ;
     * @return A reference to a Future of {@link ProcessResult}
     * @throws IOException if the process could not be started
     */
    public Future<ProcessResult> runStartCommandDetached(List<String> command) throws IOException {
        String pythonExec = "start.py";
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCmd = getPlatformCommand();
        String commandsSeparatedBySemicolon = "";

        for (int i = 1; i < command.size(); i++) {
            commandsSeparatedBySemicolon += command.get(i) + ";";
        }

        if (isWindows) {
            fullCmd.add(String.format("%s/%s", Paths.get("").toAbsolutePath().toString(), pythonExec));
            fullCmd.add(this.doQuoteCmd(command.get(0)) + " " +
                    this.doQuoteCmd(StringUtils.stripEnd(commandsSeparatedBySemicolon, ";")));
        } else {
            fullCmd.add(
                    this.doQuoteCmd(String.format("%s/%s", Paths.get("").toAbsolutePath().toString(), pythonExec)) + " " +
                            this.doQuoteCmd(command.get(0)) + " " +
                            this.doQuoteCmd(StringUtils.stripEnd(commandsSeparatedBySemicolon, ";")));
        }

        return this.runStartCmdDetached(fullCmd.toArray(new String[0])).start().getFuture();
    }

    /**
     * Runs one command in detached mode, aka Non-blocking mode.
     *
     * @param command The system command to be executed
     * @return A reference to a {@link ProcessExecutor}
     * @throws IOException if the process could not be started
     */
    public ProcessState runCommandDetached(String[] command) throws IOException {
        ArrayList<String> fullCommand = getPlatformCommand();
        fullCommand.add(String.join(" ", command));

        return this.runCmdDetached(fullCommand.toArray(new String[0]));
    }

    /**
     * Runs the system commands in parallel using multi-processes
     *
     * @param commands The system commands to be executed in parallel
     * @return The description of all commands
     * @throws IOException if the process could not be started
     */
    public CommandDescription runCommandsParallel(String[] commands) throws IOException {
        ArrayList<ProcessState> processStates = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<CommandStatus> commandStatuses = new ArrayList<>();
        LinkedHashMap<String, CommandStatus> commandsStatus = new LinkedHashMap();
        CommandDescription commandDescription = CommandDescription.builder()
                .startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .started(true)
                .finished(false)
                .pid(ProcessHandle.current().pid())
                .build();


        for (int i = 0; i < commands.length; i++) {
            commandStatuses.add(new CommandStatus());
            commandStatuses.get(i).setStartedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            processStates.add(this.runCommandDetached(commands[i].split(" ")));
        }

        //start threads that reads the stdout, stderr, pid and others
        for (int i = 0; i < processStates.size(); i++) {
            CommandStatusThread cmdStatusThread = new CommandStatusThread(this, CommandParallel.builder()
                    .commandDescription(commandDescription)
                    .commandStatuses(commandStatuses)
                    .commandsStatus(commandsStatus)
                    .command(commands[i])
                    .processState(processStates.get(i))
                    .threadId(i)
                    .build());
            threads.add(new Thread(cmdStatusThread));
            threads.get(i).start();
        }

        //join threads
        for (int i = 0; i < processStates.size(); i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                log.debug(ExceptionUtils.getStackTrace(e));
            }
        }

        return commandDescription;
    }

    /**
     * @param command      The command to be executed
     * @param processState A reference to a {@link ProcessState}
     * @return The command details of the command executed
     */
    public CommandDetails getCmdDetailsOfProcess(String[] command, ProcessState processState) {
        InputStream inputStream = InputStream.nullInputStream();
        int timeout = environment.getEnvAndVirtualEnv().get(COMMAND_TIMEOUT) != null ?
                Integer.parseInt(environment.getEnvAndVirtualEnv().get(COMMAND_TIMEOUT)) : COMMAND_TIMEOUT_DEFAULT;

        CommandDetails commandDetails;
        try {
            ProcessResult processResult = processState.getProcessResult().get(timeout, TimeUnit.SECONDS);

            int code = processResult.getExitValue();
            String out = processResult.getOutput().getString();
            inputStream = new ByteArrayInputStream(processState.getErrOutputStream().toByteArray());
            String err = IOUtils.toString(inputStream, Charset.defaultCharset());

            commandDetails = CommandDetails.builder()
                    .out(out)
                    .err(err)
                    .code(code)
                    .pid(processState.getProcess().pid())
                    .args(command)
                    .build();

        } catch (TimeoutException e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            commandDetails = CommandDetails.builder()
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(PROCESS_EXCEPTION_TIMEOUT)
                    .args(command)
                    .build();
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            commandDetails = CommandDetails.builder()
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(PROCESS_EXCEPTION_GENERAL)
                    .args(command)
                    .build();
        } finally {
            try {
                processState.closeErrOutputStream();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                log.debug(ExceptionUtils.getStackTrace(e));
            }
        }

        return commandDetails;
    }

    private ArrayList<String> getPlatformCommand() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = new ArrayList<>();

        if (isWindows) {
            fullCommand.add(EXEC_WIN);
            fullCommand.add(ARGS_WIN);
        } else {
            fullCommand.add(EXEC_LINUX);
            fullCommand.add(ARGS_LINUX);

        }

        return fullCommand;
    }

    private ProcessExecutor runStartCmdDetached(String[] command) {
        log.debug("Executing detached: " + Arrays.asList(command).toString());

        return new ProcessExecutor()
                .command(command)
                .environment(environment.getEnvAndVirtualEnv())
                .destroyOnExit()
                .readOutput(true);
    }

    private ProcessState runCmdDetached(String[] command) throws IOException {
        return getProcessState(command);
    }

    private CommandDetails getCommandDetails(String[] command) throws IOException {
        ProcessState pState = getProcessState(command);

        return this.getCmdDetailsOfProcess(command, pState);
    }

    private ProcessState getProcessState(String[] command) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StartedProcess startedProcess = new ProcessExecutor()
                .command(command)
                .environment(environment.getEnvAndVirtualEnv())
                .destroyOnExit()
                .readOutput(true)
                .redirectError(outputStream)
                .start();

        return ProcessState.builder()
                .startedProcess(startedProcess)
                .process(startedProcess.getProcess())
                .processResult(startedProcess.getFuture())
                .errOutputStream(outputStream)
                .build();
    }

    private String doQuoteCmd(String s) {
        return "\"" + s + "\"";
    }
}
