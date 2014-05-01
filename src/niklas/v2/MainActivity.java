package niklas.v2;

import java.util.ArrayList;

import InterFaces.Observer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ArrayList<Thread> threads;
	private ArrayList<ClientButton> buttons;
	public static ArrayList<Client> conversations;
	private LinearLayout layout;
	private ScrollView scroll;
	
	String myName;
	String color;
	boolean server;
	int port;
	
	Server backGroundServer = null;
	Thread serverThread = null;
	
	Context savedContext;
	Button connect;
	
	double lastBackButtonPress = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		savedContext = this;
		threads = new ArrayList<Thread>();
		conversations = new ArrayList<Client>();
		buttons = new ArrayList<ClientButton>();
		
		
		myName = getIntent().getExtras().getString("name");
		color = getIntent().getExtras().getString("color");
		String serv = getIntent().getExtras().getString("server");
		String portStr = getIntent().getExtras().getString("port");
		
		if (serv.equals("on")) {
			server = true;
			port = Integer.parseInt(portStr);
			backGroundServer = new Server(this, port, myName, color);
			Thread serverThread = new Thread(backGroundServer);
			serverThread.start();
			
		} 
		else {
			server = false;
		}
		
				
		layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"
        layout.addView(new NewConversationButton());
        scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launcher, menu);
		return false;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	
        	double t = System.currentTimeMillis();
    		if (t-lastBackButtonPress < 2000) {
    			this.finish();
    		}
    		lastBackButtonPress = t;
    		Toast.makeText(savedContext, "press again to exit",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onBackPressed() {
		double t = System.currentTimeMillis();
		if (t-lastBackButtonPress < 2000) {
			//moveTaskToBack(true);
			this.finish();
		}
		lastBackButtonPress = t;
		Toast.makeText(savedContext, "press again to exit",Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy() {
		for (Client c: conversations) {
			c.disconnect();
		}
		if (backGroundServer != null) {
			backGroundServer.stop();
		}
		super.onDestroy();
	}
	
	public void addClient(final Client c, final String conversationName) {
		
		runOnUiThread (new Thread(new Runnable() {
	         public void run() {
	        	Thread t = new Thread(c);
	     		t.start();
	     		conversations.add(c);
	     		threads.add(t);
	     		
	     		ClientButton cb = new ClientButton(c,conversationName);
	     		buttons.add(cb);
	     		layout.addView(cb);
	         }
	     }));
	}
	
	public void removeConversation(Client client) {
		int index = conversations.indexOf(client);
		
		if (index != -1) {
			conversations.remove(index);
			threads.remove(index);
			layout.removeViewAt(index+1);
			buttons.remove(index);
			client.disconnect();
		}	
	}
	
	class NewConversationButton extends Button implements OnClickListener {

		public NewConversationButton() {
			super(savedContext);
			//super(savedContext, null,android.R.style.n);
			//context, null, android.R.attr.buttonStyleSmall);
			setText("Start new coversation");
			setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			setBackgroundResource(niklas.v2.R.color.White);
			setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			newConversationDialog mCD = new newConversationDialog();
			mCD.show();
		}
	}
	
	class ClientButton extends Button implements OnClickListener, OnLongClickListener, Observer {
		
		Client client;
		String name;
		int messages = 0;
		
		ClientButton(Client c, String name) {
			super(savedContext);
			client = c;
			this.name = name;
			setText(name);
			setOnClickListener(this);
			setOnLongClickListener(this);
			setBackgroundResource(niklas.v2.R.color.Black);
			setTextColor(Color.WHITE);
		}

		@Override
		public void onClick(View v) {
			Intent myIntent = new Intent(v.getContext(), ConversationActivity.class);
			myIntent.putExtra("index",""+conversations.indexOf(client));
			startActivity(myIntent);
		}

		@Override
		public boolean onLongClick(View v) {
			MyDialog md = new MyDialog(client);
			md.show();
			return true;
		}

		@Override
		public void update(String history) {
			messages+=1;
			setText(name+" "+messages);
		}
	}
	
	class MyDialog extends Dialog implements OnClickListener {
		LinearLayout l;
		Button remove;
		Button cancel;
		Client client;
		
		public MyDialog(Client c) {
			super(savedContext);
			l = new LinearLayout(savedContext);
			l.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			l.setOrientation(1);
			super.setTitle("Delete conversation?");
			client = c;
			
			Button remove = new Button(savedContext);
			remove.setText("Delete");
			remove.setId(1);
			remove.setOnClickListener(this);
			remove.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			Button cancel = new Button(savedContext);
			cancel.setText("Cancel");
			cancel.setId(2);
			cancel.setOnClickListener(this);
			cancel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			l.addView(remove);
			l.addView(cancel);
			setContentView(l);
		}

		@Override
		public void onClick(View v) {
			if (v.getId()==1) {
				removeConversation(client);
			}
			this.dismiss();
		}
	}
	
	class newConversationDialog extends Dialog implements OnClickListener {
		
		EditText conversationName = (EditText)findViewById(R.id.editText5);
		EditText ipaddres = (EditText)findViewById(R.id.editText6);
		EditText porttext = (EditText)findViewById(R.id.editText7);
		Button done;
		
		public newConversationDialog() {
			super(savedContext);
			super.setTitle("new Conversation");
			setContentView(R.layout.activity_main_newclient);
			
			conversationName = (EditText)findViewById(R.id.editText5);
			ipaddres = (EditText)findViewById(R.id.editText6);
			porttext = (EditText)findViewById(R.id.editText7);
			done = (Button)findViewById(R.id.connectButton01);
			done.setOnClickListener(this);
			
		}
		@Override
		public void onClick(View v) {
			
			String cName = ""+conversationName.getText();
			String ip = ""+ipaddres.getText();
			String p = ""+porttext.getText();
			int port = Integer.parseInt(p);
			
			
			if (cName == "") {cName = "unnamed conversation";}
			
			Client c = new Client(myName,color,ip,port);
			addClient(c,cName);
			
			this.dismiss();
		}
	}
}