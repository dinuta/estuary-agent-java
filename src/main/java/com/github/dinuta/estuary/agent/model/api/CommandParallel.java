package com.github.dinuta.estuary.agent.model.api;

import com.github.dinuta.estuary.agent.model.ProcessState;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CommandParallel {
    private CommandDescription cmdDescription;
    private ArrayList<CommandStatus> cmdStatuses;
    private LinkedHashMap<String, CommandStatus> cmdsStatus;
    private ProcessState processState;
    private String cmd;
    private int id;

    public CommandParallel commandDescription(CommandDescription commandDescription) {
        this.cmdDescription = commandDescription;
        return this;
    }

    public CommandParallel commandStatuses(ArrayList<CommandStatus> commandStatuses) {
        this.cmdStatuses = commandStatuses;
        return this;
    }

    public CommandParallel commandsStatus(LinkedHashMap<String, CommandStatus> commandsStatus) {
        this.cmdsStatus = commandsStatus;
        return this;
    }

    public CommandParallel process(ProcessState processState) {
        this.processState = processState;
        return this;
    }

    public CommandParallel command(String command) {
        this.cmd = command;
        return this;
    }

    public CommandParallel threadId(int threadId) {
        this.id = threadId;
        return this;
    }

    public CommandDescription getCmdDescription() {
        return cmdDescription;
    }

    public void setCmdDescription(CommandDescription cmdDescription) {
        this.cmdDescription = cmdDescription;
    }

    public ArrayList<CommandStatus> getCmdStatuses() {
        return cmdStatuses;
    }

    public void setCmdStatuses(ArrayList<CommandStatus> cmdStatuses) {
        this.cmdStatuses = cmdStatuses;
    }

    public LinkedHashMap getCmdsStatus() {
        return cmdsStatus;
    }

    public void setCmdsStatus(LinkedHashMap cmdsStatus) {
        this.cmdsStatus = cmdsStatus;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
