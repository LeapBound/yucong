package scripts.alpha

import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.extra.spring.SpringUtil
import cn.hutool.http.HttpResponse
import com.github.leapbound.yc.action.func.groovy.RequestAuth
import com.github.leapbound.yc.action.func.groovy.RestClient
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate

import java.time.LocalDateTime

/**
 *
 * @author yamath
 * @since 2024/4/3 9:43
 */

@Field static String botLoginPath = '/geex-portal-ng/user/botLogin'
@Field static String REDIS_COOKIE_KEY = 'alpha.cookie.key'
@Field static String EMPLOYEE_NO = 'bot002'
@Field static String USER_NAME = 'bot002'
@Field static String BOT_SALT = 'e77989ed21758e78331b20e477fc5582'
@Field static Logger logger = LoggerFactory.getLogger('scripts.alpha.Alpha');

static def doPostParamsWithLogin(String url, String path, Map<String, Object> params, RequestAuth auth, int retry) {
    HttpResponse response = RestClient.doPostWithParams(url, path, params, auth)
    if (response != null) {
        if (response.status == 302 && retry > 0) {
            RequestAuth retryAuth = setLoginRequestAuthWithoutRedis(url)
            response = doPostParamsWithLogin(url, path, params, retryAuth, retry - 1)
        }
    }
    return response
}

static def doPostFormWithLogin(String url, String path, Map<String, Object> params, RequestAuth auth, int retry) {
    HttpResponse response = RestClient.doPostWithForm(url, path, params, auth)
    if (response != null) {
        if (response.status == 302 && retry > 0) {
            RequestAuth retryAuth = setLoginRequestAuthWithoutRedis(url)
            response = doPostFormWithLogin(url, path, params, retryAuth, retry - 1)
        }
    }
    return response
}

static def doPostBodyWithLogin(String url, String path, Map<String, Object> params, RequestAuth auth, int retry) {
    HttpResponse response = RestClient.doPostWithBody(url, path, params, auth)
    if (response != null) {
        if (response.status == 302 && retry > 0) {
            RequestAuth retryAuth = setLoginRequestAuthWithoutRedis(url)
            response = doPostBodyWithLogin(url, path, params, retryAuth, retry - 1)
        }
    }
    return response
}

static def doGetWithLogin(String url, String path, Map<String, Object> params, RequestAuth auth, int retry) {
    HttpResponse response = RestClient.doGet(url, path, params, auth)
    if (response != null) {
        if (response.status == 302 && retry > 0) {
            RequestAuth retryAuth = setLoginRequestAuthWithoutRedis(url)
            response = doGetWithLogin(url, path, params, retryAuth, retry - 1)
        }
    }
    return response
}

static def loginAlpha(String url) {
    LocalDateTime nowDateTime = LocalDateTime.now()
    int dateHour = nowDateTime.getYear() + (nowDateTime.getMonthValue() - 1) + 5 + nowDateTime.getHour()
    String token = DigestUtil.md5Hex((EMPLOYEE_NO + USER_NAME + dateHour + BOT_SALT).getBytes())
    def params = ['employeeNo': EMPLOYEE_NO, 'username': USER_NAME, 'token': token]
    def response = RestClient.doGet(url, botLoginPath, params, null)
    if (response == null) {
        logger.error('login alpha no response')
        return null
    }
    def cookieList = []
    if (response.isOk()) {
        response.getCookies().each {
            cookieList.add(it.name + '=' + it.value)
        }
    } else {
        logger.error('login alpha error: status: {}, {}', response.getStatus(), response.body())
    }
    if (!cookieList.isEmpty()) {
        StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class)
        stringRedisTemplate.delete(REDIS_COOKIE_KEY)
        stringRedisTemplate.opsForList().rightPushAll(REDIS_COOKIE_KEY, cookieList)
    }
    return cookieList
}

static def setLoginRequestAuthWithoutRedis(String url) {
    List<String> cookieList = loginAlpha(url)
    def addHeaders = ['Cookie': cookieList.join('; ')]
    RequestAuth requestAuth = new RequestAuth(null, null, null, addHeaders)
    return requestAuth
}

static def setLoginRequestAuth(String url) {
    StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class)
    if (!stringRedisTemplate.hasKey(REDIS_COOKIE_KEY)) {
        loginAlpha(url)
    }
    List<String> cookieList = stringRedisTemplate.opsForList().range(REDIS_COOKIE_KEY, 0, -1)
    def addHeaders = ['Cookie': cookieList.join('; ')]
    RequestAuth requestAuth = new RequestAuth(null, null, null, addHeaders)
    return requestAuth
}

