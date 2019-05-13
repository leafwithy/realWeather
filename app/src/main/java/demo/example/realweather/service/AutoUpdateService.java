package demo.example.realweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import demo.example.realweather.gson.Weather;
import demo.example.realweather.util.HttpUtil;
import demo.example.realweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
      //  throw new UnsupportedOperationException("Not yet implemented");
        Log.d("tag2","bind");
        return null;
}

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("tag2","Destroy"+ String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tag2","create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBackgroundimg();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//8小时 毫秒
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * 更新天气
     */
    public void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析数据
            final Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid="+
                    weatherId+"&key=c7b9b24337fe442598fe6f631aa6d1e2";
            HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather1 = Utility.handleWeatherResponse(responseText);
                    if(weather1!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
    /**
     * 更新每日一图
     */
    public void updateBackgroundimg(){
        String requestbackground = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(requestbackground, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String backgroundimg = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("background_img",backgroundimg);
                editor.apply();
            }
        });
    }
}
