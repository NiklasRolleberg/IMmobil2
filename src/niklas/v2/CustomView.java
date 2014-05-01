package niklas.v2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class CustomView extends View {
	
	Activity owner;
	ArrayList<myButton> buttons;
	
	public CustomView(Context context, Activity o) {
		super(context);
		owner = o;
		buttons = new ArrayList<myButton>();
		System.out.println(o);
		
		Button b = new Button(context);
		b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
		                                   LayoutParams.WRAP_CONTENT)); 

	}
	
	
	
	
	class myButton extends Button implements OnClickListener{

		public myButton(Context context,String name) {
			super(context);
			setText("unnamed");
		}
		
		public boolean isWorking() {
			return true;
		}

		@Override
		public void onClick(View v) {
			System.out.println("Du klickade på mig");
		}
	}

}
