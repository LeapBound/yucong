package yzggy.yucong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class YucongApplication {

    public static void main(String[] args) {
        SpringApplication.run(YucongApplication.class, args);
    }

}
