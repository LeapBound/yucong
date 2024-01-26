package yzggy.yucong.action.controller;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRequest;
import yzggy.yucong.action.service.YcFunctionOpenaiService;

@Slf4j
@RestController
@RequestMapping("/yc/business")
public class YcBusinessController {
    private static final Logger logger = LoggerFactory.getLogger(YcBusinessController.class);

    private final YcFunctionOpenaiService ycFunctionOpenaiService;

    public YcBusinessController(YcFunctionOpenaiService ycFunctionOpenaiService) {
        this.ycFunctionOpenaiService = ycFunctionOpenaiService;
    }

    @PostMapping("/execute")
    public Message executeBusiness(@RequestBody FunctionExecuteRequest request) {
        logger.info("business execute request: {}", request);
        return this.ycFunctionOpenaiService.executeFunctionForOpenai(request);
//        return this.ycFunctionOpenaiService.executeGroovyForOpenai(request);
    }

}
