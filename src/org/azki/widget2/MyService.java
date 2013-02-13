package org.azki.widget2;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
	// MediaPlayer mMediaPlayer;
	// String mMp3Path = "";
	static final int NOTI_NUM = 10967;
	Thread mThread;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	boolean threadLoop, threadLoopSwitch;
	long beforeBtyes;
	long dateBytesDelta;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!threadLoop) {
			Toast.makeText(this, "Start " + getText(R.string.app_name),
					Toast.LENGTH_LONG).show();
			pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
			editor = pref.edit();
			mThread = new Thread(mRun);
			mThread.start();
		}
		return START_STICKY;
	}

	Runnable mRun = new Runnable() {
		@SuppressLint("CommitPrefEdits")
		public void run() {
			try {
				threadLoop = true;
				threadLoopSwitch = true;
				dateBytesDelta = TrafficStats.getMobileRxBytes()
						+ TrafficStats.getMobileTxBytes();
				while (threadLoopSwitch) {
					long mobileBytes = TrafficStats.getMobileRxBytes()
							+ TrafficStats.getMobileTxBytes();
					float useage = pref.getFloat("useage", 0);
					float goal = pref.getFloat("goal", 0);
					int beforeDate = pref.getInt("beforeDate", 0);

					Calendar rightNow = Calendar.getInstance();
					int nowDate = rightNow.get(Calendar.DATE);
					if (beforeDate != nowDate) {
						editor.putInt("beforeDate", nowDate);
						dateBytesDelta = mobileBytes;
						beforeBtyes = 0;
						useage = 0;
					}
					long todayMobileBytes = mobileBytes - dateBytesDelta;
					long deltaBytes = todayMobileBytes - beforeBtyes;
					beforeBtyes = todayMobileBytes;
					editor.putFloat("useage", useage + deltaBytes);
					editor.commit();

					PendingIntent intent = PendingIntent.getActivity(
							getApplicationContext(), 0,
							new Intent(getApplicationContext(),
									MainActivity.class), 0);

					Notification notification = new Notification.Builder(
							getApplicationContext())
							.setContentIntent(intent)
							.setContentTitle(getText(R.string.app_name))
							.setContentText(
									"Today Useage: "
											+ String.valueOf((float) (int) (100 * 100 * useage / goal) / 100)
											+ "% ("
											+ String.valueOf((float) (int) (100 * useage / 1024 / 1024) / 100)
											+ "mb / "
											+ String.valueOf((float) (int) (100 * goal / 1024 / 1024) / 100)
											+ "mb)")
							.setProgress(100, (int) (100 * useage / goal),
									false).setSmallIcon(R.drawable.ic_launcher)
							.build();
					notification.flags = Notification.FLAG_ONGOING_EVENT;
					NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					nm.notify(NOTI_NUM, notification);

					Thread.sleep(1000 * 5);
				}
				threadLoop = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service destroy..", Toast.LENGTH_SHORT).show();
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NOTI_NUM);
		nm = null;
		threadLoopSwitch = false;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}