package apinexo.core.modules.stripe.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface StripeService {

    JsonNode createPriceSoftLimit();

    JsonNode createPriceHardLimit();
}
