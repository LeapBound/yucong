package yzggy.yucong.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.model.BotModel;
import yzggy.yucong.model.R;
import yzggy.yucong.service.BotService;

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