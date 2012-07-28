package com.infoway.videocam;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import android.os.Environment;

public class Server extends NanoHTTPD
{
	private boolean firstRequest=true;
	public Server() throws IOException
	{
		super(8080,new File(Environment.getExternalStorageDirectory().getPath()+"/ipcam").getAbsoluteFile());
	}
	 public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) 
	 {
		 //Deliberately introduce 6sec delay so as there is no conflict in reading the files
		 if(firstRequest)
		 {
			 firstRequest=false;
			 try 
			 {
				Thread.sleep(6000);
			 } 
			 catch (InterruptedException e) 
			 { }
		 }
         return serveFile( uri, header,new File(Environment.getExternalStorageDirectory().getPath()+"/ipcam").getAbsoluteFile(), true ); 
	 }
	public void end()
	{
		stop();
	}
}
