package model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import db.XiaoShuaiOpenHelper;

public class XiaoshuaiDB {
 
	 /**
	 * ���ݿ���
	 */
	public static final String DB_NAME="Xiaoshuai_Weather";
	 /**
	 * ���ݿ�汾
	 */
	public static final int VERSION=1;
	public static XiaoshuaiDB xiaoshuaiDB;
	private SQLiteDatabase database;
	/**
	 * �����췽��˽�л�
	 */
	private XiaoshuaiDB(Context context){
		XiaoShuaiOpenHelper dbHelper=new XiaoShuaiOpenHelper(context, DB_NAME, null, VERSION);
		database=dbHelper.getWritableDatabase();
	}
	/**
	 * @��ȡXiaoShuai��ʵ��
	 * @return
	 */
	public synchronized static XiaoshuaiDB getInstance(Context context){
		if(xiaoshuaiDB==null){
			xiaoshuaiDB=new XiaoshuaiDB(context);
		}
		return xiaoshuaiDB;
	}
	/**
	 * @��Provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name",province.getProvinceName());
			//values.put("province_code", province.getProvinceCode());
			database.insert("Province", null, values);
		}
	}
	/**
	 * �����ݿ��ȡȫ�����е�ʡ����Ϣ
	 */
	public List<Province> loadProvinces(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=database.query("Province", null, null, null, null,null, null);
		if(cursor.moveToFirst()){
			do{
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				//province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		return list;
	}
	/**
	 * @��Cityʵ���洢�����ݿ�
	 */
	public void saveCity(City city){
		if(city !=null){
			ContentValues values=new ContentValues();
			values.put("city_name",city.getCityName());
			//values.put("city_code", city.getCityCode());
			values.put("province_name", city.getProvinceName());
			database.insert("City", null, values);
		}
	}
	/**
	 * �����ݿ��ȡĳʡ�����еĳ�����Ϣ
	 */
	public List<City> loadCities(String  provinceName){
		List<City> list=new ArrayList<City>();
		Cursor cursor=database.query("City", null, "province_name=?", new String[]{provinceName}, null,null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				//city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	/**
	 * @��Countyʵ���洢�����ݿ�
	 */
	public void saveCounty(County county){
		if(county !=null){
			ContentValues values=new ContentValues();
			values.put("county_name",county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_name", county.getCityName());
			database.insert("County", null, values);
		}
	}
	/**
	 * �����ݿ��ȡĳ���������е�����Ϣ
	 */
	public List<County> loadCounties(String cityName){
		List<County> list=new ArrayList<County>();
		Cursor cursor=database.query("County", null, "city_name=?", new String[]{cityName}, null,null, null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
	}
}
