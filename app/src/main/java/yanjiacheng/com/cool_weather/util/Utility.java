package yanjiacheng.com.cool_weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import yanjiacheng.com.cool_weather.db.CoolWeatherDB;
import yanjiacheng.com.cool_weather.model.City;
import yanjiacheng.com.cool_weather.model.County;
import yanjiacheng.com.cool_weather.model.Province;

/**解析和处理服务器返回的数据,最后调用CoolWeatherDB中的三个save()方法将数据存储到相应的表中
 * Created by yan on 2016/6/22.
 */
public class Utility {
    /**
     * 解析省份数据  因为返回数据类型为   代号|城市，代号|城市
     * @param coolWeatherDB   常用数据库操作类
     * @param response 服务端返回的数据
     * @return boolean类型
     */
    public synchronized static boolean handlePovincesResponse(CoolWeatherDB coolWeatherDB,String response){
      if(!TextUtils.isEmpty(response)){
          String[] allProvinces=response.split(",");
          if(allProvinces.length>0){
              for(String p:allProvinces){
                  String[] array=p.split("\\|");
                  Province province=new Province();
                  province.setProvinceCode(array[0]);
                  province.setProvinceName(array[1]);
                  /**
                   * 将解析出来的对象保存到Province数据库表
                   */
                  coolWeatherDB.saveProvince(province);
              }
              return true;
          }
      }
        return false;
    }

    /**
     * 解析和处理返回的城市级数据
     * @param coolWeatherDB 数据库操作类
     * @param response 返回数据
     * @param provinceId 省份id
     * @return 是否
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities.length>0){
                for(String c:allCities){
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    /**
                     * 将解析出来的对象保存到City数据库表
                     */
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 将解析出来的对象保存到county数据库表
     */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if(allCounties.length>0){
                for(String c:allCounties){
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);//将解析出来的对象保存到county数据库表
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出来的数据存储到本地
     * @param context 上下文
     * @param response 反应数据文件
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String  weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            //将服务器返回的所有天气信息解析后存储到SharedPrederences文件中
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
            Log.d("aaaaa",cityName+"-"+weatherCode+"-"+temp1+"-"+temp2+"-"+weatherDesp+"-"+publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //将服务器返回的所有天气信息解析后存储到SharedPrederences文件中

    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        Log.d("shared22",cityName+"  "+weatherCode+ " "+temp1);
        editor.commit();
    }
}
