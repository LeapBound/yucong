package com.github.leapbound.yc.hub.service;


import com.github.leapbound.yc.hub.model.TimDto;

public interface TimService {

    TimDto getTimConfig(String tenantId);

    String genUserSig(String tenantId, String userId);

    void createUser(String tenantId, String userId, String nickname, String avatar);

    void sendMsg(String tenantId, String from, String to, String content);

    void sendMsg(String tenantId, String from, String to, String content, String desc);

    void sendGroupMsg(String from, String groupId, String content);

    void createGroup(String groupId);
}
