package yzggy.yucong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("yzggy.yucong.mapper")
public class YucongApplication {

    public static void main(String[] args) {
        SpringApplication.run(YucongApplication.class, args);
    }

}
