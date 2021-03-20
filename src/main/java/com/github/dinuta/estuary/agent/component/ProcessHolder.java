package com.github.dinuta.estuary.agent.component;

import com.github.dinuta.estuary.agent.model.ProcessState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessHolder {
    private Map<ProcessState, String> inMemoryCmdProcessState = new HashMap<>();
    private final int SIZE = 50;

    public void put(String[] command, ProcessState processState) {
        if (inMemoryCmdProcessState.size() <= SIZE)
            inMemoryCmdProcessState.put(processState, joinCommand(command));
    }

    public void clearAll() {
        inMemoryCmdProcessState.clear();
    }

    public Map<ProcessState, String> getAll() {
        return inMemoryCmdProcessState;
    }

    public void remove(ProcessState processState) {
        inMemoryCmdProcessState.remove(processState);
    }

    public Map<String, String> dumpAll() {
        Map<String, String> dumpProcessStateCmd = new HashMap<>();
        inMemoryCmdProcessState.forEach((pState, cmd) -> {
            dumpProcessStateCmd.put(pState.toString(), cmd);
        });
        return dumpProcessStateCmd;
    }

    private String joinCommand(String[] command) {
        return String.join(" ", command);
    }
}
