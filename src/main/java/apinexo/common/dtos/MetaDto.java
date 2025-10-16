package apinexo.common.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MetaDto {

    @JsonProperty("currentPage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer page;

    @JsonProperty("limit")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer limit;

    @JsonProperty("totalRecords")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalRecords;

    @JsonProperty("totalPage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalPage;

    @JsonProperty("moreData")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean moreData;

    @JsonProperty("message ")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
