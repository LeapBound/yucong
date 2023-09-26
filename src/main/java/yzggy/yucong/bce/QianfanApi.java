package yzggy.yucong.bce;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import yzggy.yucong.bce.entity.ChatCompletion;
import yzggy.yucong.bce.entity.ChatCompletionResponse;

public interface QianfanApi {

    @POST("rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions")
    Single<ChatCompletionResponse> chatCompletion(@Body ChatCompletion chatCompletion);

}
