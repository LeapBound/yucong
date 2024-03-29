package com.github.leapbound.yc.action.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.model.vo.request.FunctionGroovySaveRequest;
import com.github.leapbound.yc.action.model.vo.request.FunctionMethodSaveRequest;
import com.github.leapbound.yc.action.service.YcFunctionGroovyService;
import com.github.leapbound.yc.action.service.YcFunctionMethodService;

/**
 * @author yamath
 * @since 2023/7/11 10:05
 */
@RestController
@RequestMapping("/${yc.hub.context-path:yc-action}/function")
public class YcFunctionManageController {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionManageController.class);
    private final YcFunctionMethodService ycFunctionMethodService;
    private final YcFunctionGroovyService ycFunctionGroovyService;

    public YcFunctionManageController(YcFunctionMethodService ycFunctionMethodService,
                                      YcFunctionGroovyService ycFunctionGroovyService) {
        this.ycFunctionMethodService = ycFunctionMethodService;
        this.ycFunctionGroovyService = ycFunctionGroovyService;
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
        return this.ycFunctionGroovyService.uploadFunctionGroovyScripts(file, groovyUrl);
    }
}
