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
        pCmd.getCmdStatuses().get(pCmd.getId()).setDetails(
                commandRunner.getCmdDetailsOfProcess(new String[]{pCmd.getCmd()}, pCmd.getProcessState()));

        pCmd.getCmdsStatus().put(pCmd.getCmd(), pCmd.getCmdStatuses().get(pCmd.getId()));
        pCmd.getCmdStatuses().get(pCmd.getId()).setFinishedat(LocalDateTime.now().format(PATTERN));
        pCmd.getCmdStatuses().get(pCmd.getId()).setDuration(Duration.between(
                LocalDateTime.parse(pCmd.getCmdStatuses().get(pCmd.getId()).getStartedat(), PATTERN),
                LocalDateTime.parse(pCmd.getCmdStatuses().get(pCmd.getId()).getFinishedat(), PATTERN)).toMillis() / DENOMINATOR);
        pCmd.getCmdStatuses().get(pCmd.getId()).setStatus("finished");
        pCmd.getCmdDescription().setCommands(pCmd.getCmdsStatus());
        pCmd.getCmdDescription().setFinishedat(LocalDateTime.now().format(PATTERN));
        pCmd.getCmdDescription().setDuration(Duration.between(
                LocalDateTime.parse(pCmd.getCmdDescription().getStartedat(), PATTERN),
                LocalDateTime.parse(pCmd.getCmdDescription().getFinishedat(), PATTERN)).toMillis() / DENOMINATOR);
        pCmd.getCmdDescription().setFinished(true);
        pCmd.getCmdDescription().setStarted(false);
    }
}
