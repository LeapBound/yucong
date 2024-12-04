package com.github.leapbound.yc.hub.service.impl;

import com.github.leapbound.yc.hub.consts.TimConsts;
import com.github.leapbound.yc.hub.model.TimDto;
import com.github.leapbound.yc.hub.service.TimService;
import com.github.leapbound.yc.hub.utils.MyTimApiClient;
import com.tencentcloudapi.im.ApiException;
import com.tencentcloudapi.im.api.AccountApi;
import com.tencentcloudapi.im.api.GroupApi;
import com.tencentcloudapi.im.api.SingleChatApi;
import com.tencentcloudapi.im.model.*;
import com.tencentyun.TLSSigAPIv2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
@RequiredArgsConstructor
public class TimServiceImpl implements TimService {

    private final Random random = new Random();
    private final Map<String, MyTimApiClient> timApiClientMap = new ConcurrentHashMap<>();

    @Value("${yucong.tim.appId}")
    private Integer timAppId;
    @Value("${yucong.tim.secretKey}")
    private String timSecretKey;

    @Override
    public TimDto getTimConfig(String tenantId) {
        MyTimApiClient apiClient = getTimApiClient();
        return new TimDto(apiClient.getSdkappid().longValue(), null, null);
    }

    @Override
    public String genUserSig(String tenantId, String userId) {
        MyTimApiClient myTimApiClient = getTimApiClient();

        TLSSigAPIv2 api = new TLSSigAPIv2((long) myTimApiClient.getSdkappid(), myTimApiClient.getKey());
        return api.genUserSig(userId, myTimApiClient.getExpire());
    }

    private MyTimApiClient getTimApiClient() {
        return getTimApiClient("default");
    }

    private MyTimApiClient getTimApiClient(String tenantId) {
        if (this.timApiClientMap.get(tenantId) == null) {
            MyTimApiClient timApiClient = new MyTimApiClient();
            timApiClient.setBasePath(TimConsts.API_URL);
            timApiClient.setIdentifier("administrator");
            timApiClient.setSdkappid(this.timAppId);
            timApiClient.setKey(this.timSecretKey);
            timApiClient.setExpire(86400);
            timApiClient.setRetry(3);

            this.timApiClientMap.putIfAbsent(tenantId, timApiClient);
        }

        return this.timApiClientMap.get(tenantId);
    }

    @Override
    public void createUser(String tenantId, String userId, String nickname, String avatar) {
        AccountApi apiInstance = new AccountApi(getTimApiClient(tenantId));

        AccountImportRequest accountImportRequest = new AccountImportRequest();
        accountImportRequest.setUserID(userId);
        accountImportRequest.setNick(nickname);
        accountImportRequest.setFaceUrl(avatar);

        try {
            CommonResponse result = apiInstance.accountImport(msgRandom(), accountImportRequest);
            log.info("createUser {}", result);
        } catch (Exception e) {
            log.error("Exception when calling AccountApi#accountImport", e);
        }
    }

    @Override
    public void sendMsg(String tenantId, String from, String to, String content) {
        sendMsg(tenantId, from, to, content, null);
    }

    @Override
    public void sendMsg(String tenantId, String from, String to, String content, String desc) {
        log.info("sendMsg {} {} {} {} {}", tenantId, from, to, content, desc);

        SingleChatApi apiInstance = new SingleChatApi(getTimApiClient());

        SendSingleChatMsgRequest sendRequest = new SendSingleChatMsgRequest();
        sendRequest.setFromAccount(from);
        sendRequest.setToAccount(to);
        sendRequest.setMsgRandom(msgRandom());

        try {
            SendSingleChatMsgResponse result = apiInstance.sendmsg(msgRandom(), sendRequest);
            log.info("sendMsg  {}", result);
        } catch (ApiException e) {
            log.error("Exception when calling SingleChatApi#sendmsg", e);
        }
    }

    @Override
    public void sendGroupMsg(String from, String groupId, String content) {
        log.info("sendGroupMsg {} {} {}", from, groupId, content);

        GroupApi apiInstance = new GroupApi(getTimApiClient());

        List<TIMMsgElement> msgBody = new ArrayList<>();
        TIMTextElemMsgContent msgContent = new TIMTextElemMsgContent();
        msgContent.setText(content);
        TIMTextElem textElem = new TIMTextElem();
        textElem.setMsgContent(msgContent);
        TIMMsgElement msgElement = new TIMMsgElement(textElem);
        msgBody.add(msgElement);

        SendGroupMsgRequest sendRequest = new SendGroupMsgRequest();
        sendRequest.setFromAccount(from);
        sendRequest.setGroupId(groupId);
        sendRequest.setMsgBody(msgBody);

        try {
            SendGroupMsgResponse result = apiInstance.sendGroupMsg(msgRandom(), sendRequest);
            log.info("sendGroupMsg  {}", result);
        } catch (ApiException e) {
            log.error("Exception when calling GroupApi#sendGroupMsg", e);
        }

    }

    @Override
    public void createGroup(String groupName) {
        log.info("createGroup {}", groupName);

        GroupApi apiInstance = new GroupApi(getTimApiClient());

        List<CreateGroupRequestMemberListInner> memberList = new ArrayList<>();
        CreateGroupRequestMemberListInner memberListInner = new CreateGroupRequestMemberListInner();
        memberListInner.setMemberAccount("tangxu");
        memberList.add(memberListInner);
        memberListInner = new CreateGroupRequestMemberListInner();
        memberListInner.setMemberAccount("bd");
        memberList.add(memberListInner);
        memberListInner = new CreateGroupRequestMemberListInner();
        memberListInner.setMemberAccount("guye123");
        memberList.add(memberListInner);

        CreateGroupRequest createGroupRequest = new CreateGroupRequest();
        createGroupRequest.setOwnerAccount("administrator");
        createGroupRequest.setType(GroupType.MEETING);
        createGroupRequest.setName(groupName);
        createGroupRequest.setMemberList(memberList);

        try {
            CreateGroupResponse result = apiInstance.createGroup(msgRandom(), createGroupRequest);
            log.info("createGroup {}", result);
        } catch (ApiException e) {
            log.error("Exception when calling GroupApi#createGroup", e);
        }

    }

    private int msgRandom() {
        return this.random.nextInt(9999999);
    }
}
