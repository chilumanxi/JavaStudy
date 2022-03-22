

import github.chilumanxi.checkParam.ServerApplication;
import github.chilumanxi.checkParam.checkValid.CheckValidA;
import github.chilumanxi.checkParam.checkValid.CheckValidB;
import github.chilumanxi.checkParam.domain.entity.EntityA;
import github.chilumanxi.checkParam.domain.enums.EnumOperationTag;
import github.chilumanxi.checkParam.excute.ValidLinkExecute;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * test class
 *
 * @author zhanghaoran25
 * @date 2021/10/11 17:29
 */
@SpringBootTest(classes = ServerApplication.class)
public class test {

    @Autowired
    private ValidLinkExecute validLinkExecute;

    @Test
    public void test() throws ClassNotFoundException {
        EntityA a = new EntityA();
        a.setA(1);
        List list = Lists.newArrayList(CheckValidA.class, CheckValidB.class);
        Boolean test = validLinkExecute.execute(list, a, EnumOperationTag.A_TYPE);
        Assertions.assertEquals(false, test);
    }

}
