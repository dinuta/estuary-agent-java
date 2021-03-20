package com.github.dinuta.estuary.agent.model;

import java.util.List;

public class ProcessInfo {

    private String status = "NA";
    private String name;
    private long pid;
    private String username;
    private long parent;
    private List<String> arguments;
    private List<ProcessHandle> children;


    public List<ProcessHandle> getChildren() {
        return children;
    }

    public void setChildren(List<ProcessHandle> children) {
        this.children = children;
    }


    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public ProcessInfo status(String status) {
        this.status = status;
        return this;
    }

    public ProcessInfo name(String name) {
        this.name = name;
        return this;
    }

    public ProcessInfo pid(long pid) {
        this.pid = pid;
        return this;
    }

    public ProcessInfo username(String username) {
        this.username = username;
        return this;
    }

    public ProcessInfo parent(long parent) {
        this.parent = parent;
        return this;
    }

    public ProcessInfo arguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public ProcessInfo children(List<ProcessHandle> children) {
        this.children = children;
        return this;
    }
}
