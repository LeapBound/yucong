package com.github.leapbound.yc.hub.controller.api;

import com.github.leapbound.yc.hub.model.R;
import com.github.leapbound.yc.hub.service.BotService;
import com.github.leapbound.yc.hub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/${yc.hub.context-path:yc-hub}/api/user")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserService userService;
    private final BotService botService;

    @PostMapping("/account/create")
    public R<String> createAccount(@RequestParam String username,
                                   @RequestParam String accountName,
                                   @RequestParam String botId,
                                   @RequestParam String roleName) {
        String userId = this.userService.getUserByUsername(username);
        if (userId == null) {
            userId = this.userService.createUser(username);
        }

        String accountID = this.userService.getAccountId(userId, botId);
        if (accountID == null) {
            accountID = this.userService.createAccount(accountName, userId, botId);
        }

        this.userService.addAccountRoleRelation(roleName, accountID);
        return R.ok();
    }
}