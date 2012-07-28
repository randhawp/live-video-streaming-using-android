package com.pradnya.CameraApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

public class Server extends NanoHTTPD
{
	public Server() throws IOException
	{
		//Initialize the NanoHttpD and pass it a home Directory of the Application
		super(8080,new File(Environment.getExternalStorageDirectory().getPath()+"/ipcam").getAbsoluteFile());
	}
	
	 public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) 
	 {
		 //If request is for an html page give response as index.html
		 if(uri.contains(".html"))
		 {
			 InputStream ins=null;
				try 
				{
					ins = new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/ipcam/index.html");
				} 
				catch (FileNotFoundException e) 
				{ }
				 
				 Response res = new Response( HTTP_OK, "text/html", ins);
		         res.addHeader( "Connection", "Keep-alive");
		         return res;
		 }
		 else 
		 {
			 //else let NanoHttpD handel the request
			 return serveFile( uri, header,new File(Environment.getExternalStorageDirectory().getPath()+"/ipcam").getAbsoluteFile(), true ); 
		 }
	 }
	 //at the End Stop the server
	public void end()
	{
		stop();
	}
}