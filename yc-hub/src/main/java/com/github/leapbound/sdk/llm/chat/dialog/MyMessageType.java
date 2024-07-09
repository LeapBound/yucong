package com.github.leapbound.sdk.llm.chat.dialog;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fred
 * @date 2024/6/25 13:28
 */
@Getter
@AllArgsConstructor
public enum MyMessageType {

    TEXT("text"),
    IMAGE("image"),
    VIDEO("video");

    private final String name;
}
