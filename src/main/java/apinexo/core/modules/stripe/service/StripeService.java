package apinexo.core.modules.stripe.service;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

public interface StripeService {

    JsonNode createPriceSoftLimit(String apiName, String upTo, String price);

    JsonNode createPriceHardLimit(MultiValueMap<String, Object> body);

    ResponseEntity<Object> cancelSubscription(String subscriptionId);

    ResponseEntity<Object> reportUsage(String subscriptionId, long quantity);
}
