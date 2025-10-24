package apinexo.core.modules.openmeter.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface OpenmeterService {

    void upsertSubject(String key, String displayName);

    JsonNode stripeCheckoutSessions();
}
