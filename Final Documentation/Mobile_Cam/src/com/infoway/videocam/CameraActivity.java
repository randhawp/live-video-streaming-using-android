package com.infoway.videocam;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.infoway.videocam.R;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Toast;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;


public class CameraActivity extends Activity
implements SurfaceHolder.Callback,MediaRecorder.OnInfoListener
{
	//Specialized class object of NanoHTTPD server
	private Server server=null;	
	private Camera camera;
	private MediaRecorder mediaRecorder; 
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private int fileCounter=0;
	public static final String rootDir = "/ipcam/"; 
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //Initialize the server object so now we can accept http requests
        startServer();
        
        //Initialize the camera & some other mediaRecorder attributes
        createSurface();
    }

	//Initialize the server object so now we can accept http requests
    private void startServer()
    {
    	try 
    	{
			server=new Server();
			Toast.makeText(this,"Server Started", Toast.LENGTH_SHORT).show();
		} 
    	catch (IOException e) 
    	{
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
    }
    
    //Initialize the camera & some other mediaRecorder attributes
    private void createSurface()
    {
    	camera = getCameraInstance();
    	surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
    	surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mediaRecorder = new MediaRecorder();
    	mediaRecorder.setOnInfoListener(this);
    }
    
    // A safe way to get an instance of the Camera object. 
    private Camera getCameraInstance()
    {
        Camera camera = null;
        try 
        {
        	camera = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e)
        {
        	Toast.makeText(CameraActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return camera; // returns null if camera is unavailable
    }

    //Set up the Video Recorder Attributes
    //These attributes need to be set up in the start & explicitly after calling release() of the media recorder 
    private void prepareVideoRecorder()
    {
        // Step 1: Unlock and set camera to MediaRecorder
    	// Unlock the camera so the mediaRecorder can use the camera
        camera.unlock();													
        mediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        
        // Setp 3: Set profile
        CamcorderProfile targetProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
       	targetProfile.videoFrameWidth = 320;
        targetProfile.videoFrameHeight = 240;
        targetProfile.videoBitRate = 512*1024;
        targetProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
        targetProfile.audioCodec = MediaRecorder.AudioEncoder.AMR_NB;
        targetProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        mediaRecorder.setProfile(targetProfile);
        mediaRecorder.setMaxDuration(5000);									//Create only 5sec videos

        // Step 4: Set the preview output
		startRecording();
    }
    
    //Setting the recording fileName & starting the Recording
    private void startRecording()
    {
    	mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getPath()+rootDir+"recording.mp4");
    	mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
    	
        // Step 5: Prepare configured MediaRecorder
        try 
        {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } 
        catch (IllegalStateException e) 
        {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            releaseMediaRecorder();
        } 
        catch (IOException e) 
        {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            releaseMediaRecorder();
        }
    }
    
    //Clear the media recorder object
    private void releaseMediaRecorder()
    {
        if (mediaRecorder != null) 
        {
            mediaRecorder.reset();   		// clear recorder configuration
            mediaRecorder.release(); 		// release the recorder object
            mediaRecorder = null;
            camera.lock();			 		// lock camera for later use
            camera.startPreview();
        }
    }
        
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) 
	{
        if (holder.getSurface() == null)
        {
          return;
        }
        try 
        {
            camera.stopPreview();
        } 
        catch (Exception e)
        {
        	Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
	}
	public void surfaceCreated(SurfaceHolder holder) 
	{
		  try 
		  {
			  	//Prevent turning off the screen
			  	holder.setKeepScreenOn(true);
	            camera.setPreviewDisplay(holder);
	            camera.startPreview();
	            prepareVideoRecorder();
		  } 
		  catch (Exception e) 
		  {
			  Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
		  }
	}


	public void surfaceDestroyed(SurfaceHolder holder) 
	{}

	//returns the counter which is used to name the recorded file.
	private int getCounter()
	{
		if(fileCounter==10)
		{
			fileCounter=0;
			return fileCounter;
		}
		else
		{
			return fileCounter++;
		}
	}
	
	//Called once the recording is finished. In this case every after 5seconds as the maxSize of the recording is set to 5 secs.
	/* Initially the video is saved in the recording.mp4 but we have to create a copy of the same. 
	 * This copy will now be accessed by the index.html file. Recording is done in a separate file so there is no collision
	 * while creating the video */ 
	public void onInfo(MediaRecorder mr, int what, int extra) 
	{
		try
		{
			//Stop the recording
			mediaRecorder.stop();
			camera.lock(); 			// lock camera for later use
			Thread.sleep(200);		// Used for create a delay so as the file i.e the video is completely created
			
			//Copy the content of recording to another file byte by byte
			FileInputStream fin=new FileInputStream(Environment.getExternalStorageDirectory().getPath()+rootDir+"recording.mp4");
			FileOutputStream fout=new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+rootDir+getCounter()+".mp4");
			byte[] b=new byte[fin.available()];
			fin.read(b);
			fout.write(b);
			fin.close();
			fout.close();
		}
		catch (Exception e) 
		{
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
		}
		prepareVideoRecorder();
	}
	
	@Override
    protected void onStop() 
    {
		//Release the recourses once the activity has stopped
    	mediaRecorder.stop();
    	server.end();
    	camera.release();
    	super.onStop();
    	finish();
    	System.exit(0);
    }
    
    @Override
    protected void onDestroy() 
    {
    	//Release the recourses once the activity has destroyed
    	mediaRecorder.stop();
    	server.end();
    	camera.release();
    	super.onDestroy();
    	finish();
    	System.exit(0);
    }
}