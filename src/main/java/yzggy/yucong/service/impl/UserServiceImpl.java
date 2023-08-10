package yzggy.yucong.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public List<String> listAccountByBotName(String botName) {
        return null;
    }

    @Override
    public List<String> getAuthByUserId(String userId) {
        return null;
    }
}
