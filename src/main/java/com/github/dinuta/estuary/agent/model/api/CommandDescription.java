package com.github.dinuta.estuary.agent.model.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

@Builder
public class CommandDescription {
    @Getter
    @Setter
    private boolean finished;
    @Getter
    @Setter
    private boolean started;
    @Getter
    @Setter
    private String startedat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    @Getter
    @Setter
    private String finishedat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    @Getter
    @Setter
    private float duration = 0;
    @Getter
    @Setter
    private long pid = 0;
    @Getter
    @Setter
    private String id = "none";
    @Getter
    @Setter
    private LinkedHashMap<String, CommandStatus> commands = new LinkedHashMap<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    finished: ").append(toIndentedString(finished));
        sb.append("    started: ").append(toIndentedString(started));
        sb.append("    startedat: ").append(toIndentedString(startedat));
        sb.append("    finishedat: ").append(toIndentedString(finishedat));
        sb.append("    duration: ").append(toIndentedString(duration));
        sb.append("    pid: ").append(toIndentedString(pid));
        sb.append("    commands: ").append(toIndentedString(commands));
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
