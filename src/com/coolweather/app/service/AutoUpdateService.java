package com.coolweather.app.service;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8 * 60 * 60 * 1000; // ��Сʱ�ĺ�����
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent intent1 = new Intent(this, AutoUpdateService.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
		return super.onStartCommand(intent, flags, startId);
	}
	
	//����������Ϣ
	 private void updateWeather() {
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		 String weatherCode = preferences.getString("weather_code", "");
		 String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode +".html";
		 HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				Utility.handleWeatherResponse(AutoUpdateService.this, response);
				
			}
			
			@Override
			public void onError(Exception exception) {

				exception.printStackTrace();
			}
		});
	 }
}
