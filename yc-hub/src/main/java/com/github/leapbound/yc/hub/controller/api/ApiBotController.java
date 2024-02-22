package com.github.leapbound.yc.hub.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.leapbound.yc.hub.model.BotModel;
import com.github.leapbound.yc.hub.model.R;
import com.github.leapbound.yc.hub.service.BotService;

@Slf4j
@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
public class ApiBotController {

    private final BotService botService;

    @PostMapping("/bot/create")
    public R<String> createBot(@RequestBody BotModel botModel) {
        this.botService.create(botModel);
        return R.ok();
    }
}