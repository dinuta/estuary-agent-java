package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandDetails {

    @Getter
    @Setter
    @Builder.Default
    @JsonProperty("out")
    private String out = "";

    @Getter
    @Setter
    @Builder.Default
    @JsonProperty("err")
    private String err = "";

    @Getter
    @Setter
    @JsonProperty("code")
    private long code;

    @Getter
    @Setter
    @JsonProperty("pid")
    private long pid;

    @Getter
    @Setter
    @JsonProperty("args")
    private String[] args;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String arguments = (args != null) ? String.join(" ", args) : String.valueOf(args);

        sb.append("{\n");

        sb.append("    out: ").append(toIndentedString(out));
        sb.append("    err: ").append(toIndentedString(err));
        sb.append("    code: ").append(toIndentedString(code));
        sb.append("    pid: ").append(toIndentedString(pid));
        sb.append("    args: ").append(toIndentedString(arguments));
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
