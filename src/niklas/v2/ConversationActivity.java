package niklas.v2;

import InterFaces.Observer;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConversationActivity extends Activity implements Observer {
	
	Client  client;
	TextView chattWindow;
	EditText messageWindow;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		int index = Integer.parseInt(getIntent().getExtras().getString("index"));
		client = MainActivity.conversations.get(index);
		setContentView(R.layout.conversation);
		chattWindow = (TextView) findViewById(R.id.chattwindow);
		messageWindow = (EditText) findViewById(R.id.messageField);
		client.registerObserver(this);
	}
	
	/**Keeps the up button from reseting activity*/
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.launcher, menu);
		return false;
	}
	
	/**takes the words from messagewindow and send the message*/
	public void send(View v) {
		String s = messageWindow.getText().toString();
		messageWindow.setText("");
		client.send(s);
	}

	/**Writes the message in chattwindow*/
	@Override
	public void update(final String history) {
		runOnUiThread (new Thread(new Runnable() {
	         public void run() {
	        	 chattWindow.setText(Html.fromHtml(history));
	        	 //chattWindow.setText(history);
	         }
	     }));
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	}

	@Override
	public void onResume() {
	    super.onResume();
	    client.requestUpdate();
	}
	
	@Override
	public void onStart() {
		client.requestUpdate();
		super.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		//System.out.println("onDestroy");
		client.removeObserver(this);
		super.onDestroy();
	}
	
	
}
