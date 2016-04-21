package activity;

import com.example.xiaoshhuai.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

public class WeatherShow extends Activity implements android.view.View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView tempText;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.weather_layout);
    	weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
    	cityNameText=(TextView)findViewById(R.id.city_name);
    	publishText=(TextView)findViewById(R.id.publish_text);
    	weatherDespText=(TextView)findViewById(R.id.weather_desp);
    	tempText=(TextView)findViewById(R.id.temp_range);
    	switchCity=(Button)findViewById(R.id.switch_city);
    	refreshWeather=(Button)findViewById(R.id.refresh_weather);
    	currentDateText=(TextView)findViewById(R.id.current_data);
    	String countyCode=getIntent().getStringExtra("county_code");
    	if(!TextUtils.isEmpty(countyCode)){
    		publishText.setText("同步中...");
//    		weatherInfoLayout.setVisibility(View.INVISIBLE);
//    		cityNameText.setVisibility(View.INVISIBLE);
    		queryWeatherCode(countyCode);
    	}else {
    		showWeather();
    	}
    	switchCity.setOnClickListener(this);
    	refreshWeather.setOnClickListener(this);
    }
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(preferences.getString("city_name", ""));
		publishText.setText("今天"+preferences.getString("publish_time", "")+"发布");
		weatherDespText.setText(preferences.getString("weather_desp", ""));
		tempText.setText(preferences.getString("temp_range", ""));
		currentDateText.setText(preferences.getString("current_date", ""));
//		weatherInfoLayout.setVisibility(View.VISIBLE);
//		cityNameText.setVisibility(View.VISIBLE);
	}
	private void queryWeatherCode(String county_code) {
		// TODO Auto-generated method stub
		String address="http://v.juhe.cn/weather/index?cityname="+county_code+"&dtype=&format=&key=f82f73a70a431bd851a35622336716ec";
		queryFromServer(address,county_code);
	}
	private void queryFromServer(String address,final String county_code) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(WeatherShow.this, response,county_code);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						showWeather();
					}
				});
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
			String countyCode=preferences.getString("county_code", "");
			if(TextUtils.isEmpty(countyCode)){
				queryWeatherCode(countyCode);
			}
		default:
			
			break;
		}
	}
	

}
