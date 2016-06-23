package yanjiacheng.com.cool_weather.util;

/**
 * 使用这个接口来回调服务返回的结果
 * Created by yan on 2016/6/22.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
