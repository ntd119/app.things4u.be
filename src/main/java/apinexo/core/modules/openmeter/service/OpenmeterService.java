package apinexo.core.modules.openmeter.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface OpenmeterService {

    void upsertSubject(String key, String displayName);

    JsonNode createCustomer(String id, String name, String description, String email, String subjectKeys);

    void deleteCustomer​(String customerIdOrKey);

    void deleteSubject​(String id);

    JsonNode stripeCheckoutSessions();
}
