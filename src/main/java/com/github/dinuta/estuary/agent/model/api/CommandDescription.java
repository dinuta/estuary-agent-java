package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandDescription {
    @Getter
    @Setter
    @JsonProperty("finished")
    private boolean finished;

    @Getter
    @Setter
    @JsonProperty("started")
    private boolean started;

    @Getter
    @Setter
    @Builder.Default
    @JsonProperty("startedat")
    private String startedat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

    @Getter
    @Setter
    @Builder.Default
    @JsonProperty("finishedat")
    private String finishedat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

    @Getter
    @Setter
    @JsonProperty("duration")
    private float duration = 0;

    @Getter
    @Setter
    @JsonProperty("pid")
    private long pid = 0;

    @Getter
    @Setter
    @Builder.Default
    @JsonProperty("id")
    private String id = "none";

    @Getter
    @Setter
    @Builder.Default
    @JsonProperty("commands")
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
