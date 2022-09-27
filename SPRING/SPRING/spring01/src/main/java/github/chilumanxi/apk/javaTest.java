package github.chilumanxi.apk;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * javaTest class
 *
 * @author zhanghaoran25
 * @date 2022/7/13 16:52
 */
public class javaTest {

    public static void main(String[] args){
        Map<String, String> mp = new HashMap<>();
        mp.put("test1", "test");
        System.out.println(JSON.toJSONString(mp));

        Obj a = new Obj();
        a.setTest1("test");
        System.out.println(JSON.toJSONString(a));
    }

    private static class Obj {
        private String test1;

        public String getTest1() {
            return test1;
        }

        public void setTest1(String test1) {
            this.test1 = test1;
        }
    }
}