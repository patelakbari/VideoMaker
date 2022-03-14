package newwave.videomaker.statusmaker.Retrofit;

import newwave.videomaker.statusmaker.model.ModelCategoryResponse;
import newwave.videomaker.statusmaker.model.ModelOptionsResponse;
import newwave.videomaker.statusmaker.model.ModelVideoResponce;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SwUserService {

    //categoery wise data api call
    @POST("getdatacategorywise1.php")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<ModelVideoResponce> getCatVideo(@Body JsonObject jsonObject);

    @POST("ooption.php")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<ModelOptionsResponse> getCat_Video_ooptions(@Body JsonObject jsonObject);

    //get categoery api call
    @POST("getallcategory.php")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<ModelCategoryResponse> getAllCategory(@Body JsonObject jsonObject);

    //download file api
    @POST("download.php")
    Call<JsonObject> updateDownloads(@Body JsonObject jsonObject);
}
