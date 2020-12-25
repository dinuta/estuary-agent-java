package com.github.dinuta.estuary.agent.model.api;

import com.github.dinuta.estuary.agent.model.ProcessState;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandParallel {
    @Setter
    @Getter
    private CommandDescription commandDescription;
    @Setter
    @Getter
    private ArrayList<CommandStatus> commandStatuses;
    @Setter
    @Getter
    private LinkedHashMap<String, CommandStatus> commandsStatus;
    @Setter
    @Getter
    private ProcessState processState;
    @Setter
    @Getter
    private String command;
    @Setter
    @Getter
    private int threadId;
}
