package yzggy.yucong.hub

import com.alibaba.fastjson.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

/**
 *
 * @author yamath
 * @since 2024/1/26 15:53
 */

class LoanService {

    static Logger log = LoggerFactory.getLogger(LoanService.class);

    static String flowEngineUrl = 'http://yc-process'

    static ProcessTaskDto queryTask(String userId) {
        try {
            JSONObject query = new JSONObject();
            query.put("businessKey", userId);

            JSONObject startResponse = sendObject(flowEngineUrl + "/geex-guts-camunda/business/task", query, HttpMethod.POST);
            log.info("queryTask response {}", startResponse);

            return startResponse.getObject("data", ProcessTaskDto.class);
        } catch (Exception ex) {
            log.error("queryTask", ex);
        }
        return null
    }

    static JSONObject getProcessVariable(String instanceId) {
        ProcessDto processDto = new ProcessDto();
        processDto.setProcessInstanceId(instanceId);

        JSONObject responseDto = sendObject(flowEngineUrl + "/geex-guts-camunda/business/process/variables", processDto, HttpMethod.POST);
        log.debug("getProcessVariable {}", responseDto);

        return responseDto.getJSONObject("data");
    }

    static <T> JSONObject sendObject(String url, T body, HttpMethod method) {
        HttpHeaders requestHeaders = new HttpHeaders();
        if (body instanceof MultiValueMap) {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        } else {
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        HttpEntity<T> requestEntity = new HttpEntity<>(body, requestHeaders);

        ResponseEntity<JSONObject> entity = new RestTemplate().exchange(url, method, requestEntity, JSONObject.class);
        return entity.getBody();
    }
}