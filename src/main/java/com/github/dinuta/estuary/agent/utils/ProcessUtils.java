package com.github.dinuta.estuary.agent.utils;

import com.github.dinuta.estuary.agent.model.ProcessInfo;
import com.github.dinuta.estuary.agent.model.ProcessState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ProcessUtils {
    private static final Logger log = LoggerFactory.getLogger(ProcessUtils.class);

    private static String EXEC = "runcmd";


    public static List<ProcessInfo> getProcesses(boolean addChildren) {
        List customProcessInfoList = new ArrayList();
        ProcessHandle.allProcesses().forEach(p -> {
            long parent = -1L;
            List<String> arguments = new ArrayList<>();

            try {
                parent = p.parent().get().pid();
            } catch (Exception e) {
                //try at best
            }
            try {
                arguments = Arrays.asList(p.info().arguments().get());
            } catch (Exception e) {
                //try at best
            }

            ProcessInfo processInfo = new ProcessInfo()
                    .status("NA")
                    .name(p.info().command().orElse(""))
                    .pid(p.pid())
                    .username(p.info().user().orElse(""))
                    .parent(parent)
                    .arguments(arguments);

            if (addChildren) processInfo.setChildren(p.children().collect(Collectors.toList()));

            customProcessInfoList.add(processInfo);
        });

        return customProcessInfoList;
    }

    @NotNull
    public static List<ProcessInfo> getProcessInfoForPid(Long pid, boolean addChildren) {
        List<ProcessInfo> processInfoList = getProcesses(addChildren).stream().filter(elem ->
                elem.getPid() == pid).collect(Collectors.toList());

        return processInfoList;
    }

    @NotNull
    public static List<ProcessInfo> getProcessInfoForPidAndParent(Long pid, boolean addChildren) {
        List<ProcessInfo> processInfoList = ProcessUtils.getProcessInfoForPid(pid, addChildren);
        if (processInfoList.size() == 1)
            processInfoList.add(ProcessUtils.getProcessInfoForPid(processInfoList.get(0).getParent(), addChildren).get(0));

        return processInfoList;
    }

    @NotNull
    public static List<ProcessInfo> getProcessInfoForExec(String exec) {
        List<ProcessInfo> backgroundCmdProcessInfo = getProcesses(false).stream().filter(elem ->
                elem.getName().contains(exec)).collect(Collectors.toList());

        return backgroundCmdProcessInfo;
    }

    /**
     * @param childrenProcesses A list of children
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static void killChildrenProcesses(@NotNull List<ProcessHandle> childrenProcesses) throws IOException, InterruptedException, TimeoutException {
        for (int i = 0; i < childrenProcesses.size(); i++) {
            if (childrenProcesses.get(i).children().collect(Collectors.toList()).size() > 0)
                killChildrenProcesses(childrenProcesses.get(i).children().collect(Collectors.toList()));
            log.debug("Killing child PID " + (int) childrenProcesses.get(i).pid() + " is alive: " + childrenProcesses.get(i).isAlive());
            killProcess(childrenProcesses.get(i));
        }
    }

    @NotNull
    public static void killProcess(ProcessInfo processInfo) throws IOException, InterruptedException, TimeoutException {
        PidProcess process = Processes.newPidProcess((int) processInfo.getPid());

        log.debug("Process PID " + (int) processInfo.getPid() + " is alive: " + process.isAlive());
        if (process.isAlive() && (int) processInfo.getPid() != 0)
            ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 2, TimeUnit.SECONDS, 3, TimeUnit.SECONDS);
        log.debug("Process PID " + (int) processInfo.getPid() + " is alive: " + process.isAlive());
    }

    public static void killProcess(ProcessHandle processHandle) throws IOException, InterruptedException, TimeoutException {
        PidProcess process = Processes.newPidProcess((int) processHandle.pid());

        log.debug("Process PID " + (int) processHandle.pid() + " is alive: " + process.isAlive());
        if (process.isAlive() && (int) processHandle.pid() != 0)
            ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 2, TimeUnit.SECONDS, 3, TimeUnit.SECONDS);
        log.debug("Process PID " + (int) processHandle.pid() + " is alive: " + process.isAlive());
    }

    public static void killProcessAndChildren(ProcessState processState) throws InterruptedException, TimeoutException, IOException {
        @NotNull List<ProcessInfo> processInfoList = getProcessInfoForPid(processState.getProcess().pid(), true);
        List<ProcessHandle> children = processInfoList.get(0).getChildren();
        if (children != null) {
            ProcessUtils.killChildrenProcesses(children);
        }
        ProcessUtils.killProcess(processInfoList.get(0));
    }
}
