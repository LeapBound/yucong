package com.github.leapbound.yc.action.controller;

import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionGroovySaveRequest;
import com.github.leapbound.yc.action.model.vo.request.FunctionMethodSaveRequest;
import com.github.leapbound.yc.action.model.vo.request.FunctionTaskRequest;
import com.github.leapbound.yc.action.service.YcFunctionGroovyService;
import com.github.leapbound.yc.action.service.YcFunctionMethodService;
import com.github.leapbound.yc.action.service.YcFunctionOpenaiService;
import com.github.leapbound.yc.action.service.YcFunctionTaskService;
import groovy.util.logging.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yamath
 * @date 2023/7/11 10:05
 */
@Slf4j
@RestController
@RequestMapping("/function")
public class YcFunctionManageController {

    private final YcFunctionMethodService ycFunctionMethodService;
    private final YcFunctionGroovyService ycFunctionGroovyService;
    private final YcFunctionTaskService ycFunctionTaskService;
    private final YcFunctionOpenaiService ycFunctionOpenaiService;

    public YcFunctionManageController(YcFunctionMethodService ycFunctionMethodService,
                                      YcFunctionGroovyService ycFunctionGroovyService,
                                      YcFunctionTaskService ycFunctionTaskService,
                                      YcFunctionOpenaiService ycFunctionOpenaiService) {
        this.ycFunctionMethodService = ycFunctionMethodService;
        this.ycFunctionGroovyService = ycFunctionGroovyService;
        this.ycFunctionTaskService = ycFunctionTaskService;
        this.ycFunctionOpenaiService = ycFunctionOpenaiService;
    }

    @PostMapping("/method/save")
    public ResponseVo<Void> saveFunctionMethod(@RequestBody FunctionMethodSaveRequest request) {
        return this.ycFunctionMethodService.saveFunctionMethod(request);
    }

    @PostMapping("/method/update")
    public ResponseVo<Void> updateFunctionMethod(@RequestBody FunctionMethodSaveRequest request) {
        return this.ycFunctionMethodService.updateFunctionMethod(request);
    }

    @PostMapping("/method/delete")
    public ResponseVo<Void> deleteFunctionMethod(@RequestParam("functionName") String functionName,
                                                 @RequestParam(value = "userName", required = false) String userName) {
        return this.ycFunctionMethodService.deleteFunctionMethod(functionName, userName);
    }

    @PostMapping("/groovy/save")
    public ResponseVo<Void> saveFunctionGroovy(@RequestBody FunctionGroovySaveRequest request) {
        return this.ycFunctionGroovyService.saveFunctionGroovy(request);
    }

    @PostMapping("/groovy/update")
    public ResponseVo<Void> updateFunctionGroovy(@RequestBody FunctionGroovySaveRequest request) {
        return this.ycFunctionGroovyService.updateFunctionGroovy(request);
    }

    @PostMapping("/groovy/delete")
    public ResponseVo<Void> deleteFunctionGroovy(@RequestParam("functionName") String functionName,
                                                 @RequestParam(value = "userName", required = false) String userName) {
        return this.ycFunctionGroovyService.deleteFunctionGroovy(functionName, userName);
    }

    @PostMapping("/groovy/scripts/upload")
    public ResponseVo<Void> uploadFunctionGroovyScripts(@RequestParam("file") MultipartFile file,
                                                        @RequestParam("groovyUrl") String groovyUrl) {
        ResponseVo<Void> vo = this.ycFunctionGroovyService.uploadFunctionGroovyScripts(file, groovyUrl);
        if (vo.isSuccess()) {
            this.ycFunctionOpenaiService.checkCommonEngineMap(file.getOriginalFilename());
        }
        return vo;
    }

    @PostMapping("/task/save")
    public ResponseVo<Void> saveFunctionTask(@RequestBody FunctionTaskRequest request) {
        return this.ycFunctionTaskService.saveFunctionTask(request);
    }

    @PostMapping("/task/update")
    public ResponseVo<Void> updateFunctionTask(@RequestBody FunctionTaskRequest request) {
        return this.ycFunctionTaskService.updateFunctionTask(request);
    }

    @PostMapping("/task/delete")
    public ResponseVo<Void> deleteFunctionTask(@RequestParam("processId") String processId,
                                               @RequestParam("functionName") String functionName,
                                               @RequestParam(value = "taskName", required = false) String taskName,
                                               @RequestParam(value = "userName", required = false) String userName) {
        return this.ycFunctionTaskService.deleteFunctionTask(processId, functionName, taskName, userName);
    }
}
