package github.chilumanxi.checkParam.valid;

/**
 * ParamValid interface
 *
 * @author zhanghaoran25
 * @date 2021/10/11 16:38
 */
public interface ParamValid<T, EnumOperationTag> {
    Boolean valid(T param, EnumOperationTag operationTag);
}
