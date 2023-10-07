package yzggy.yucong.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.model.SingleChatModel;
import yzggy.yucong.service.ConversationService;

@Slf4j
@RestController
@RequestMapping("/api/conversation")
@RequiredArgsConstructor
public class ApiConversationController {

    private final ConversationService conversationService;

    @PostMapping("/clear")
    public void clearMsgHistory(@RequestParam String botId, @RequestParam String accountId) {
        this.conversationService.clearMessageHistory(botId, accountId);
    }

    @PostMapping("/chat")
    public String chat(@RequestParam("botId") String botId,
                       @RequestParam("accountId") String accountId,
                       @RequestParam("content") String content) {
        SingleChatModel singleChatModel = new SingleChatModel();
        singleChatModel.setBotId(botId);
        singleChatModel.setAccountId(accountId);
        singleChatModel.setContent(content);
        return this.conversationService.chat(singleChatModel);
    }
}