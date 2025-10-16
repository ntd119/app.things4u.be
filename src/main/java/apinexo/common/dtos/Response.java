package apinexo.common.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Response {

    @JsonProperty("data")
    private Object data;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("meta")
    private MetaDto meta;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("filters")
    private Object filters;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("errors")
    private Object errors;

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("message")
    private String message;

    public Response() {
        this.message = "Successful";
        this.status = true;
    }

    public Object getData() {
        return data;
    }
}
