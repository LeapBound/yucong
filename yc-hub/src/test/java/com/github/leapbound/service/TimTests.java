package com.github.leapbound.service;

import com.github.leapbound.yc.hub.service.TimService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

/**
 * @author Fred Gu
 * @date 2024-12-03 11:52
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class TimTests {

    @Autowired
    private TimService timService;

    @Test
    void createGroup() {
        this.timService.createGroup("单元测试小组4");
    }

    @Test
    void sendGroupMessage() {
        this.timService.sendGroupMsg("administrator", "@TGS#34FDIHTPT", String.valueOf(new Date()));
    }

}
