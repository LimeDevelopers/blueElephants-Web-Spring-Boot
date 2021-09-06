package kr.or.btf.web.common.retrofit;

import kr.or.btf.web.domain.web.enums.ApiURL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfig {
    private static final String baseURL = "https://api.sphokidz.com";

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
//                .addInterceptor(chain -> {
//                    Request request = chain.request()
//                            .newBuilder()
//                            .addHeader("apiKey", "api등록 키")
//                            .build();
//                    return chain.proceed(request);
//                })
                .build();
    }

    @Bean
    public Retrofit retrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Bean
    public ServerAPIs serverAPIs(Retrofit retrofit) {
        return retrofit.create(ServerAPIs.class);
    }
}

