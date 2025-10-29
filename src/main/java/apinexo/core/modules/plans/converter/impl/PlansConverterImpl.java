package apinexo.core.modules.plans.converter.impl;

import org.springframework.stereotype.Component;

import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.plans.converter.PlansConverter;
import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.PlansEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlansConverterImpl implements PlansConverter {

    private final ApinexoUtils utils;

    @Override
    public ApiPlansResponse entity2Resposne(PlansEntity entity) {
        return ApiPlansResponse.builder().id(entity.getId()).nickname(entity.getNickname()).key(entity.getKey())
                .upTo(entity.getUpTo()).period(entity.getPeriod()).currency(entity.getCurrency())
                .active(entity.getActive()).price(entity.getPrice()).isFree(entity.getIsFree())
                .overagePrices(utils.createList()).metadata(utils.convertStrToJson(entity.getMetadata())).build();
    }
}
