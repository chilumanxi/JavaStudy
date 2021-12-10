package github.chilumanxi.checkParam;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ServerApplication class
 *
 * @author zhanghaoran25
 * @date 2021/10/11 16:40
 */
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(basePackages = {"github.chilumanxi"})
public class ServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        logger.info("Server start success!");

    }
}
