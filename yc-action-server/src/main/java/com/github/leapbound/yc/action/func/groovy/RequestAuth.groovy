package com.github.leapbound.yc.action.func.groovy
/**
 *
 * @author yamath
 * @date 2023/10/9 11:39
 *
 */

class RequestAuth {
    String username
    String password
    String token
    Map<String, String> headers

    RequestAuth(username, password, token, headers) {
        this.username = username
        this.password = password
        this.token = token
        this.headers = headers
    }
}