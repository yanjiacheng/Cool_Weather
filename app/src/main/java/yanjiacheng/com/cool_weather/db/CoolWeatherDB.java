package yanjiacheng.com.cool_weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import yanjiacheng.com.cool_weather.model.City;
import yanjiacheng.com.cool_weather.model.County;
import yanjiacheng.com.cool_weather.model.Province;

/**
 * 常用数据库操作类，它是一个单例类，将它构造方法私有化，并且提供一个getInstance()方法来获取实例，，
 * 提供了以下方法: 存储省份数据，读取所有省份数据，存储城市数据，读取某个省类所有城市数据，存储县城数据，读取某个城市里所有县的数据
 * Created by yan on 2016/6/22.
 */
public class CoolWeatherDB{
    /**
     * 数据库名称
     */
   public static final String DB_NAME="cool_weather";

    /**
     * 数据库版本
     *
     */
    public static final int VERSION=1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;//HashMap操作数据 增删改查

    /**
     * 构造方法私有化
     */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }

    /**
     * 获取唯一的CoolWeatherDB实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB==null){
            coolWeatherDB=new CoolWeatherDB(context);

        }
        return coolWeatherDB;
    }

    /**
     *
     * @param province 将Province省份实例存储到数据库
     */
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            /**
             * 参数：表名，第二个参数用于在未指定添加数据的情况下给某些可为空的列自动赋值Null,一般我们不用，，第三个是ContentValues对象
             */
            db.insert("Province",null,values);
        }
    }

    /**
     *
     * @return 全国所有省份的信息
     */
    public List<Province> loadProvinces(){
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do {
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将city实例存储到数据库
     * @param city city实例
     */
    public void saveCity(City city){
        if(city!=null){
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            /**
             * 参数：表名，第二个参数用于在未指定添加数据的情况下给某些可为空的列自动赋值Null,一般我们不用，，第三个是ContentValues对象
             */
            db.insert("City",null,values);
        }
    }

    /**
     * 从数据库读取某个省份下所有城市的信息
     * @param provinceId 省份ID号
     * @return 返回该省份下所有城市
     */
    public List<City> loadCities(int provinceId){
        List<City> list=new ArrayList<City>();
        /**
         * 游标的参数   表名，字段，where语句，where语句中的参数,分组，分组后过滤,排序
         */
        Cursor cursor=db.query("City",null,"province_id=?",new String[] {String.valueOf((provinceId))},null,null,null);
        if(cursor.moveToFirst()){
            do {
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
    }


    /**
     *
     *存储县城County的数据
     */
    public void saveCounty(County county){
        if(county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            /**
             * 参数：表名，第二个参数用于在未指定添加数据的情况下给某些可为空的列自动赋值Null,一般我们不用，，第三个是ContentValues对象
             */
            db.insert("County",null,values);
        }
    }

    /**
     * 获得指定城市下面所有的县城信息
     *
     * @param cityId 城市Id
     * @return 所有县城对象列表
     */
    public List<County> loadCounties(int cityId){
        List<County> list=new ArrayList<County>();
        /**
         * 游标的参数   表名，字段，where语句，where语句中的参数,分组，分组后过滤,排序
         */
        Cursor cursor=db.query("County",null,"city_id=?",new String[] {String.valueOf((cityId))},null,null,null);
        if(cursor.moveToFirst()){
            do {
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            }while(cursor.moveToNext());
        }
        return list;
    }
}
