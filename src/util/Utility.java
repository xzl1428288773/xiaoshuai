package util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import model.City;
import model.County;
import model.Province;
import model.XiaoshuaiDB;

public class Utility {
	/**
	 * 从服务器中获得全国所有省市县的数据,并保存到数据库
	 */
	public synchronized static boolean handleDataFromServer(XiaoshuaiDB xiaoshuaiDB, String response) {
		boolean  result=false;
		if (!TextUtils.isEmpty(response)) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONArray jsonArray = jsonObject.getJSONArray("result");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					String provinceName = jsonObject2.getString("province");
					//province数据
					Province province = new Province();
					province.setProvinceName(provinceName);
					xiaoshuaiDB.saveProvince(province);
					//city数据
					City city = new City();
					city.setCityName(jsonObject2.getString("city"));
					city.setProvinceName(provinceName);
					xiaoshuaiDB.saveCity(city);
                    //county数据
					County county = new County();
					county.setCountyCode(jsonObject2.getString("id"));
					county.setCountyName(jsonObject2.getString("district"));
					county.setCityName(jsonObject2.getString("city"));
                    xiaoshuaiDB.saveCounty(county);
				}
				result=true;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result=false;
			}
		}
		return result;
	}
	/**
	 * 从服务器中加载特定的天气信息，并保持到本地
	 */
	public static void handleWeatherResponse(Context context,String response,String county_code){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject jsonObject2=jsonObject.getJSONObject("result");
			JSONObject jsonObject3=jsonObject2.getJSONObject("sk");
			JSONObject jsonObject4=jsonObject2.getJSONObject("today");
			String cityName=jsonObject4.getString("city");
			String publishTime=jsonObject3.getString("time");
			String currentDate=jsonObject4.getString("date_y");
			String weatherDesp=jsonObject4.getString("weather");
			String tempRange=jsonObject4.getString("temperature");
			saveWeather(context,cityName,publishTime,currentDate,weatherDesp,tempRange,county_code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将服务器返回的天气信息存到本地中
	 */
	public static void saveWeather(Context context, String cityName, String publishTime, String currentDate,
			String weatherDesp, String tempRange,String county_code) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("county_code", county_code);
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", currentDate);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("temp_range", tempRange);
		editor.commit();		
	}
}
