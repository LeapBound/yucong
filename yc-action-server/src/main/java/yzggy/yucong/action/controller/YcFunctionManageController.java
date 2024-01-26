package yzggy.yucong.action.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yzggy.yucong.action.model.vo.ResponseVo;
import yzggy.yucong.action.model.vo.YcFunctionRoleVo;
import yzggy.yucong.action.model.vo.request.FunctionGroovySaveRequest;
import yzggy.yucong.action.model.vo.request.FunctionMethodSaveRequest;
import yzggy.yucong.action.model.vo.request.FunctionRoleSaveRequest;
import yzggy.yucong.action.service.YcFunctionGroovyService;
import yzggy.yucong.action.service.YcFunctionManageService;
import yzggy.yucong.action.service.YcFunctionMethodService;
import yzggy.yucong.action.service.YcFunctionRoleService;

/**
 * @author yamath
 * @since 2023/7/11 10:05
 */
@RestController
@RequestMapping("/yc/function")
public class YcFunctionManageController {

    private static final Logger logger = LoggerFactory.getLogger(YcFunctionManageController.class);

    private final YcFunctionRoleService ycFunctionRoleService;
    private final YcFunctionManageService ycFunctionManageService;
    private final YcFunctionMethodService ycFunctionMethodService;
    private final YcFunctionGroovyService ycFunctionGroovyService;

    public YcFunctionManageController(YcFunctionRoleService ycFunctionRoleService,
                                      YcFunctionManageService ycFunctionManageService,
                                      YcFunctionMethodService ycFunctionMethodService,
                                      YcFunctionGroovyService ycFunctionGroovyService) {
        this.ycFunctionRoleService = ycFunctionRoleService;
        this.ycFunctionManageService = ycFunctionManageService;
        this.ycFunctionMethodService = ycFunctionMethodService;
        this.ycFunctionGroovyService = ycFunctionGroovyService;
    }

    @PostMapping("/role/save")
    public ResponseVo<Void> saveFunctionRole(@RequestBody FunctionRoleSaveRequest request) {
        return this.ycFunctionRoleService.saveFunctionRole(request);
    }

    @PostMapping("/role/delete")
    public ResponseVo<Void> deleteFunctionRole(@RequestBody YcFunctionRoleVo vo) {
        return this.ycFunctionRoleService.deleteFunctionRole(vo);
    }

    @PostMapping("/manage/save")
    public ResponseVo<Void> saveFunctionManage(@RequestBody FunctionMethodSaveRequest request) {
        return this.ycFunctionManageService.saveFunctionManage(request);
    }

    @PostMapping("/manage/update")
    public ResponseVo<Void> updateFunctionManage(@RequestBody FunctionMethodSaveRequest request) {
        return this.ycFunctionManageService.updateFunctionManage(request);
    }

    @PostMapping("/manage/delete")
    public ResponseVo<Void> deleteFunctionManage(@RequestParam("functionName") String functionName,
                                                 @RequestParam(value = "userName", required = false) String userName) {
        return this.ycFunctionManageService.deleteFunctionManage(functionName, userName);
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
