package com.github.leapbound.yc.hub.service.gpt;

/**
 * @author Fred
 * @date 2024/4/15 13:19
 */
public interface GptHandlerFactory {

    GptHandler getHandler(String type);
}
