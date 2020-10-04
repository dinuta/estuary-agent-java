package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommandStatus {
    @JsonProperty("status")
    private String status = null;

    @JsonProperty("details")
    private CommandDetails details = null;

    @JsonProperty("startedat")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private String startedat = null;

    @JsonProperty("finishedat")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private String finishedat = null;

    @JsonProperty("duration")
    private float duration = 0;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFinishedat() {
        return finishedat;
    }

    public void setFinishedat(String finishedat) {
        this.finishedat = finishedat;
    }

    public String getStartedat() {
        return startedat;
    }

    public void setStartedat(String startedat) {
        this.startedat = startedat;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public CommandDetails getDetails() {
        return details;
    }

    public void setDetails(CommandDetails details) {
        this.details = details;
    }

    public CommandStatus status(String status) {
        this.status = status;
        return this;
    }

    public CommandStatus details(CommandDetails details) {
        this.details = details;
        return this;
    }

    public CommandStatus finishedat(String finishedat) {
        this.finishedat = finishedat;
        return this;
    }

    public CommandStatus startedat(String startedat) {
        this.startedat = startedat;
        return this;
    }

    public CommandStatus duration(float duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    status: ").append(toIndentedString(status));
        sb.append("    details: ").append(toIndentedString(details));
        sb.append("    startedat: ").append(toIndentedString(startedat));
        sb.append("    finishedat: ").append(toIndentedString(finishedat));
        sb.append("    duration: ").append(toIndentedString(duration));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
