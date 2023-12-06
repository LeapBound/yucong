package yzggy.yucong.config.gpt;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.interceptor.DefaultOpenAiAuthInterceptor;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yzggy.yucong.vendor.bce.QianfanApiClient;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
public class GptClientConfig {

    @Value("${api2d.api.base}")
    private String base;
    @Value("${api2d.api.key}")
    private String key;
    @Value("${bce.client.id}")
    private String qianfanClientId;
    @Value("${bce.client.secret}")
    private String qianfanClientSecret;

    @Bean
    public OpenAiClient openAiClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //！！！！千万别再生产或者测试环境打开BODY级别日志！！！！
        //！！！生产或者测试环境建议设置为这三种级别：NONE,BASIC,HEADERS,！！！
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OpenAiResponseInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        return OpenAiClient.builder()
                .apiKey(Collections.singletonList(this.key))
                .keyStrategy(new FirstKeyStrategy())
                .apiHost(this.base)
                .authInterceptor(new DefaultOpenAiAuthInterceptor())
                .okHttpClient(okHttpClient)
                .build();
    }

    @Bean
    public QianfanApiClient qianfanApiClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //！！！！千万别再生产或者测试环境打开BODY级别日志！！！！
        //！！！生产或者测试环境建议设置为这三种级别：NONE,BASIC,HEADERS,！！！
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        return QianfanApiClient.builder()
                .clientId(this.qianfanClientId)
                .clientSecret(this.qianfanClientSecret)
                .okHttpClient(okHttpClient)
                .build();
    }
}
