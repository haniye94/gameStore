package co.hani.myket.network;


import co.hani.myket.model.GameModelResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RequestInterface {
    @Headers({
            "Myket-Version: 735",
            "Authorization: eyJhY2MiOiJraC5tYXNoYXlla2hAeWFob28uY29tIiwiYXBpIjoiMjMiLCJjcHUiOiJhcm02NC12OGE7YXJtZWFiaS12N2E7YXJtZWFiaSIsImR0IjozOTMzMzQ4MjYsImgiOiIzNTc5NjMwNTA2MjQ4NzEiLCJoc2giOiJMUW5IN0p1SVNMN3BwMnJpK3R5MFpqTVBBSW89Iiwic2NyIjoiMzAwXzQ4MCJ9"
    })
    @GET("v1/applications/package/All_Data/?")
    Call<GameModelResponse> getGameList(@Query("offset") String offset, @Query("limit") String limit, @Query("lang") String lang);
}
