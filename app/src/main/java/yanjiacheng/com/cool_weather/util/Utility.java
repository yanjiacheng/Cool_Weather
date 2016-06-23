package yanjiacheng.com.cool_weather.util;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
          if(allProvinces!=null&&allProvinces.length>0){
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
     * @return
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null&&allCities.length>0){
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
            if(allCounties!=null&&allCounties.length>0){
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
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
