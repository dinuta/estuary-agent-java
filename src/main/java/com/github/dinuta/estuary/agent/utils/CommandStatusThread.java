package com.github.dinuta.estuary.agent.utils;


import com.github.dinuta.estuary.agent.component.CommandRunner;
import com.github.dinuta.estuary.agent.model.api.CommandParallel;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.github.dinuta.estuary.agent.constants.DateTimeConstants.PATTERN;

public class CommandStatusThread implements Runnable {
    private static final float DENOMINATOR = 1000F;

    private final CommandParallel pCmd;
    private final CommandRunner commandRunner;

    public CommandStatusThread(CommandRunner commandRunner, CommandParallel pCmd) {
        this.commandRunner = commandRunner;
        this.pCmd = pCmd;
    }

    @Override
    public void run() {
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setDetails(
                commandRunner.getCmdDetailsOfProcess(new String[]{pCmd.getCommand()}, pCmd.getProcessState()));

        pCmd.getCommandsStatus().put(pCmd.getCommand(), pCmd.getCommandStatuses().get(pCmd.getThreadId()));
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setFinishedat(LocalDateTime.now().format(PATTERN));
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setDuration(Duration.between(
                LocalDateTime.parse(pCmd.getCommandStatuses().get(pCmd.getThreadId()).getStartedat(), PATTERN),
                LocalDateTime.parse(pCmd.getCommandStatuses().get(pCmd.getThreadId()).getFinishedat(), PATTERN)).toMillis() / DENOMINATOR);
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setStatus("finished");
        pCmd.getCommandDescription().setCommands(pCmd.getCommandsStatus());
        pCmd.getCommandDescription().setFinishedat(LocalDateTime.now().format(PATTERN));
        pCmd.getCommandDescription().setDuration(Duration.between(
                LocalDateTime.parse(pCmd.getCommandDescription().getStartedat(), PATTERN),
                LocalDateTime.parse(pCmd.getCommandDescription().getFinishedat(), PATTERN)).toMillis() / DENOMINATOR);
        pCmd.getCommandDescription().setFinished(true);
        pCmd.getCommandDescription().setStarted(false);
    }
}
