package yzggy.yucong.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.model.R;
import yzggy.yucong.service.BotService;
import yzggy.yucong.service.ConversationService;
import yzggy.yucong.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final ConversationService conversationService;
    private final UserService userService;
    private final BotService botService;

    @PostMapping("/conversation/clear")
    public void clearMsgHistory(@RequestParam String botId, @RequestParam String accountId) {
        this.conversationService.clearMessageHistory(botId, accountId);
    }

    @PostMapping("/account/create")
    public R<String> createAccount(@RequestParam String username,
                                   @RequestParam String accountName,
                                   @RequestParam String botId,
                                   @RequestParam String roleName) {
        Long userNId = this.userService.getUserByUsername(username);
        if (userNId == null) {
            userNId = this.userService.createUser(username);
        }

        Long botNId = this.botService.getBotNIdByBotId(botId);
        if (botNId == null) {
            return R.fail("botId错误");
        }

        Long accountNID = this.userService.getAccountNId(userNId, botNId);
        if (accountNID == null) {
            accountNID = this.userService.createAccount(accountName, userNId, botNId);
        }

        this.userService.addAccountRoleRelation(roleName, accountNID);
        return R.ok("");
    }
}