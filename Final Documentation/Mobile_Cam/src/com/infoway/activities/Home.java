package com.infoway.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.infoway.connection.MobileConnection;
import com.infoway.videocam.CameraActivity;
import com.infoway.videocam.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends Activity 
{
    private TextView txtAboutUs;
    private TextView txtGreeting;
    private Button btnStart;
    private TextView txtIP;
    
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
       
        //Create the Required Directory Structure and Files
        createResources();
        
        //Retrieve the Label which will be used to display the url to be used
        txtIP = (TextView) findViewById(R.id.ipaddress);
        
        //Initialize the Connections available. Later used to retrieve IP address
        MobileConnection.Update(this);
        txtIP.append("\nhttp://"+MobileConnection.getInfo("IP")+":8080");
        
        //Link Label to redirect to About Us Activity
        txtAboutUs=(TextView) findViewById(R.id.aboutus);
        txtAboutUs.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) 
			{
				//redirect to aboutus activity
				Intent aboutus=new Intent(getApplicationContext(), AboutUs.class);
				startActivity(aboutus);
			}
		});
        
        btnStart=(Button)findViewById(R.id.startbtn);
        btnStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//start CameraClass Activity i.e. Start Streaming
				Intent home=new Intent(getApplicationContext(), CameraActivity.class);
				startActivity(home);
			}
		}); 
        
        setGreetingText();
    }
    
    //Greating the User Depending on the System Time
    private void setGreetingText()
    {
    	txtGreeting=(TextView) findViewById(R.id.time);
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String str=formattedDate.substring(11, 13);
        int d=Integer.parseInt(str);

        if(d>=1 && d<12)
         	txtGreeting.setText("Good Morning");
         else if(d>=12 && d<16)
         	txtGreeting.setText("Good Afternoon");
         else if(d>=16 && d<21)
         	txtGreeting.setText("Good Evening");
         else
         	txtGreeting.setText("Good Night");
    }
    
    private boolean checkResources()
    {
    	File ipCamDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ipcam");
    	if(ipCamDir.exists())
    		return true;
    	return false;
    }
    
    private void createResources()
    {
    	if(!checkResources())
    	{
    		try
    		{
    			String fileRoot = Environment.getExternalStorageDirectory().getAbsoluteFile()+"/ipcam";
    			File ipcamDir = new File(fileRoot);
    			ipcamDir.mkdir();
    			copyResources(R.raw.index, fileRoot+"/index.html");
    			copyResources(R.raw.flowplayerflash, fileRoot+"/flowplayerflash.swf");
    			copyResources(R.raw.flowplayercontrols, fileRoot+"/playercontrolers.swf");
    			copyResources(R.raw.flowplayer, fileRoot+"/flowplayer.js");
    			copyResources(R.raw.style, fileRoot+"/style.css");
    		}
    		catch(Exception e)
    		{
    			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
    		}
    	}
    }
    
    private void copyResources(int id,String targetFile) throws IOException
    {
    	InputStream ios = ((Context)this).getResources().openRawResource(id);
    	FileOutputStream fos = new FileOutputStream(targetFile);
    	byte []fileLength = new byte[ios.available()];
    	ios.read(fileLength);
    	fos.write(fileLength);
    	ios.close();
    	fos.close();
    }
}