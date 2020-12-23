package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandStatus {
    @JsonProperty("status")
    @Getter
    @Setter
    private String status = null;

    @JsonProperty("details")
    @Getter
    @Setter
    private CommandDetails details = null;

    @JsonProperty("startedat")
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private String startedat = null;

    @JsonProperty("finishedat")
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private String finishedat = null;

    @JsonProperty("duration")
    @Getter
    @Setter
    private float duration = 0;

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
