package com.github.leapbound.service;

import com.github.leapbound.yc.hub.service.BotService;
import com.github.leapbound.yc.hub.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ManageServiceTests {

    @Autowired
    private UserService userService;
    @Autowired
    private BotService botService;

    @Test
    void getAllBot() {
        log.info("bot {}", this.botService.listAll());
    }
}
