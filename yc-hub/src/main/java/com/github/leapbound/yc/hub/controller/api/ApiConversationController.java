package com.github.leapbound.yc.hub.controller.api;

import com.github.leapbound.yc.hub.model.SingleChatDto;
import com.github.leapbound.yc.hub.model.process.ProcessResponseDto;
import com.github.leapbound.yc.hub.model.wx.WxCpKfDto;
import com.github.leapbound.yc.hub.service.ConversationService;
import com.github.leapbound.yc.hub.service.impl.runnable.NotifyUserRunnable;
import com.github.leapbound.yc.hub.vendor.wx.cp.YcWxCpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/api/conversation")
@RequiredArgsConstructor
public class ApiConversationController {

    private final ConversationService conversationService;
    private final YcWxCpService ycWxCpService;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() - 1,
            Runtime.getRuntime().availableProcessors() * 2,
            5000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(999)
    );

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
        SingleChatDto singleChatDto = responseDto.getData();
        singleChatDto.setBotId("B6aeff8084b134aaeba2d919270f8322a");
        singleChatDto.setAccountId("A95b28a1a41024b5ca1b8053996d24cb5");
        NotifyUserRunnable notifyUserRunnable = new NotifyUserRunnable(this.conversationService, responseDto.getData());
        this.executor.execute(notifyUserRunnable);
    }

    @PostMapping("/servicer/list")
    public void servicerList(@RequestBody ProcessResponseDto<WxCpKfDto> responseDto) {
        WxCpKfDto switchKfDto = responseDto.getData();
        this.ycWxCpService.listCpKfServicer(switchKfDto);
    }

    @PostMapping("/servicer/add")
    public void servicerAdd(@RequestBody ProcessResponseDto<WxCpKfDto> responseDto) {
        WxCpKfDto switchKfDto = responseDto.getData();
        this.ycWxCpService.addCpKfServicer(switchKfDto);
    }

    @PostMapping("/servicer/switch")
    public void switchDealer(@RequestBody ProcessResponseDto<WxCpKfDto> responseDto) {
        WxCpKfDto switchKfDto = responseDto.getData();
        this.ycWxCpService.switchCpKfServicer(switchKfDto);
    }

    @PostMapping("/servicer/switch/group")
    public void switchDealerByGroup(@RequestBody ProcessResponseDto<WxCpKfDto> responseDto) {
        WxCpKfDto switchKfDto = responseDto.getData();
        this.ycWxCpService.switchCpKfServicerByGroupTag(switchKfDto);
    }

    @PostMapping("/servicer/del")
    public void servicerDel(@RequestBody ProcessResponseDto<WxCpKfDto> responseDto) {
        WxCpKfDto switchKfDto = responseDto.getData();
        this.ycWxCpService.switchCpKfServicer(switchKfDto);
    }

}