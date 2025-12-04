package apinexo.core.modules.stripe.service;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.exception.StripeException;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;

public interface StripeService {

    JsonNode createPriceSoftLimit(String apiName, String upTo, String price);

    JsonNode createPriceHardLimit(MultiValueMap<String, Object> body);

    ResponseEntity<Object> cancelSubscription(SubscriptionEntity subscription) throws StripeException;

    ResponseEntity<Object> reportUsage(String subscriptionId, long quantity);

    long getCurrentUsage(String subscriptionItemId);
}
