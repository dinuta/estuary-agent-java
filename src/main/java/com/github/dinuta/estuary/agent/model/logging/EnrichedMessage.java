package com.github.dinuta.estuary.agent.model.logging;


import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrichedMessage {
    public String name;
    public String port;
    public String version;
    public String[] uname;
    public String java;
    public long pid;

    @JsonProperty("level_code")
    public String levelCode;

    public ParentMessage msg;
    public String timestamp;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getUname() {
        return uname;
    }

    public void setUname(String[] uname) {
        this.uname = uname;
    }

    public String getJava() {
        return java;
    }

    public void setJava(String java) {
        this.java = java;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ParentMessage getMsg() {
        return msg;
    }

    public void setMsg(ParentMessage msg) {
        this.msg = msg;
    }
}
