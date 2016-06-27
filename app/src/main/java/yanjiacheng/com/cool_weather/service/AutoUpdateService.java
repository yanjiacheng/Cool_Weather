package yanjiacheng.com.cool_weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import yanjiacheng.com.cool_weather.recevier.AutoUpdateReceiver;
import yanjiacheng.com.cool_weather.util.HttpCallbackListener;
import yanjiacheng.com.cool_weather.util.HttpUtil;
import yanjiacheng.com.cool_weather.util.Utility;

/**
 * 服务
 *
 * 用来实现后台服务自动更新天气,每8小时自动更新一次数据到SharedPreferences对应文件中。当用户打开软件时候，自动调用新数据
 * Created by yan on 2016/6/27.
 */
public class AutoUpdateService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开启了一个子线程，在子线程里面调用updateWeather方法来更新天气
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;//8个小时的毫秒数
        //设定系统时钟.
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);//8小时后自动调用Intent  跳转到AutoUpdateReceiver
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=prefs.getString("weather_code", "");
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //需要context上下文来确定SharedPreferences文件名称
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
}
