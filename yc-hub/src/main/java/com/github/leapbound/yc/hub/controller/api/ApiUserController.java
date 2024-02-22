package com.github.leapbound.yc.hub.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.leapbound.yc.hub.model.R;
import com.github.leapbound.yc.hub.service.BotService;
import com.github.leapbound.yc.hub.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserService userService;
    private final BotService botService;

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
        return R.ok();
    }
}