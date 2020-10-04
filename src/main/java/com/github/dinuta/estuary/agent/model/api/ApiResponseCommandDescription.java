package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

public class ApiResponseCommandDescription {
    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("description")
    private CommandDescription description = null;

    @JsonProperty("timestamp")
    private String timestamp = null;

    @JsonProperty("path")
    private String path = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("version")
    private String version = null;

    public ApiResponseCommandDescription code(int code) {
        this.code = code;
        return this;
    }

    public ApiResponseCommandDescription message(String message) {
        this.message = message;
        return this;
    }

    public ApiResponseCommandDescription description(CommandDescription description) {
        this.description = description;
        return this;
    }

    public ApiResponseCommandDescription path(String path) {
        this.path = path;
        return this;
    }

    public ApiResponseCommandDescription timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ApiResponseCommandDescription name(String name) {
        this.name = name;
        return this;
    }

    public ApiResponseCommandDescription version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     **/
    @ApiModelProperty(value = "")
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Get message
     *
     * @return message
     **/
    @ApiModelProperty(value = "")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get description
     *
     * @return description
     **/
    @ApiModelProperty(value = "")
    public CommandDescription getDescription() {
        return description;
    }

    public void setDescription(CommandDescription description) {
        this.description = description;
    }


    /**
     * Get timestamp
     *
     * @return timestamp
     **/
    @ApiModelProperty(value = "")
    @Valid
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get version
     *
     * @return version
     **/
    @ApiModelProperty(value = "")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


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

