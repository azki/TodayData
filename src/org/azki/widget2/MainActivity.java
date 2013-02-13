package org.azki.widget2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		EditText edit1 = (EditText) findViewById(R.id.editText1);
		float useage = (float)(int)(10 * pref.getFloat("useage", 0) / 1024) / 10;
		edit1.setText(String.valueOf(useage));
		EditText edit2 = (EditText) findViewById(R.id.editText2);
		float goal = pref.getFloat("goal", 50 * 1024 * 1024 ) / 1024 / 1024;
		edit2.setText(String.valueOf(goal));

		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();

				EditText edit1 = (EditText) findViewById(R.id.editText1);
				float useage = Float.parseFloat(edit1.getText().toString()) * 1024;
				editor.putFloat("useage", useage);

				EditText edit2 = (EditText) findViewById(R.id.editText2);
				float goal = Float.parseFloat(edit2.getText().toString()) * 1024 * 1024;
				editor.putFloat("goal", goal);

				editor.commit();
			}
		});
		
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		startService(new Intent(this, MyService.class));
	}
}
