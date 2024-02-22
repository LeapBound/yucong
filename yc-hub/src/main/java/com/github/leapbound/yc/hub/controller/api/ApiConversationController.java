package com.github.leapbound.yc.hub.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.leapbound.yc.hub.model.SingleChatModel;
import com.github.leapbound.yc.hub.service.ConversationService;

@Slf4j
@RestController
@RequestMapping("/api/conversation")
@RequiredArgsConstructor
public class ApiConversationController {

    private final ConversationService conversationService;

    @PostMapping("/clear")
    public void clearMsgHistory(@RequestBody SingleChatModel chatModel) {
        this.conversationService.clearMessageHistory(chatModel.getBotId(), chatModel.getAccountId());
    }

    @PostMapping("/chat")
    public String chat(@RequestBody SingleChatModel chatModel) {
        return this.conversationService.chat(chatModel);
    }
}