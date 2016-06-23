package yanjiacheng.com.cool_weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yan on 2016/6/22.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    /**创建省数据表语句
     * id 主键。   province_name 省名    province_code 省级代号
     */
    public static final String CREATE_PROVINCE="create table Province(id integer primary key autoincrement,province_name text,province_code text)";
    /**
     * 创建城市数据表语句
     */
    public static final String CREATE_CITY="create table City(id integer primary key autoincrement,city_name text,city_code text,province_id integer)";
    /**
     * 创建县数据表语句
     */
    public static final String CREATE_COUNTY="create table County (id integer primary key autoincrement,county_name text,county_code text,city_id integer)";


    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
