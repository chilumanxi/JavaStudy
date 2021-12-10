package github.chilumanxi.checkParam.checkValid;


import github.chilumanxi.checkParam.domain.entity.EntityA;
import github.chilumanxi.checkParam.domain.enums.EnumOperationTag;
import github.chilumanxi.checkParam.valid.ParamValid;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * CheckValidA class
 *
 * @author zhanghaoran25
 * @date 2021/10/11 16:31
 */
@Service
public class CheckValidA implements ParamValid<EntityA, EnumOperationTag> {

    @Override
    public Boolean valid(EntityA param, EnumOperationTag enumOperationTag) {
        if(Objects.isNull(param.getA())){
            return false;
        }
        return true;
    }
}
