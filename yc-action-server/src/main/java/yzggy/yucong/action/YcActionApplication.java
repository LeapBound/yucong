package yzggy.yucong.action;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yamath
 * @since 2023/7/11 9:34
 */
@SpringBootApplication(scanBasePackages = {"yzggy.yucong.action", "geex.architecture.guts.hub.func", "geex.architecture.guts.hub.config"})
public class YcActionApplication {

    public static void main(String[] args) {
        SpringApplication.run(YcActionApplication.class);
    }
}
