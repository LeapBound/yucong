package com.github.leapbound.yc.action.func.groovy

import cn.hutool.core.net.url.UrlBuilder
import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author yamath
 * @since 2023/10/9 16:30
 *
 */
class RestClient {
    static Logger log = LoggerFactory.getLogger(RestClient.class);

    /**
     * http request get
     * @param url
     * @param path
     * @param params
     * @param auth
     * @return
     */
    static def doGet(String url, String path, Map<String, Object> params, RequestAuth auth) {
        // url
        def urlString = buildUrl(url, path, params)
        // create get request
        def request = HttpUtil.createGet(urlString)
                .contentType("application/json")
                .setConnectionTimeout(10000).setReadTimeout(10000)
        // check auth
        request = authType(request, auth)
        // http request
        try (def response = request.execute()) {
            if (response != null && !StrUtil.isBlankIfStr(response.body())) {
                log.info("execute doGet {} and response = {}", path, response.body());
            }
            return response
        } catch (Exception ex) {
            log.error('post url {} error', url + path, ex)
        }
        return null
    }

    /**
     * http request post with params
     * @param url
     * @param path
     * @param params
     * @param auth
     * @return
     */
    static def doPostWithParams(String url, String path, Map<String, Object> params, RequestAuth auth) {
        // url
        def urlString = buildUrl(url, path, params)
        // create get request
        def request = HttpUtil.createPost(urlString)
                .contentType("application/json")
                .setConnectionTimeout(10000).setReadTimeout(10000)
        // check auth
        request = authType(request, auth)
        try (def response = request.execute()) {
            if (response != null && !StrUtil.isBlankIfStr(response.body())) {
                log.info("execute doPostWithParams {} and response = {}", path, response.body());
            }
            return response
        } catch (Exception ex) {
            log.error('post url {} error', url + path, ex)
        }
        return null;
    }

    /**
     * http request post with form
     * @param url
     * @param path
     * @param params
     * @param auth
     * @return
     */
    static def doPostWithForm(String url, String path, Map<String, Object> params, RequestAuth auth) {
        // url
        def urlString = buildUrl(url, path, null)
        // create get request
        def request = HttpUtil.createPost(urlString)
                .contentType("application/json")
                .form(params)
                .setConnectionTimeout(10000).setReadTimeout(10000)
        // check auth
        request = authType(request, auth)
        try (def response = request.execute()) {
            if (response != null && !StrUtil.isBlankIfStr(response.body())) {
                log.info("execute doPostWithForm {} and response = {}", path, response.body());
            }
            return response
        } catch (Exception ex) {
            log.error('post url {} error', url + path, ex)
        }
        return null;
    }

    /**
     * http request post with body
     * @param url
     * @param path
     * @param params
     * @param auth
     * @return
     */
    static def doPostWithBody(String url, String path, Map<String, Object> params, RequestAuth auth) {
        // url
        def urlString = buildUrl(url, path, null)
        // create get request
        def request = HttpUtil.createPost(urlString)
                .contentType("application/json")
                .body(JSON.toJSONString(params))
                .setConnectionTimeout(10000).setReadTimeout(10000)
        // check auth
        request = authType(request, auth)
        try (def response = request.execute()) {
            if (response != null && !StrUtil.isBlankIfStr(response.body())) {
                log.info("execute doPostWithBody {} and response = {}", path, response.body());
            }
            return response
        } catch (Exception ex) {
            log.error('post url {} error', url + path, ex)
        }
        return null;
    }

    /**
     * build url with request params
     * @param url
     * @param path
     * @param params
     * @return
     */
    static def buildUrl(String url, String path, Map<String, Object> params) {
        def urlBuilder = UrlBuilder.of(url + path)
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                urlBuilder.addQuery(key, params.get(key))
            }
        }
        return urlBuilder.build();
    }

    /**
     * http request set auth
     * @param request
     * @param auth
     * @return
     */
    static def authType(HttpRequest request, RequestAuth auth) {
        if (auth == null) {
            return request
        }
        def username = auth.username
        def password = auth.password
        def token = auth.token
        //
        if (StrUtil.isBlankIfStr(username) && !StrUtil.isBlankIfStr(token)) {
            return request.bearerAuth(token)
        }
        //
        if (!StrUtil.isBlankIfStr(username)) {
            if (!StrUtil.isBlankIfStr(password)) {
                return request.basicAuth(username, password)
            }
            if (!StrUtil.isBlankIfStr(token)) {
                return request.basicAuth(username, token)
            }
        }
        return request
    }
}

