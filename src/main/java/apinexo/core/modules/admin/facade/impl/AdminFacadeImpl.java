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
import apinexo.core.modules.admin.dto.AdminPlanResponse;
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
            ApiPlansEntity entity = null;
            if (optional.isPresent()) {
                entity = optional.get();
            } else {
                JsonNode json = utils.readJsonFile(PATH_FILE + "api_plans.json", JsonNode.class);
                json = utils.jsonNodeAt(json, "/" + apiId);
                if (Objects.nonNull(json) && !json.isEmpty()) {
                    entity = new ApiPlansEntity();
                    entity.setId(apiId.toLowerCase());
                    List<String> planNames = utils.createList("Basic", "Pro", "Ultra", "Mega");
                    // List<String> planNames = utils.createList("Basic");
                    for (String planName : planNames) {
                        JsonNode planJson = utils.jsonNodeAt(json, "/" + planName);
                        if (Objects.nonNull(planJson) && !planJson.isEmpty()) {
                            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

                            // key
                            String key = planName.toLowerCase();

                            // upTo
                            Long upTo = utils.jsonNodeAt(planJson, "/up_to", Long.class);

                            // price
                            Integer price = utils.jsonNodeAt(planJson, "/price", Integer.class);

                            // is_free
                            Boolean isFree = utils.jsonNodeAt(planJson, "/is_free", Boolean.class);

                            // isSoftLimit
                            String isSoftLimit = utils.jsonNodeAt(planJson, "/is_soft_limit", String.class);

                            // rateLimit
                            String rateLimit = utils.jsonNodeAt(planJson, "/rate_limit", String.class);

                            // rateLimitPeriod
                            String rateLimitPeriod = utils.jsonNodeAt(planJson, "/rate_limit_period", String.class);

                            body.add("unit_amount", String.valueOf(price));
                            body.add("product_data[name]", apiId + " " + planName);
                            body.add("nickname", planName);
                            body.add("metadata[api_id]", apiId.toLowerCase());
                            body.add("metadata[key]", key);
                            body.add("metadata[is_free]", String.valueOf(isFree));
                            body.add("metadata[is_soft_limit]", String.valueOf(isSoftLimit));
                            body.add("metadata[rate_limit]", rateLimit);
                            body.add("metadata[rate_limit_period]", rateLimitPeriod);
                            body.add("metadata[up_to]", String.valueOf(upTo));
                            JsonNode result = null;
                            AdminPlanResponse planResponse = null;
                            String id = null;
                            if ("Basic".equalsIgnoreCase(planName)) {
                                id = utils.generateRandomHexString(24);
                            } else {
                                result = stripeService.createPriceHardLimit(body);
                                // id
                                id = utils.jsonNodeAt(result, "/id", String.class);
                            }

                            planResponse = AdminPlanResponse.builder().id(id).nickname(planName).key(key).upTo(upTo)
                                    .period("month").currency("usd").active(true).price(price).isFree(isFree)
                                    .overagePrices(utils.createList())
                                    .metadata(AdminPlanResponse.Metadata.builder().apiId(apiId.toLowerCase())
                                            .isSoftLimit(isSoftLimit).key(key).privateField("false")
                                            .rateLimit(rateLimit).rateLimitPeriod(rateLimitPeriod).build())
                                    .build();
                            if ("Basic".equalsIgnoreCase(planName)) {
                                entity.setBasic(utils.convertDtoToJson(planResponse).toPrettyString());
                            } else if ("Pro".equalsIgnoreCase(planName)) {
                                entity.setPro(utils.convertDtoToJson(planResponse).toPrettyString());
                            } else if ("Ultra".equalsIgnoreCase(planName)) {
                                entity.setUltra(utils.convertDtoToJson(planResponse).toPrettyString());
                            } else if ("Mega".equalsIgnoreCase(planName)) {
                                entity.setMega(utils.convertDtoToJson(planResponse).toPrettyString());
                            }
                        }
                    }
                    apiPlansService.save(entity);
                }
            }
            ApiPlansResponse response = ApiPlansResponse.builder().id(entity.getId())
                    .basic(utils.convertStrToJson(entity.getBasic())).pro(utils.convertStrToJson(entity.getPro()))
                    .ultra(utils.convertStrToJson(entity.getUltra())).mega(utils.convertStrToJson(entity.getMega()))
                    .build();
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.convertStrToJson(ex.getResponseBodyAsString()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
