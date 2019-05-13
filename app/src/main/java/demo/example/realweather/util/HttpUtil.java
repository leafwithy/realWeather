package demo.example.realweather.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    public static void sendOkhttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).build();

        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
