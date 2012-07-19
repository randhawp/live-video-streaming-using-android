package com.pradnya.MyVideoRecorder;

import java.io.File;


import java.io.IOException;

import org.apache.commons.logging.Log;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class MyVideoRecorderActivity extends Activity implements SurfaceHolder.Callback {
    /** Called when the activity is first created. */
	  private AudioManager mAudioManager = null;
	  private Camera myCamera = null;
	  int targetWid = 640;
	    int targetHei = 480;
AlertDialog alert;  
    private MediaRecorder mediaRecorder;
    private SurfaceHolder holder;
    private SurfaceView surface;
    private Button btnCapture;
    File file;
    private Uri fileUri;
	private Uri OutputFileUri;
	 private String checkingFile;
	   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

      checkingFile=Environment.getExternalStorageDirectory().getPath()+"/myvideo.mp4";
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        btnCapture=(Button) findViewById(R.id.btnCapture);
         surface=(SurfaceView) findViewById(R.id.sview);
         SetupCamera();
         prepareVideoRecorder();
      
        
    }
	public void SetupCamera(){    	
    
    holder = surface.getHolder();
    	holder.addCallback(this);
    	holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        myCamera = Camera.open();
      
    }
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if ( myCamera != null && mediaRecorder == null) {
            myCamera.stopPreview();
            try {
                myCamera.setPreviewDisplay(arg0);
            } catch ( Exception ex) {
                ex.printStackTrace(); 
            }
            myCamera.startPreview();
        }
	}
	 private void prepareVideoRecorder(){
	    

		 mediaRecorder =  new MediaRecorder();
	        myCamera.stopPreview();
	        myCamera.unlock();
	   
	        mediaRecorder.setCamera(myCamera);
	        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    CamcorderProfile targetProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
	        targetProfile.videoFrameWidth = targetWid;
	        targetProfile.videoFrameHeight = targetHei;
	        targetProfile.videoFrameRate = 25;
	        targetProfile.videoBitRate = 512*1024;
	        targetProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
	        targetProfile.audioCodec = MediaRecorder.AudioEncoder.AMR_NB;
	        targetProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
	        mediaRecorder.setProfile(targetProfile);
	        mediaRecorder.setOutputFile(checkingFile);
	    
	    }
	 private boolean realyStart() {
	        
	        mediaRecorder.setPreviewDisplay(holder.getSurface());
	        try {
	        	mediaRecorder.prepare();
		    } catch (IllegalStateException e) {
		        releaseMediaRecorder();	
		       // Log.d("TEAONLY", "JAVA:  camera prepare illegal error");
	            return false;
		    } catch (IOException e) {
		        releaseMediaRecorder();	    
		      //  Log.d("TEAONLY", "JAVA:  camera prepare io error");
	            return false;
		    }
		    
	        try {
	            mediaRecorder.start();
	        } catch( Exception e) {
	            releaseMediaRecorder();
		       // Log.d("TEAONLY", "JAVA:  camera start error");
	            return false;
	        }

	        return true;
	    }
	 private void releaseMediaRecorder(){
	        if (mediaRecorder != null) {
	        	mediaRecorder.reset();   // clear recorder configuration
	        	mediaRecorder.release(); // release the recorder object
	        	mediaRecorder = null;
	            myCamera.lock();           // lock camera for later use
	            myCamera.startPreview();
	        }
	        mediaRecorder = null;
	    }
	  public boolean StartRecording() {
	       
	                
	        return realyStart();
	    }
	  public void StopMedia() {
	        mediaRecorder.stop();
	        releaseMediaRecorder();        
	    }
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
			
			
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
		mediaRecorder.release();
		
	}
	public void Start(View v)
	{
		if(btnCapture.getText().equals("Start"))
		{	
			btnCapture.setText("Stop");
			if(StartRecording())
			{
				Toast.makeText(this, "Recording Started",Toast.LENGTH_SHORT ).show();
			}
			else
			{
				Toast.makeText(this, "Recording not done",Toast.LENGTH_SHORT ).show();
				
			}
		}
		else
		{
			 StopMedia();
				btnCapture.setText("Start");
			 myCamera.stopPreview();
				myCamera.release();
			 Toast.makeText(this, "Recording Stopped",Toast.LENGTH_SHORT ).show();
		}	
			
		}
	

	 public boolean onCreateOptionsMenu(Menu menu) {
	    	// TODO Auto-generated method stub
	    	 Toast.makeText(this,"onCreateOptionsMenu()called..",Toast.LENGTH_SHORT ).show();
	  		MenuInflater inflate=getMenuInflater();
	  		inflate.inflate(R.menu.menu, menu);
	    	 return true;
	    }
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	    	// TODO Auto-generated method stub
	   		 Toast.makeText(this,"onPrepareOptionsMenu()called..",Toast.LENGTH_SHORT ).show();
	 		   
	   	 
	    	return true;
	    }
	@Override
	 public boolean onOptionsItemSelected(MenuItem i){
    	switch(i.getItemId()){
		    case R.id.item1:
		   	 Toast.makeText(this, "Settings",Toast.LENGTH_SHORT ).show();
		  	showDialog();
		   	 return true;	    	
		    default:
		    	return false;
		}
    }	
		//	mediaRecorder.release();
		//	mCamera.lock();

 public void Stop(View v)
 {
	
		//btnCapture.setText("Capture");
		
 }
 private void showDialog() { 

	 final CharSequence[] items={"320x240","1280x960","1600x1200"};
	   
	AlertDialog.Builder alt_bld = new AlertDialog.Builder(this); 
	//alt_bld.setIcon(R.drawable.icon); 
	alt_bld.setTitle("Select Resolution"); 

	//you can set the Default selected value of the list, Here it is 0 means India 
	alt_bld.setSingleChoiceItems(items, 0, 
	new DialogInterface.OnClickListener() { 
	public void onClick(DialogInterface dialog, int item) { 

	Toast.makeText(getBaseContext(),items[item], 
	Toast.LENGTH_LONG).show(); 
	if(item==0)
	{
		targetWid=320;
		targetHei=240;
	}
	else if(item==1)
	{
		targetWid=1280;
		targetHei=960;
	}
	else if(item==2)
	{
		targetWid=1600;
		targetHei=1200;
	}
	alert.cancel(); 
	} 
	}); 
	//Toast.makeText(getBaseContext(),targetHei+"x"+targetWid, 
	    //	Toast.LENGTH_LONG).show(); 
	    	
	alert = alt_bld.create(); 
	alert.show(); 

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		System.exit(0);
	}
}