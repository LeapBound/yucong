package com.github.leapbound.yc.hub.utils;

import com.tencentcloudapi.im.ApiCallback;
import com.tencentcloudapi.im.Pair;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TimApiClientRequest {

    String baseUrl;
    String path;
    String method;
    List<Pair> queryParams;
    List<Pair> collectionQueryParams;
    Object body;
    Map<String, String> headerParams;
    Map<String, String> cookieParams;
    Map<String, Object> formParams;
    String[] authNames;
    ApiCallback callback;
}
