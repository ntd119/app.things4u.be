package apinexo.core.apis.testing.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestingInternalRequest {

    @JsonProperty("query")
    private String query;
}
