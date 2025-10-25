package apinexo.common.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Response {

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("data")
    private Object data;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("meta")
    private MetaDto meta;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("filters")
    private Object filters;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("message")
    private Object message;

    public Object getData() {
        return data;
    }
}
