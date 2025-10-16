package apinexo.common.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ResponseErrors {
    @JsonProperty("errors")
    private Object errors;
}
