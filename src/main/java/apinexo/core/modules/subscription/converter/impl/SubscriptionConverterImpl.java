package apinexo.core.modules.subscription.converter.impl;

import org.springframework.stereotype.Component;

import apinexo.core.modules.api.converter.ApiConverter;
import apinexo.core.modules.api.dto.ApiResponse;
import apinexo.core.modules.plans.converter.PlansConverter;
import apinexo.core.modules.subscription.converter.SubscriptionConverter;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionConverterImpl implements SubscriptionConverter {

    private final ApiConverter apiConverter;

    private final PlansConverter plansConverter;

    @Override
    public ApiResponse entity2Resposne(SubscriptionEntity entity) {
        ApiResponse apiResponse = apiConverter.entity2Resposne(entity.getApi());
        apiResponse.setPlan(plansConverter.entity2Resposne(entity.getPlan()));
        return apiResponse;
    }
}
