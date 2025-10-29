package apinexo.core.modules.plans.converter;

import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.PlansEntity;

public interface PlansConverter {

    ApiPlansResponse entity2Resposne(PlansEntity entity);

}
