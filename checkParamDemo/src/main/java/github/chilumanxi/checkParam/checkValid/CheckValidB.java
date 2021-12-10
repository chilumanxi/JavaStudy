package github.chilumanxi.checkParam.checkValid;


import github.chilumanxi.checkParam.domain.entity.EntityA;
import github.chilumanxi.checkParam.domain.enums.EnumOperationTag;
import github.chilumanxi.checkParam.valid.ParamValid;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * CheckValidB class
 *
 * @author zhanghaoran25
 * @date 2021/10/11 16:46
 */
@Service
public class CheckValidB implements ParamValid<EntityA, EnumOperationTag> {
    @Override
    public Boolean valid(EntityA param, EnumOperationTag enumOperationTag) {
        if(Objects.isNull(param.getB())){
            return false;
        }
        return true;
    }
}
