package apinexo.core.modules.admin.facade.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.admin.dto.AdminCreateApiRequest;
import apinexo.core.modules.admin.dto.AdminCreateApiRequest.PlanDTO;
import apinexo.core.modules.admin.dto.AdminSitesUpsertRequest;
import apinexo.core.modules.admin.facade.AdminFacade;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.api.service.ApiService;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.plans.service.PlansService;
import apinexo.core.modules.stripe.service.StripeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminFacadeImpl extends AbstractService implements AdminFacade {

    private final ApinexoUtils utils;

    private final StripeService stripeService;

    private final PlansService apiPlansService;

    private final ApiService apiService;

    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Object> createApi(Jwt jwt, AdminCreateApiRequest request) {
        try {
            String email = jwt.getClaimAsString("email");
            if (!"ntd119@gmail.com".equals(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "The user does not have permission to access this"));
            }

            ApiEntity apiEntity = apiService.findbyId(request.getId())
                    .orElseGet(() -> ApiEntity.builder().id(request.getId()).build());
            apiEntity.setName(request.getName());
            apiEntity.setShortDescription(request.getShortDescription());
            apiEntity.setLongDescription(request.getLongDescription());
            apiEntity.setImage(request.getImage());
            apiEntity.setYamlFile(request.getYamlFile());
            apiEntity.setRapidLink(request.getRapidLink());

            List<PlanDTO> planDTOs = request.getPlans();
            List<PlansEntity> plans = apiEntity.getPlans();
            if (CollectionUtils.isEmpty(plans)) {
                plans = new ArrayList<>();
            }
            for (PlanDTO planDTO : planDTOs) {
                PlansEntity findPlans = apiPlansService.findByApi_IdAndKey(request.getId(), planDTO.getKey());
                if (Objects.isNull(findPlans)) {
                    String id = null;
                    if (planDTO.getIsFree()) {
                        id = utils.generateRandomHexString(24);
                    } else {
                        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                        body.add("unit_amount", BigDecimal.valueOf(planDTO.getPrice()).multiply(BigDecimal.valueOf(100))
                                .intValueExact());
                        body.add("product_data[name]", request.getName() + " " + planDTO.getNickname());
                        body.add("nickname", planDTO.getNickname());
                        body.add("metadata[api_id]", request.getId());
                        body.add("metadata[key]", planDTO.getKey());
                        body.add("metadata[is_free]", planDTO.getIsFree());
                        body.add("metadata[is_soft_limit]", planDTO.getMetadata().getIsSoftLimit());
                        body.add("metadata[rate_limit]", planDTO.getMetadata().getRateLimit());
                        body.add("metadata[rate_limit_period]", planDTO.getMetadata().getRateLimitPeriod());
                        body.add("metadata[up_to]", planDTO.getUpTo());
                        JsonNode result = stripeService.createPriceHardLimit(body);
                        id = utils.jsonNodeAt(result, "/id", String.class);
                    }

                    JsonNode metadata = utils.convertDtoToJson(planDTO.getMetadata());
                    ((ObjectNode) metadata).put("key", planDTO.getKey());
                    ((ObjectNode) metadata).put("api_id", request.getId());
                    PlansEntity plansEntity = PlansEntity.builder().id(id).nickname(planDTO.getNickname())
                            .key(planDTO.getKey()).upTo(planDTO.getUpTo()).period(planDTO.getPeriod())
                            .currency(planDTO.getCurrency()).active(planDTO.getActive()).price(planDTO.getPrice())
                            .isFree(planDTO.getIsFree()).metadata(metadata.toPrettyString()).api(apiEntity)
                            .overagePrices(planDTO.getOveragePrices()).index(planDTO.getIndex()).build();
                    plans.add(plansEntity);
                }
            }
            apiEntity.setPlans(plans);
            apiService.save(apiEntity);
            return ResponseEntity.ok("Successful!");
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.convertStrToJson(ex.getResponseBodyAsString()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Object> sitesUpsert(Jwt jwt, AdminSitesUpsertRequest newItem) {
        try {
            String email = jwt.getClaimAsString("email");
            if (!"ntd119@gmail.com".equals(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "The user does not have permission to access this"));
            }

            @SuppressWarnings("unchecked")
            List<LinkedHashMap<String, Object>> rawList = utils.readJsonFile("/data_static/api-config.json",
                    List.class);

            List<AdminSitesUpsertRequest> list = rawList.stream()
                    .map(item -> objectMapper.convertValue(item, AdminSitesUpsertRequest.class))
                    .collect(Collectors.toList());

            Optional<AdminSitesUpsertRequest> existing = list.stream()
                    .filter(i -> i.getId().equalsIgnoreCase(newItem.getId())).findFirst();

            if (existing.isPresent()) {
                // Update
                existing.get().setUrls(newItem.getUrls());
            } else {
                // Add new
                list.add(newItem);
            }
            JsonNode json = utils.convertDtoToJson(list);
            utils.saveToFile(json.toPrettyString(), "/data_static/api-config.json");
            return ResponseEntity.ok(json);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.convertStrToJson(ex.getResponseBodyAsString()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Object> sitesDelete(Jwt jwt, String id) {
        try {
            String email = jwt.getClaimAsString("email");
            if (!"ntd119@gmail.com".equals(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "The user does not have permission to access this"));
            }

            @SuppressWarnings("unchecked")
            List<LinkedHashMap<String, Object>> rawList = utils.readJsonFile("/data_static/api-config.json",
                    List.class);

            List<AdminSitesUpsertRequest> list = rawList.stream()
                    .map(item -> objectMapper.convertValue(item, AdminSitesUpsertRequest.class))
                    .collect(Collectors.toList());

            boolean removed = list.removeIf(item -> item.getId().equalsIgnoreCase(id));

            if (!removed) {
                throw new RuntimeException("ID not found: " + id);
            }

            JsonNode json = utils.convertDtoToJson(list);
            utils.saveToFile(json.toPrettyString(), "/data_static/api-config.json");
            return ResponseEntity.ok(json);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.convertStrToJson(ex.getResponseBodyAsString()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
