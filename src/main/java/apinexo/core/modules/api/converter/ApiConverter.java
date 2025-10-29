package apinexo.core.modules.api.converter;

import apinexo.core.modules.api.dto.ApiResponse;
import apinexo.core.modules.api.entity.ApiEntity;

public interface ApiConverter {

    ApiResponse entity2Resposne(ApiEntity entity);
}
