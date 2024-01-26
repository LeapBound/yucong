package geex.architecture.guts.camunda.controller;

import geex.architecture.guts.camunda.model.bo.ProcessFunctionSaveRequest;
import geex.architecture.guts.camunda.model.vo.R;
import geex.architecture.guts.camunda.service.FunctionManageService;
import org.springframework.web.bind.annotation.*;

/**
 * @author yamath
 * @since 2023/11/24 13:44
 */
@RestController
@RequestMapping("/manage")
public class ManageController {

    private final FunctionManageService functionManageService;

    public ManageController(FunctionManageService functionManageService) {
        this.functionManageService = functionManageService;
    }

    @PostMapping("/process/function/save")
    public R<?> saveProcessFunction(@RequestBody ProcessFunctionSaveRequest processFunctionSaveRequest) {
        return functionManageService.saveProcessFunctionManage(processFunctionSaveRequest);
    }

    @GetMapping("/process/function/list")
    public R<?> getProcessFunction(@RequestParam("processKey") String processKey) {
        return functionManageService.getProcessFunction(processKey);
    }
}
