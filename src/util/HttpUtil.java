package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
   public static void sendHttpRequest(final String address,final HttpCallbackListener listenr){
	   new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			try {
				URL url=new URL(address);
				connection=(HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(8000);
				InputStream inputStream=connection.getInputStream();
				BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder response=new StringBuilder();
				String line;
				while((line=reader.readLine())!=null){
					response.append(line);
				}
				if(listenr!=null){
					//回调onFinish()方法
					listenr.onFinish(response.toString());
				} 
			}catch (Exception e) {
				    //回调onError()方法
				    listenr.onError(e);
			}finally {
				if(connection!=null){
					connection.disconnect();
				}
			}
		}
	}).start();
   }
}
