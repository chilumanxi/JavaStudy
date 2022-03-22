package github.chilumanxi.checkParam.excute;




import github.chilumanxi.checkParam.domain.enums.EnumOperationTag;
import github.chilumanxi.checkParam.util.DspApplicationContextUtils;
import github.chilumanxi.checkParam.valid.ParamValid;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ValidLinkExecute class
 *
 * @author zhanghaoran25
 * @date 2021/10/11 16:48
 */
@Service
public class ValidLinkExecute {

    public static <T> Boolean execute(List<Class<T>> list, Object param, EnumOperationTag type) throws ClassNotFoundException {
        if(list.isEmpty()){
            return true;
        }
        Boolean res = true;
        for (Class<T> tClass : list) {
            ParamValid paramValid = (ParamValid) DspApplicationContextUtils.getContext().getBean(Class.forName(tClass.getName()));
            res = paramValid.valid(param, type);
        }
        return res;
    }
}
