package apinexo.core.modules.admin.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSitesUpsertRequest {

    @JsonProperty("id")
    private String id;

    @JsonProperty("urls")
    private List<String> urls;

    @JsonProperty("secret-header")
    private String secretHeader;
}