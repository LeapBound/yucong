package yzggy.yucong.bce;

import cn.hutool.core.util.StrUtil;
import io.reactivex.Single;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import yzggy.yucong.bce.entity.ChatCompletion;
import yzggy.yucong.bce.entity.ChatCompletionResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QianfanApiClient {
    @Getter
    @NotNull
    private String clientId;
    @Getter
    @NotNull
    private String clientSecret;
    @Getter
    private String grantType;
    /**
     * 自定义api host使用builder的方式构造client
     */
    @Getter
    private String apiHost;
    @Getter
    private QianfanApi qianfanApi;
    /**
     * 自定义的okHttpClient
     * 如果不自定义 ，就是用sdk默认的OkHttpClient实例
     */
    @Getter
    private OkHttpClient okHttpClient;

    /**
     * 构造器
     *
     * @return OpenAiClient.Builder
     */
    public static QianfanApiClient.Builder builder() {
        return new QianfanApiClient.Builder();
    }

    private OkHttpClient okHttpClient() {
        return new OkHttpClient
                .Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();
    }

    private QianfanApiClient(QianfanApiClient.Builder builder) {
        if (!StringUtils.hasText(builder.clientId)) {
            throw new RuntimeException("clientId is null");
        }
        this.clientId = builder.clientId;

        if (!StringUtils.hasText(builder.clientSecret)) {
            throw new RuntimeException("clientSecret is null");
        }
        this.clientSecret = builder.clientSecret;

        if (!StringUtils.hasText(builder.grantType)) {
            builder.grantType = QianfanConst.GRANT_TYPE;
        }
        this.grantType = builder.grantType;

        if (StrUtil.isBlank(builder.apiHost)) {
            builder.apiHost = QianfanConst.BCE_HOST;
        }
        this.apiHost = builder.apiHost;

        if (Objects.isNull(builder.okHttpClient)) {
            builder.okHttpClient = this.okHttpClient();
        } else {
            // 自定义的okhttpClient
            builder.okHttpClient = builder.okHttpClient
                    .newBuilder()
                    .build();
        }
        okHttpClient = builder.okHttpClient;
        this.qianfanApi = new Retrofit.Builder()
                .baseUrl(apiHost)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(QianfanApi.class);
    }

    public ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion) {
        String token = "24.8fe100dfd5cf0e22b9acb394a24a642f.2592000.1701511676.282335-38561369";
        Single<ChatCompletionResponse> chatCompletionResponse = this.qianfanApi.chatCompletion(token, chatCompletion);
        return chatCompletionResponse.blockingGet();
    }

    public static final class Builder {
        private @NotNull String clientId;
        private @NotNull String clientSecret;
        private String grantType;
        private String apiHost;
        /**
         * 自定义OkhttpClient
         */
        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public QianfanApiClient.Builder apiHost(String val) {
            apiHost = val;
            return this;
        }

        public QianfanApiClient.Builder grantType(@NotNull String val) {
            this.grantType = val;
            return this;
        }

        public QianfanApiClient.Builder clientId(@NotNull String val) {
            this.clientId = val;
            return this;
        }

        public QianfanApiClient.Builder clientSecret(@NotNull String val) {
            this.clientSecret = val;
            return this;
        }

        public QianfanApiClient.Builder okHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public QianfanApiClient build() {
            return new QianfanApiClient(this);
        }

    }
}
