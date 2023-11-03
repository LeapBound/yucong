package yzggy.yucong.bce;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yzggy.yucong.bce.entity.ChatCompletion;
import yzggy.yucong.bce.entity.ChatCompletionResponse;

public interface QianfanApi {

    @POST("rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions")
    Single<ChatCompletionResponse> chatCompletion(@Query("access_token") String token, @Body ChatCompletion chatCompletion);

    @POST("rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant")
    Single<ChatCompletionResponse> chatCompletionTurbo(@Query("access_token") String token, @Body ChatCompletion chatCompletion);

    @POST("rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro")
    Single<ChatCompletionResponse> chatCompletionPro(@Query("access_token") String token, @Body ChatCompletion chatCompletion);

}
