package com.pradnya.CameraApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.pradnya.CameraApp.R;
import android.R.integer;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class GUIDesingActivity extends Activity {
    /** Called when the activity is first created. */
    TextView txtaboutus;
    TextView txtgreeting;
    Button startbtn;
    TextView txtIP;
	
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        txtIP = (TextView) findViewById(R.id.ipaddress);
        MobileConnection.Update(this);
        txtIP.append("\n\thttp://"+MobileConnection.getInfo("IP")+":8080/");
        
        txtaboutus=(TextView) findViewById(R.id.aboutus);
        txtaboutus.setOnClickListener(new OnClickListener() {
		
			public void onClick(View arg0) 
			{
				//go to aboutus activity
					Intent aboutus=new Intent(getApplicationContext(), AboutUs.class);
					startActivity(aboutus);
			}
		});
        
        startbtn=(Button)findViewById(R.id.startbtn);
        
        startbtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//start camera
				Intent home=new Intent(getApplicationContext(), Home.class);
				startActivity(home);
			}
		}); 
        
       txtgreeting=(TextView) findViewById(R.id.time);

       Calendar c = Calendar.getInstance();
       System.out.println("Current time => "+c.getTime());

       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String formattedDate = df.format(c.getTime());
       String str=formattedDate.substring(11, 13);
       //txtgreeting.setText(str);
  int d=Integer.parseInt(str);
       if(d>=1 && d<12)
        {
        	txtgreeting.setText("Good Morning");
        	
        }
        else if(d>=12 && d<16)
        {
        	txtgreeting.setText("Good Afternoon");
        	
        }
        else if(d>=16 && d<21)
        {
        	txtgreeting.setText("Good Evening");
        }
        else
        {
        	txtgreeting.setText("Good Night");
        }    
    }
}