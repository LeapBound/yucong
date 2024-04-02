package com.github.leapbound.yc.hub.config.gpt;

import com.unfbx.chatgpt.function.KeyStrategyFunction;

import java.util.List;

public class FirstKeyStrategy implements KeyStrategyFunction<List<String>, String> {

    /**
     * 总是使用第一个
     *
     * @param keys 所有key
     * @return 使用的key
     */
    @Override
    public String apply(List<String> keys) {
        return keys.get(0);
    }

}
