package yucong.camunda.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yamath
 * @since 2023/11/15 11:19
 */
@Slf4j
@Component("userTaskExecution")
public class UserTaskExecutionListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        log.info("variables: {}", delegateExecution.getVariables());
        String currentActivityId = delegateExecution.getCurrentActivityId();
        log.info("current activity id: {}", currentActivityId);
        TaskService taskService = delegateExecution.getProcessEngine().getTaskService();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(delegateExecution.getProcessInstanceId()).list();
        if (taskList != null && !taskList.isEmpty()) {
            Task task = taskList.get(0);
            String taskId = task.getId();
            log.info("current taskId: {}", taskId);
        }
//        runtimeService.setVariablesLocal("", Maps.newHashMap());
    }
}
