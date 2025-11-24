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
        return SubscriptionResponse.builder().apiId(entity.getId()).apiName(entity.getName()).image(entity.getImage())
                .build();
    }

    @Override
    public SubscriptionResponse entity2Resposne(SubscriptionEntity entity) {
        SubscriptionResponse subscriptionResponse = this.entity2Resposne(entity.getApi());
        subscriptionResponse.setSubscriptionId(entity.getId());
        subscriptionResponse.setBillingPeriodFrom(entity.getBillingPeriodFrom());
        subscriptionResponse.setBillingPeriodTo(entity.getBillingPeriodTo());
        subscriptionResponse.setPlan(plansConverter.entity2Resposne(entity.getPlan()));
        subscriptionResponse.setCurrentPlan(entity.getCurrentPlan());
        subscriptionResponse.setQuota(entity.getQuota());
        subscriptionResponse.setPeriod(entity.getPeriod());
        subscriptionResponse.setQuotaUsed(entity.getQuotaUsed());
        subscriptionResponse.setRateLimit(entity.getRateLimit());
        subscriptionResponse.setRateLimitPeriod(entity.getRateLimitPeriod());
        subscriptionResponse.setIsSoftLimit(entity.getIsSoftLimit());
        subscriptionResponse.setOveragePrices(entity.getOveragePrices());
        subscriptionResponse.setPrice(entity.getPrice());
        return subscriptionResponse;
    }
}
