package apinexo.core.modules.stripe.service;

import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

public interface StripeService {

    JsonNode createPriceSoftLimit();

    JsonNode createPriceHardLimit(MultiValueMap<String, String> body);
}
