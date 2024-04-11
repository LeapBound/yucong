package com.github.leapbound.yc.hub.controller.api;

import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.model.process.ProcessResponseDto;
import com.github.leapbound.yc.hub.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/api/conversation")
@RequiredArgsConstructor
public class ApiConversationController {

    private final ConversationService conversationService;

    @PostMapping("/chat")
    public String chat(@RequestBody SingleChatDto chatModel) {
        return this.conversationService.chat(chatModel).getContent();
    }

    @PostMapping("/clear")
    public void clearMsgHistory(@RequestBody SingleChatDto chatModel) {
        this.conversationService.clearMessageHistory(chatModel.getBotId(), chatModel.getAccountId());
    }

    @PostMapping("/notice")
    public void noticeUser(@RequestBody ProcessResponseDto<SingleChatDto> responseDto) {
        log.debug("noticeUser {}", responseDto);
        this.conversationService.notifyUser(responseDto.getData());
    }
}