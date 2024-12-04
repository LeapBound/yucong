package com.github.leapbound.yc.hub.utils;

import com.tencentcloudapi.im.*;
import com.tencentcloudapi.im.model.CommonResponse;
import com.tencentcloudapi.im.model.GetRecentContactListGroupGetResponse;
import com.tencentcloudapi.im.model.GetRoamMsgResponse;
import com.tencentcloudapi.im.model.SendSingleChatMsgResponse;
import com.tencentyun.TLSSigAPIv2;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Response;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class MyTimApiClient extends ApiClient {

    @Setter
    @Getter
    private long expire;
    @Setter
    @Getter
    private int retry;
    private String userSig = null;
    private int random;
    private TimApiClientRequest timApiClientRequest;

    @Override
    public List<Pair> buildLocalQueryParams(Integer random) {
        this.random = random;

        List<Pair> localVarQueryParams = new ArrayList<>();
        localVarQueryParams.addAll(this.parameterToPair("random", random));
        localVarQueryParams.addAll(this.parameterToPair("sdkappid", getSdkappid()));
        localVarQueryParams.addAll(this.parameterToPair("identifier", getIdentifier()));
        localVarQueryParams.addAll(this.parameterToPair("contenttype", "json"));
        if (this.userSig == null) {
            TLSSigAPIv2 api = new TLSSigAPIv2((long) getSdkappid(), getKey());
            this.userSig = api.genUserSig(this.getIdentifier(), getExpire());
        }
        localVarQueryParams.addAll(this.parameterToPair("usersig", this.userSig));
        return localVarQueryParams;
    }

    @Override
    public Call buildCall(String baseUrl, String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams, Object body, Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String[] authNames, ApiCallback callback) throws ApiException {
        TimApiClientRequest timApiClientRequest = new TimApiClientRequest();
        timApiClientRequest.setBaseUrl(baseUrl);
        timApiClientRequest.setPath(path);
        timApiClientRequest.setMethod(method);
        timApiClientRequest.setQueryParams(queryParams);
        timApiClientRequest.setCollectionQueryParams(collectionQueryParams);
        timApiClientRequest.setBody(body);
        timApiClientRequest.setHeaderParams(headerParams);
        timApiClientRequest.setCookieParams(cookieParams);
        timApiClientRequest.setFormParams(formParams);
        timApiClientRequest.setAuthNames(authNames);
        timApiClientRequest.setCallback(callback);
        this.timApiClientRequest = timApiClientRequest;

        return buildCall(timApiClientRequest);
    }

    private Call buildCall(TimApiClientRequest timApiClientRequest) throws ApiException {
        return super.buildCall(
                timApiClientRequest.getBaseUrl(),
                timApiClientRequest.getPath(),
                timApiClientRequest.getMethod(),
                timApiClientRequest.getQueryParams(),
                timApiClientRequest.getCollectionQueryParams(),
                timApiClientRequest.getBody(),
                timApiClientRequest.getHeaderParams(),
                timApiClientRequest.getCookieParams(),
                timApiClientRequest.getFormParams(),
                timApiClientRequest.getAuthNames(),
                timApiClientRequest.getCallback()
        );
    }

    @Override
    public <T> ApiResponse<T> execute(Call call, Type returnType) throws ApiException {
        ApiException apiException = new ApiException("init error");

        for (int i = 0; i < this.retry; i++) {
            try {
                Response response = call.execute();
                T data = this.handleResponse(response, returnType);
                ApiResponse<T> apiResponse = new ApiResponse<>(response.code(), response.headers().toMultimap(), data);

                // 处理错误代码
                int errorCode = 0;
                if (data instanceof CommonResponse) {
                    errorCode = ((CommonResponse) data).getErrorCode();
                } else if (data instanceof SendSingleChatMsgResponse) {
                    errorCode = ((SendSingleChatMsgResponse) data).getErrorCode();
                } else if (data instanceof GetRecentContactListGroupGetResponse) {
                    errorCode = ((GetRecentContactListGroupGetResponse) data).getErrorCode();
                } else if (data instanceof GetRoamMsgResponse) {
                    errorCode = ((GetRoamMsgResponse) data).getErrorCode();
                }
                if (errorCode > 0) {
                    treatErrorCode(errorCode);
                    this.timApiClientRequest.setQueryParams(buildLocalQueryParams(this.random));
                    call = buildCall(this.timApiClientRequest);
                    continue;
                }

                return apiResponse;
            } catch (Exception e) {
                log.error("execute error", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    log.error("sleep error", e);
                }
                apiException = new ApiException(e);
                call = call.clone();
            }
        }

        throw apiException;
    }

    private void emptyUserSig() {
        this.userSig = null;
    }

    private void treatErrorCode(int errorCode) {
        log.error("treatErrorCode {}", errorCode);
        if (errorCode == 70001) {
            if (StringUtils.hasText(this.userSig)) {
                emptyUserSig();
            }
        }

        try {
            Thread.sleep(100);
        } catch (Exception e) {
            log.error("treatErrorCode", e);
        }
    }
}
