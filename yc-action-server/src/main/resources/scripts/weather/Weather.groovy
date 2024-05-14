package scripts.weather

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.leapbound.yc.action.func.groovy.GeneralMethods

/**
 *
 * @author yamath
 * @since 2023/12/8 9:35
 */

static def execWeatherMethod(String method, String arguments) {
    JSONObject result = new JSONObject()
    //
    if (arguments == null || arguments == '') {
        result.put('错误', '没有提供必要的信息')
        return result
    }
    switch (method) {
        case 'get_day_weather_forecast':
            result = getDayWeatherForecast(arguments)
            break
        case 'get_current_weather':
            result = getCurrentWeather(arguments)
            break
        default:
            result.put('结果', '没有执行方法')
            break
            return result
    }
}

static def getCurrentWeather(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String location = ''
    String format = '摄氏度'
    //
    if (args.containsKey('location')) {
        location = args.getString('location')
    }
    if (args.containsKey('format')) {
        format = args.getString('format')
    }
    JSONObject result = new JSONObject()
    if (StrUtil.isEmptyIfStr(location)) {
        result.put('错误', '没有提供 location，要求用户明确 location.')
        return GeneralMethods.makeResponseVo(true, null, result)
    }
    result.put('location', location)
    result.put('天气', '晴朗')
    result.put('温度', '32')
    result.put('空气质量指数', '30')
    result.put('紫外线指数', '5')
    result.put('风速', '5m/s')
    return GeneralMethods.makeResponseVo(true, null, result)
}

static def getDayWeatherForecast(String arguments) {
    JSONObject args = JSON.parseObject(arguments)
    String location = ''
    String format = '摄氏度'
    Integer days = 1
    if (args.containsKey('location')) {
        location = args.getString('location')
    }
    if (args.containsKey('format')) {
        format = args.getString('format')
    }
    if (args.containsKey('num_days')) {
        days = args.getInteger('num_days')
    }
    JSONObject result = new JSONObject()
    if (StrUtil.isEmptyIfStr(location)) {
        result.put('错误', '没有提供 location，要求用户明确 location.')
        return GeneralMethods.makeResponseVo(true, null, result)
    }
    result.put('location', location)
    result.put('第一天', '天气: 晴朗, 温度: 23-35, 空气质量指数: 60')
    result.put('第二天', '天气: 小雨转大雨, 温度: 20-28')
    result.put('第三天', '天气: 雷暴, 温度: 10-16')
    return GeneralMethods.makeResponseVo(true, null, result)
}

execWeatherMethod(method, arguments)