package yzggy.yucong.action.controller;

import com.alibaba.fastjson.JSONObject;
import com.unfbx.chatgpt.entity.chat.Message;
import geex.architecture.guts.hub.dto.process.ProcessTaskDto;
import geex.architecture.guts.hub.func.loan.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import yzggy.yucong.action.model.vo.request.FunctionExecuteRequest;
import yzggy.yucong.action.service.YcFunctionOpenaiService;

@Slf4j
@RestController
@RequestMapping("/yc/business")
public class YcBusinessController {
    private static final Logger logger = LoggerFactory.getLogger(YcBusinessController.class);

    private final YcFunctionOpenaiService ycFunctionOpenaiService;
    private final LoanService loanService;

    public YcBusinessController(YcFunctionOpenaiService ycFunctionOpenaiService, LoanService loanService) {
        this.ycFunctionOpenaiService = ycFunctionOpenaiService;
        this.loanService = loanService;
    }

    @GetMapping("/task/next")
    public ProcessTaskDto getNextTask(HttpServletRequest httpServletRequest) {
        String accountId = httpServletRequest.getHeader("accountId");
        return this.loanService.queryTask(accountId);
    }

    @GetMapping("/process/config")
    public JSONObject getProcessConfig(HttpServletRequest httpServletRequest) {
        String processInstanceId = httpServletRequest.getHeader("processInstanceId");
        JSONObject processVariable = this.loanService.getProcessVariable(processInstanceId);
        return processVariable.getJSONObject("loanConfig");
    }

    @PostMapping("/execute")
    public Message executeBusiness(@RequestBody FunctionExecuteRequest request) {
        logger.info("business execute request: {}", request);
        return this.ycFunctionOpenaiService.executeFunctionForOpenai(request);
//        return this.ycFunctionOpenaiService.executeGroovyForOpenai(request);
    }

}
