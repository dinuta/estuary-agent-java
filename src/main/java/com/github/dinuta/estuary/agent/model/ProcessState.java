package com.github.dinuta.estuary.agent.model;

import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;

public class ProcessState {
    private StartedProcess startedProcess;
    private Process process;
    private Future<ProcessResult> processResult;
    private ByteArrayOutputStream errOutputStream;


    public ProcessState errOutputStream(ByteArrayOutputStream errOutputStream) {
        this.errOutputStream = errOutputStream;
        return this;
    }

    public ProcessState startedProcess(StartedProcess startedProcess) {
        this.startedProcess = startedProcess;
        return this;
    }

    public ProcessState process(Process process) {
        this.process = process;
        return this;
    }

    public ProcessState processResult(Future<ProcessResult> processResult) {
        this.processResult = processResult;
        return this;
    }

    public void closeErrOutputStream() throws IOException {
        if (errOutputStream != null) errOutputStream.close();
    }

    public Future<ProcessResult> getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Future<ProcessResult> processResult) {
        this.processResult = processResult;
    }

    public StartedProcess getStartedProcess() {
        return startedProcess;
    }

    public void setStartedProcess(StartedProcess processExecutor) {
        this.startedProcess = processExecutor;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public ByteArrayOutputStream getErrOutputStream() {
        return errOutputStream;
    }

    public void setErrOutputStream(ByteArrayOutputStream errOutputStream) {
        this.errOutputStream = errOutputStream;
    }
}
