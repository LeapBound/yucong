package yzggy.yucong.service;

import yzggy.yucong.model.SingleChatModel;

import java.math.BigDecimal;
import java.util.List;

public interface GptService {

    String chat(SingleChatModel singleChatModel);

    String summary(String content);

    List<BigDecimal> embedding(String content);
}
