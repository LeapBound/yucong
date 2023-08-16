package yzggy.yucong.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import yzggy.yucong.service.ConversationService;

@Slf4j
@RestController
@RequestMapping("/api/conversation")
@RequiredArgsConstructor
public class ApiConversationController {

    private final ConversationService conversationService;

    @PostMapping("/conversation/clear")
    public void clearMsgHistory(@RequestParam String botId, @RequestParam String accountId) {
        this.conversationService.clearMessageHistory(botId, accountId);
    }
}