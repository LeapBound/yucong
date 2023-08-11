package yzggy.yucong.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.service.ConversationService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final ConversationService conversationService;

    @PostMapping("/conversation/clear")
    public void clearMsgHistory(@RequestParam String botId, @RequestParam String accountId) {
        this.conversationService.clearMessageHistory(botId, accountId);
    }
}