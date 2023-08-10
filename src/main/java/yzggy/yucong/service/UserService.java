package yzggy.yucong.service;

import java.util.List;

public interface UserService {

    List<String> listAccountByBotName(String botName);

    List<String> getAuthByUserId(String userId);
}
