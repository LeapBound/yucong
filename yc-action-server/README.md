# yc-action-server

### What Is yc-action-server?

`yc-action-server` is a simple backend application. It is for `function-call` definition method implementation.

### Feature Overview

1.`yc-action-server` adopts the java + groovy method. Except for the interface and class definitions, which are all
java, the implementation methods of function-call are all completed by groovy scripts.
The advantage of using a groovy script instead of a class is that the methods in the groovy script can be dynamically
called at any time, so that you can respond to the continuously added methods of the upper-layer service `function-call`
at any time.

2.The project uses `redis-stream` as the message queue

3.Configure using `HTTP Interface Client(WebClient)` to write Http client, similar to Spring Cloud OpenFeign, you only
need to declare the interface to complete the work.

### Start Up

```
1. execute sql， sql/20231016/yucong.sql

2. The configuration file is configured according to your own environment

3. Start Spring
```

### Explanation

1.Table `yc_function_groovy` saves the groovy script name and script storage location corresponding to the function
name. When the application reads the function defined by the `function-call` of the upper-layer service,
Find the groovy script and call it.

2.The data table `yc_function_execute_record` records the records of function calls, including input parameters and
returns.

3.When this application starts, `ApplicationListener` is set, reads the scripts under resources (including all groovy
scripts) and places them at the specified groovy address (`groovy_url` of `yc_function_groovy`).

4.The original java class calling method is also retained in the application.

### How To Use?

1. Save groovy script definition

- Method: **POST**
- URL: ```http://localhost:8180/yc/function/groovy/save```
- Headers: Content-Type:application/json
- Body:

```json
{
  "functionName": "get_current_weather",
  "groovyName": "Weather.groovy",
  "groovyUrl": "/home/scripts/weather/",
  "userName": "yao"
}
```

- Response:

```json
{
  "success": true,
  "code": null,
  "msg": null,
  "data": null
}
```

```json
{
  "success": false,
  "code": null,
  "msg": "data has existed",
  "data": null
}
```

2. Upload groovy script file to groovy url

- Method: **POST**
- URL: ```http://localhost:8180/yc/function/groovy/scripts/upload```
- Headers: Content-Type:multipart/form-data
- Form-data:

```
file@
groovyUrl=/home/scripts/weather/
```

- Response:

```json
{
  "success": true,
  "code": null,
  "msg": null,
  "data": null
}
```

```json
{
  "success": false,
  "code": null,
  "msg": "Groovy scripts not exist in yc_function_groovy, Weather.groovy",
  "data": null
}
```

3.call execute

- Method: **POST**
- URL: ```http://localhost:8180/yc/function/openai/execute```
- Headers:

```
Content-Type:application-json
userName: yao
accountId:
deviceId:
```

- Body:

```json
{
  "name": "get_current_weather",
  "arguments": "{\"location\":\"Los Angeles\"}"
}
```

- Response:

```json
{
  "role": "function",
  "content": "{\"Weather\":\"Sunny\",\"Temperature\":\"32\",\"UV index\":\"5\",\"Wind speed\":\"5m/s\",\"Air Quality Index\":\"30\",\"location\":\"Los Angeles\"}",
  "name": "get_current_weather"
}
```

```json
{
  "role": "function",
  "content": "{\"Error\":\"No location is provided, the user is required to specify the location.\"}",
  "name": "get_current_weather"
}
```