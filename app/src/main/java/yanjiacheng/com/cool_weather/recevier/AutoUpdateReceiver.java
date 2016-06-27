package yanjiacheng.com.cool_weather.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import yanjiacheng.com.cool_weather.service.AutoUpdateService;

/**
 * 广播接收器
 * 系统8小时（可以调时间）后自动调用方法跳转到这里，然后调用这个方法，返回那个方法继续执行，
 * Created by yan on 2016/6/27.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startActivity(i);
    }
}
