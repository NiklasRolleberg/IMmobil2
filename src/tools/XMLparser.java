package tools;

public class XMLparser {
	
	/**
	 * @param instr
	 * @return
	 * -1 = broken
	 * 0 = unknown
	 * 1 = text message
	 * 2 = disconnect
	 */
	public static int type(String instr) {
		int index = instr.indexOf("<message");
		//System.out.println("Get type: instr"+instr);
		//System.out.println("Get type: index="+index);		
		if (index <= 4) {
			if (instr.contains("<disconnect/>")){
				//System.out.println("Get type= disconnect");
				return 2;
			}

			if (instr.contains("<text") && instr.contains("</text>") && instr.indexOf("<text") < instr.indexOf("</text>")) {
				//System.out.println("Get type= text");
				return 1;
			}
		}
		return -1;
	}

	public static String getTextMessage(String instr) {
		return "<font color=#"+findColor(instr)+'>'+findSender(instr)+": "+findText(instr)+"</font><br>";
	}
	 //<font color=white>" text "</font><br>
	
	/**returns the senders name
	 * 
	 * @param in
	 * message string
	 * 
	 * @return
	 * name of sender
	 */
	private static String findSender(String in) {	
		//kolla om tagen är som den ska
		if (in.contains("sender=")) {
			int i;
			if ((i = in.indexOf("sender=")) < (in.indexOf(">"))) {
				String sub = in.substring(i+8);		
				int j = sub.indexOf('"');		
				return sub.substring(0,j);
			}
			return ("not working");
	    }
	    else{
	    	return("not working");	    	
	    }
	 }
	
	/**returns the textmessage
	 * 
	 * @param in
	 * the string with text
	 * 
	 * @return
	 * the textmessage
	 */
	private static String findText(String in) {	 
	    if (in.contains("<text") && in.contains("</text>")){
	    	String text = "";
	    	
	    	int i = in.indexOf("<text");
	    	text = in.substring(i);
	    	text = text.substring(text.indexOf('>'));
	    	int j = text.indexOf("</text>");
	    	text =text.substring(1,j);
	    
	    	
	    	return text;
	    }
	    else{
	    	return("not working");	    	
	    }
	 }
	
	/**returns the color
	 * 
	 * @param in
	 * message string
	 * 
	 * @return
	 * String with color ex"ffffff"
	 */
	private static String findColor(String in) {
		String c = "000000"; //default color
		if (in.contains("color=#")) {
			int i = in.indexOf("color=#");
			c = in.substring(i+7, i+13);	
		}
		//System.out.println(in);
		//System.out.println(c);
		return c;
	}
}
