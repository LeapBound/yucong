package com.github.leapbound.yc.hub.service.impl.gpt;

import com.github.leapbound.yc.hub.service.gpt.GptHandler;
import com.github.leapbound.yc.hub.service.gpt.GptHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fred
 * @date 2024/4/16 17:41
 */
@Service
@RequiredArgsConstructor
public class DefaultGptHandlerFactory implements GptHandlerFactory {

    private final ApplicationContext context;
    private final Map<String, GptHandler> handlers = new ConcurrentHashMap<>();

    @Override
    public GptHandler getHandler(String type) {
        if (type == null) {
            return null;
        }

        return handlers.computeIfAbsent(type, t -> context.getBean(t, GptHandler.class));
    }
}
