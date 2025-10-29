package apinexo.core.modules.subscription.converter.impl;

import org.springframework.stereotype.Component;

import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.plans.converter.PlansConverter;
import apinexo.core.modules.subscription.converter.SubscriptionConverter;
import apinexo.core.modules.subscription.dto.SubscriptionResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionConverterImpl implements SubscriptionConverter {


    private final PlansConverter plansConverter;

    @Override
    public SubscriptionResponse entity2Resposne(ApiEntity entity) {
        return SubscriptionResponse.builder().apiId(entity.getId()).apiName(entity.getName()).build();
    }

    @Override
    public SubscriptionResponse entity2Resposne(SubscriptionEntity entity) {
        SubscriptionResponse subscriptionResponse = this.entity2Resposne(entity.getApi());
        subscriptionResponse.setSubscriptionId(entity.getId());
        subscriptionResponse.setPlan(plansConverter.entity2Resposne(entity.getPlan()));
        return subscriptionResponse;
    }
}
