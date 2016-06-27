package yanjiacheng.com.cool_weather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import yanjiacheng.com.cool_weather.R;
import yanjiacheng.com.cool_weather.service.AutoUpdateService;
import yanjiacheng.com.cool_weather.util.HttpCallbackListener;
import yanjiacheng.com.cool_weather.util.HttpUtil;
import yanjiacheng.com.cool_weather.util.Utility;

/**
 * Created by yan on 2016/6/24. 天气类
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private RelativeLayout relativeLayout_info;//用来改变背景图片
    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private TextView switchCity;
    private Button refreshWeather;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        //初始化各种控件
        weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
        relativeLayout_info=(RelativeLayout)findViewById(R.id.info);
        cityNameText=(TextView)findViewById(R.id.city_name);
        publishText=(TextView)findViewById(R.id.publish_text);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        temp1Text=(TextView)findViewById(R.id.temp1);
        temp2Text=(TextView)findViewById(R.id.temp2);
        currentDateText=(TextView)findViewById(R.id.current_date);
        switchCity=(Button)findViewById(R.id.switchCity);
        refreshWeather=(Button)findViewById(R.id.refresh_weather);

        String countyCode=getIntent().getStringExtra("county_code");//取得Intent带过来的数据
       // Log.d("TAG2","county_code"+countyCode);
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代码时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);//查询县级城市代号对于的天气代号
        }else{
            //没有县级代号时就直接显示本地天气
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }


    /**
     * 按钮点击事件
     * @param v 事件源
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case   R.id.switchCity:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case  R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);//查询天气代号对应的天气
                }
                break;
            default:
                break;
        }

    }

    /**
     * 查询天气代号对应的天气
     * @param weatherCode 天气代号
     */
    private void queryWeatherInfo(String weatherCode) {
                String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
                queryFormServer(address,"weatherCode");

    }


    /**
     * 查询县级代号对于的天气代号
     * @param countyCode 县级代号
     */
    private void queryWeatherCode(String countyCode) {
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFormServer(address,"countyCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     * @param address 传入的网络地址
     * @param type 类型
     */
    private void queryFormServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败!");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并且显示到界面上
     */
    private void showWeather() {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp2",""));
        temp2Text.setText(prefs.getString("temp1",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天"+prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        //在这个里开始计时，使用自动刷新天气
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);//千万别写错了，这是启动服务，不是活动
    }
}
