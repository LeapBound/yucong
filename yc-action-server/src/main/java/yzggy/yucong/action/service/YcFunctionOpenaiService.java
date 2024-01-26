package yzggy.yucong.action.service;

import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Message;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRequest;

import java.util.List;

/**
 * @author yamath
 * @since 2023/7/12 9:52
 */
public interface YcFunctionOpenaiService {

    List<Functions> getFunctionsForOpenai(List<String> roleIdList);

    Message executeFunctionForOpenai(FunctionExecuteRequest request);

    Message executeGroovyForOpenai(FunctionExecuteRequest request);
}
