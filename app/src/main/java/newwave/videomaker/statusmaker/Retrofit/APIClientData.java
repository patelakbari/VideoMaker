package newwave.videomaker.statusmaker.Retrofit;

import newwave.videomaker.statusmaker.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClientData {
    public static Retrofit retrofit = null;


    public static SwUserService getInterface() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);


        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(Level.BODY);
            httpClient.addInterceptor(httpLoggingInterceptor);
        }
        retrofit = new Builder().baseUrl(BuildConfig.My_api).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        return retrofit.create(SwUserService.class);
    }
}
