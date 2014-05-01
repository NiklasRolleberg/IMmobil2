package niklas.v2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class Launcher extends Activity {
	
	EditText nam;
	EditText col;
	Switch ser;
	EditText por;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		
		nam = (EditText) findViewById(R.id.editText1);
		col = (EditText) findViewById(R.id.editText2);
		ser = (Switch) findViewById(R.id.switch1);
		por = (EditText) findViewById(R.id.editText4);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launcher, menu);
		return false;
	}
	
	public void start(View v) {
		String name = ""+nam.getText();
		String color =""+col.getText();
		boolean serv = ser.isChecked();
		String server = "off";
		String port = "0000";
		if (serv) {
			port = ""+por.getText();
			server = "on";
		}
		
		if (name.length()>=1 && port.length() == 4) {
			Intent myIntent = new Intent(v.getContext(), MainActivity.class);
			myIntent.putExtra("name", name);
			myIntent.putExtra("color", color);
			myIntent.putExtra("server", server);
			myIntent.putExtra("port", port);
	    	startActivityForResult(myIntent, 0);
		}
	}
}
