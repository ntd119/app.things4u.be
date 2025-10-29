package apinexo.core.modules.subscription.converter;

import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.subscription.dto.SubscriptionResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;

public interface SubscriptionConverter {

    SubscriptionResponse entity2Resposne(SubscriptionEntity entity);

    SubscriptionResponse entity2Resposne(ApiEntity entity);
}
