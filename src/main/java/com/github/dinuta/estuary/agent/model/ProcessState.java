package com.github.dinuta.estuary.agent.model;

import lombok.*;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessState {
    @Setter
    @Getter
    private StartedProcess startedProcess;
    @Setter
    @Getter
    private Process process;
    @Setter
    @Getter
    private Future<ProcessResult> processResult;
    @Setter
    @Getter
    private ByteArrayOutputStream errOutputStream;

    public void closeErrOutputStream() throws IOException {
        if (errOutputStream != null) errOutputStream.close();
    }
}
