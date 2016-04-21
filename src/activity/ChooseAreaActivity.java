package activity;

import java.util.ArrayList;
import java.util.List;

import com.example.xiaoshhuai.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import model.*;
import model.XiaoshuaiDB;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

public class ChooseAreaActivity extends Activity{
  public static final int LEVEL_PROVINCE=0;
  public static final int LEVEL_CITY=1;
  public static final int LEVEL_COUNTY=2;
  private TextView titletext;
  private ListView listView;
  private ArrayAdapter<String> adapter;
  private XiaoshuaiDB xiaoshuaiDB;
  private List<String> dataList = new ArrayList<String>();
  private List<Province> provinceList;
  private List<City> cityList;
  private List<County> countyList;
  private Province selectedProvince;
  private City selectedCity;
  private int currentLevel;
  private boolean isFromWeatherShow;
  @Override
  protected void onCreate(Bundle savedInstanceState){
	  super.onCreate(savedInstanceState);
	  isFromWeatherShow=getIntent().getBooleanExtra("from_weather_activity", false);
	  SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
	  if(preferences.getBoolean("city_selected", false) && !isFromWeatherShow){
		  Intent intent=new Intent(this,WeatherShow.class);
		  startActivity(intent);
		  finish();
		  return;
	  }
	  requestWindowFeature(Window.FEATURE_NO_TITLE);
      setContentView(R.layout.choose_area);
      listView=(ListView)findViewById(R.id.list_view);
      titletext=(TextView)findViewById(R.id.title_text);
      adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
      listView.setAdapter(adapter);
      xiaoshuaiDB=XiaoshuaiDB.getInstance(this);
      listView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			if(currentLevel==LEVEL_PROVINCE){
				selectedProvince=provinceList.get(position);
				queryCities();
			}else if(currentLevel==LEVEL_CITY){
				selectedCity=cityList.get(position);
				queryCounties();
			}else if(currentLevel==LEVEL_COUNTY){
				String countyCode=countyList.get(position).getCountyCode();
				Intent intent=new Intent(ChooseAreaActivity.this,WeatherShow.class);
				intent.putExtra("county_code",countyCode);
				startActivity(intent);
				finish() ;
			}
		}
	});
      queryProvinces();
  }
  //从数据库中获得省份信息，并更新UI
  private void queryProvinces(){
	  provinceList=xiaoshuaiDB.loadProvinces();
	  if(provinceList.size()>0){
		  dataList.clear();
		  for(Province province : provinceList){
			  dataList.add(province.getProvinceName());
		  }
		  adapter.notifyDataSetChanged();
		  listView.setSelection(0);
		  titletext.setText("中国");
		  currentLevel=LEVEL_PROVINCE;
	  }else{
		  queryFromServer("http://v.juhe.cn/weather/citys?key=f82f73a70a431bd851a35622336716ec","province");
	  }
	  
  }
//从数据库中获得城市信息，并更新UI
  private void queryCities(){
	  cityList=xiaoshuaiDB.loadCities(selectedProvince.getProvinceName());
	  if(cityList.size()>0){
		  dataList.clear();
		  for(City city : cityList){
			  dataList.add(city.getCityName());
		  }
		  adapter.notifyDataSetChanged();
		  listView.setSelection(0);
		  titletext.setText(selectedProvince.getProvinceName());
		  currentLevel=LEVEL_CITY;
	  }else{
		  queryFromServer("http://v.juhe.cn/weather/citys?key=f82f73a70a431bd851a35622336716ec","city");
	  }
  }
//从数据库中获得地区信息，并更新UI
  private void queryCounties(){
	  countyList=xiaoshuaiDB.loadCounties(selectedCity.getCityName());
	  if(countyList.size()>0){
		  dataList.clear();
		  for(County county : countyList){
			  dataList.add(county.getCountyName());
		  }
		  adapter.notifyDataSetChanged();
		  listView.setSelection(0);
		  titletext.setText(selectedCity.getCityName());
		  currentLevel=LEVEL_COUNTY;
	  }else{
		  queryFromServer("http://v.juhe.cn/weather/citys?key=f82f73a70a431bd851a35622336716ec","county");
	  }
  }
  private void queryFromServer(String address,final String type){
	   HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			boolean result=false;
			// TODO Auto-generated method stub
			result=Utility.handleDataFromServer(xiaoshuaiDB, response);
			if(result){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if("province".equals(type)){
							queryProvinces();
						}else if("city".equals(type)){
							queryCities();
						}else if("county".equals(type)){
							queryCounties();
						}
					}
				});
			}
		}
		
		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	});
  }
  @Override
  public void onBackPressed(){
	  if(currentLevel==LEVEL_COUNTY){
		  queryCities();
	  }else if(currentLevel==LEVEL_CITY){
		  queryProvinces();
	  }else {
		  if(isFromWeatherShow){
		  Intent intent=new Intent(this,WeatherShow.class);
		  startActivity(intent);
		  }
		  finish();
	  }
  }
}
