package apinexo.core.modules.admin.facade.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.admin.facade.AdminFacade;
import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.ApiPlansEntity;
import apinexo.core.modules.plans.service.ApiPlansService;
import apinexo.core.modules.stripe.service.StripeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminFacadeImpl extends AbstractService implements AdminFacade {

    private final ApinexoUtils utils;

    private final StripeService stripeService;

    private final ApiPlansService apiPlansService;

    private static final String PATH_FILE = "data_static/";

    @Override
    public ResponseEntity<Object> createPlans(String apiId) {
        try {
            Optional<ApiPlansEntity> optional = apiPlansService.findByid(apiId.toLowerCase());
            if (optional.isPresent()) {
                ApiPlansEntity entity = optional.get();
                ApiPlansResponse response = ApiPlansResponse.builder().id(entity.getId())
                        .basic(utils.convertStrToJson(entity.getBasic())).pro(utils.convertStrToJson(entity.getPro()))
                        .ultra(utils.convertStrToJson(entity.getUltra())).mega(utils.convertStrToJson(entity.getMega()))
                        .build();
                return ResponseEntity.ok(response);
            } else {
                JsonNode json = utils.readJsonFile(PATH_FILE + "api_plans.json", JsonNode.class);
                json = utils.jsonNodeAt(json, "/" + apiId);
                if (Objects.nonNull(json) && !json.isEmpty()) {
                    List<String> planNames = utils.createList("Pro");
                    for (String planName : planNames) {
                        JsonNode pro = utils.jsonNodeAt(json, "/" + planName);
                        if (Objects.nonNull(pro) && !pro.isEmpty()) {
                            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                            body.add("unit_amount", utils.jsonNodeAt(pro, "/price", String.class));
                            body.add("product_data[name]", apiId);
                            body.add("nickname", planName);
                            body.add("metadata[api_id]", apiId.toLowerCase());
                            body.add("metadata[key]", planName.toLowerCase());
                            body.add("metadata[rate_limit]", utils.jsonNodeAt(pro, "/rate_limit", String.class));
                            body.add("metadata[rate_limit_period]",
                                    utils.jsonNodeAt(pro, "/rate_limit_period", String.class));
                            JsonNode result = stripeService.createPriceHardLimit(body);
                            return ResponseEntity.ok(result);
                        }
                    }
                }
                return ResponseEntity.ok(json);
            }
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.convertStrToJson(ex.getResponseBodyAsString()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }

    }
}
