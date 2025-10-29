package apinexo.core.modules.subscription.converter;

import apinexo.core.modules.api.dto.ApiResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;

public interface SubscriptionConverter {

    ApiResponse entity2Resposne(SubscriptionEntity entity);
}
