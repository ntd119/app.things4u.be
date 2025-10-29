package apinexo.core.modules.api.converter.impl;

import org.springframework.stereotype.Component;

import apinexo.core.modules.api.converter.ApiConverter;
import apinexo.core.modules.api.dto.ApiResponse;
import apinexo.core.modules.api.entity.ApiEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiConverterImpl implements ApiConverter {
    
    @Override
    public ApiResponse entity2Resposne(ApiEntity entity) {
        return ApiResponse.builder().apiId(entity.getId()).build();
    }
}
