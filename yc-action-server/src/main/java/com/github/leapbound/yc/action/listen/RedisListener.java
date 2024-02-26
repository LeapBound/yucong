package com.github.leapbound.yc.action.listen;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import com.github.leapbound.yc.action.config.RedisConfig;
import com.github.leapbound.yc.action.model.vo.request.FunctionExecuteRecordSaveRequest;
import com.github.leapbound.yc.action.service.YcFunctionExecuteRecordService;

/**
 * @author yamath
 * @since 2023/7/14 10:56
 */
@Component
public class RedisListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisListener.class);

    private final YcFunctionExecuteRecordService ycFunctionExecuteRecordService;

    public RedisListener(YcFunctionExecuteRecordService ycFunctionExecuteRecordService) {
        this.ycFunctionExecuteRecordService = ycFunctionExecuteRecordService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // redis topic channel
        String channel = new String(message.getChannel());
        try {
            // save function call record
            if (RedisConfig.REDIS_CHANNEL_TOPIC_FUNCTION_CALL_RECORD.equals(channel)) {
                // do
                FunctionExecuteRecordSaveRequest request = JSON.parseObject(message.getBody(),
                        FunctionExecuteRecordSaveRequest.class);
                this.ycFunctionExecuteRecordService.saveFunctionExecuteRecord(request);
            } else {

            }
        } catch (Exception ex) {
            logger.error("{} 执行失败", channel, ex);
        }
    }
}
