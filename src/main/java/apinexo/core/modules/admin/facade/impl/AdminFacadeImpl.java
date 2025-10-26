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
                            body.add("metadata[up_to]", utils.jsonNodeAt(pro, "/up_to", String.class));
                            // JsonNode result = stripeService.createPriceHardLimit(body);
                            JsonNode result = utils.convertStrToJson("{\r\n"
                                    + "  \"id\": \"price_1SMIIdR5bl4Qw3RDB8VkfWj5\",\r\n"
                                    + "  \"object\": \"price\",\r\n" + "  \"active\": true,\r\n"
                                    + "  \"billing_scheme\": \"per_unit\",\r\n" + "  \"created\": 1761440351,\r\n"
                                    + "  \"currency\": \"usd\",\r\n" + "  \"custom_unit_amount\": null,\r\n"
                                    + "  \"livemode\": false,\r\n" + "  \"lookup_key\": null,\r\n"
                                    + "  \"metadata\": {\r\n" + "    \"api_id\": \"jsearch\",\r\n"
                                    + "    \"is_soft_limit\": \"false\",\r\n" + "    \"key\": \"pro\",\r\n"
                                    + "    \"rate_limit\": \"5\",\r\n" + "    \"rate_limit_period\": \"second\",\r\n"
                                    + "    \"up_to\": \"10000\"\r\n" + "  },\r\n" + "  \"nickname\": \"Pro\",\r\n"
                                    + "  \"product\": \"prod_TIu6MUbt4zzDe8\",\r\n" + "  \"recurring\": {\r\n"
                                    + "    \"aggregate_usage\": null,\r\n" + "    \"interval\": \"month\",\r\n"
                                    + "    \"interval_count\": 1,\r\n" + "    \"meter\": null,\r\n"
                                    + "    \"trial_period_days\": null,\r\n" + "    \"usage_type\": \"licensed\"\r\n"
                                    + "  },\r\n" + "  \"tax_behavior\": \"unspecified\",\r\n"
                                    + "  \"tiers_mode\": null,\r\n" + "  \"transform_quantity\": null,\r\n"
                                    + "  \"type\": \"recurring\",\r\n" + "  \"unit_amount\": 1500,\r\n"
                                    + "  \"unit_amount_decimal\": \"1500\"\r\n" + "}");

                            // id
                            String id = utils.jsonNodeAt(result, "/id", String.class);

                            // nickname
                            String nickname = utils.jsonNodeAt(result, "/nickname", String.class);

                            // nickname
                            String key = utils.jsonNodeAt(result, "/metadata/key", String.class);

                            // upTo
                            Long upTo = utils.jsonNodeAt(result, "/metadata/up_to", Long.class);

                            // price
                            Integer price = utils.jsonNodeAt(result, "/unit_amount", Integer.class);

                            // rateLimit
                            String rateLimit = utils.jsonNodeAt(result, "/metadata/rate_limit", String.class);

                            // rateLimitPeriod
                            String rateLimitPeriod = utils.jsonNodeAt(result, "/metadata/rate_limit_period",
                                    String.class);

                            AdminPlanResponse planResponse = AdminPlanResponse.builder().id(id).nickname(nickname)
                                    .key(key).upTo(upTo).period("month").currency("usd").active(true).price(price)
                                    .isFree(false).overagePrices(utils.createList())
                                    .metadata(AdminPlanResponse.Metadata.builder().apiId(apiId.toLowerCase())
                                            .isSoftLimit("false").key(key).privateField("false").rateLimit(rateLimit)
                                            .rateLimitPeriod(rateLimitPeriod).build())
                                    .build();

                            ApiPlansEntity entity = new ApiPlansEntity();
                            entity.setId(apiId.toLowerCase());
                            entity.setBasic(utils.convertDtoToJson(planResponse).toPrettyString());
                            entity.setPro(utils.convertDtoToJson(planResponse).toPrettyString());
                            entity.setUltra(utils.convertDtoToJson(planResponse).toPrettyString());
                            entity.setMega(utils.convertDtoToJson(planResponse).toPrettyString());
                            apiPlansService.save(entity);
                            return ResponseEntity.ok(planResponse);
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
