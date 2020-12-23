package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseCommandDescription {
    @Getter
    @Setter
    @JsonProperty("code")
    private int code;

    @Getter
    @Setter
    @JsonProperty("message")
    private String message = null;

    @Getter
    @Setter
    @JsonProperty("description")
    private CommandDescription description = null;

    @Getter
    @Setter
    @JsonProperty("timestamp")
    private String timestamp = null;

    @Getter
    @Setter
    @JsonProperty("path")
    private String path = null;

    @Getter
    @Setter
    @JsonProperty("name")
    private String name = null;

    @Getter
    @Setter
    @JsonProperty("version")
    private String version = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiResponseCommandDescription apiResponseSuccess = (ApiResponseCommandDescription) o;
        return Objects.equals(this.message, apiResponseSuccess.message) &&
                Objects.equals(this.description, apiResponseSuccess.description) &&
                Objects.equals(this.code, apiResponseSuccess.code) &&
                Objects.equals(this.timestamp, apiResponseSuccess.timestamp) &&
                Objects.equals(this.path, apiResponseSuccess.path) &&
                Objects.equals(this.name, apiResponseSuccess.name) &&
                Objects.equals(this.version, apiResponseSuccess.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, description, path, timestamp, name, version);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    code: ").append(toIndentedString(code));
        sb.append("    message: ").append(toIndentedString(message));
        sb.append("    description: ").append(toIndentedString(description.toString()));
        sb.append("    path: ").append(toIndentedString(path));
        sb.append("    timestamp: ").append(toIndentedString(timestamp));
        sb.append("    name: ").append(toIndentedString(name));
        sb.append("    version: ").append(toIndentedString(version));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

