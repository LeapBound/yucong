package yzggy.yucong.hub

/**
 *
 * @author yamath
 * @since 2024/1/26 16:06
 */
class ProcessTaskDto {

    String processInstanceId;
    String taskId;
    String taskName;
    List<ProcessFormFieldDto> currentInputForm;
    List<Map<String, Object>> taskProperties;
}