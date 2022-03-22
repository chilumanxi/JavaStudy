package github.chilumanxi.checkParam.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * DspApplicationContextUtils class
 *
 * @author zhanghaoran25
 * @date 2021/10/11 16:53
 */
@Service
public class DspApplicationContextUtils implements ApplicationContextAware{

    private static ApplicationContext applicationContext;

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DspApplicationContextUtils.applicationContext = applicationContext;
    }

}
