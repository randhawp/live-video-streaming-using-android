package com.infoway.camapp;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.infoway.servers.Server;
import com.infoway.activities.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class CameraClass extends Activity 
{
	private Camera camera;
	private int count=0;
	private SurfaceView sv=null;
	private int[] pixels;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sv = (SurfaceView) findViewById(R.id.sview);
        //createDirectory();
        try 
        {
        	//Need So that the Application can now start accepting http requests
			@SuppressWarnings("unused")
			Server server = new Server();
		} 
        catch (IOException e) 
        {
        	Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Streaming Started", Toast.LENGTH_LONG).show();		//Retrive IP & display URL
    }
 
    //Write The bitmap i.e. JPEG image in this case on to the file 
    private void writeToFile(Bitmap image)
	{
		try
		{
			if(count==20)		//As we are using Round Robin while saving the Frames. The max limit is 20 i.e. 0->20
				count=0;

			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ipcam/images/"+(count++)+".jpg");
			f.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
			image.compress(CompressFormat.JPEG, 50, bos);
			bos.close();
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
    
    //This method gives the YUV format of the preview image
    private PreviewCallback previewCallBack = new PreviewCallback() 
    {
		public void onPreviewFrame(byte[] data, Camera camera) 
		{
			try
			{
				Size previewSize = camera.getParameters().getPreviewSize();
				//Change the YUV bytes to RGB int array
				decodeYUV(pixels, data, previewSize.width, previewSize.height);
			
				//Create a jpeg file using RGB array
				Bitmap bitmap = Bitmap.createBitmap(pixels, previewSize.width, previewSize.height, Config.ARGB_8888);
				writeToFile(bitmap);
			}
			catch(Exception e)
			{
				Toast.makeText(CameraClass.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	};
    
    protected void onStart() 
	{
		super.onStart();
		camera = Camera.open();														//Start the camera
		
		Parameters param = camera.getParameters();
		param.set("orientation", "landscape");
		
		//Setting the Resolution
		List<Camera.Size> sizes = param.getSupportedPreviewSizes();
		int height=9999,width=9999;
		for(Camera.Size size : sizes)
		{
			if(height>size.height)
				height=size.height;
			if(width>size.width)
				width=size.width;
		}
		
		param.setPreviewSize(width, height);
		Toast.makeText(this, "Resolution Used: "+width+" X "+height, Toast.LENGTH_LONG).show();
		Size previewSize = camera.getParameters().getPreviewSize();
		
		camera.setPreviewCallback(previewCallBack);									//Set the preview callback function
		pixels = new int[previewSize.width*previewSize.height];						//array used to store the RGB image
		
		SurfaceHolder holder = sv.getHolder();
		//Depreciated method but necessary for lower android versions
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(	new Callback() 
		{
			public void surfaceDestroyed(SurfaceHolder holder) 
			{	}
			public void surfaceCreated(SurfaceHolder holder) 
			{
				try 
				{
					holder.setKeepScreenOn(true);
					camera.setPreviewDisplay(holder);
				} 
				catch (IOException e) 
				{
					Toast.makeText(CameraClass.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				//Start the Camera Preview
				camera.startPreview();
		}
		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) 
		{	}
		});
	}
	
    //Release the camera on exit so as other applications can use the camera
	protected void onStop() 
	{
		super.onStop();
		camera.stopPreview();
		Toast.makeText(this, "Priview Stopped", Toast.LENGTH_SHORT).show();
		camera.release();
	}
	
	//Release the camera on exit so as other applications can use the camera
	@Override
	public void onBackPressed() 
	{
		camera.stopPreview();
		camera.release();
		finish();
		System.exit(0);
	}
	
	//Function which accepts the YUV byte array and converts it into an RGB image
	public void decodeYUV(int[] out, byte[] fg, int width, int height)
    throws NullPointerException, IllegalArgumentException 
    				//out ->int rgb array,	fg->YUV format byte array,	width->previewWidth,	height->previewHeight
    {
		int sz = width * height;
		if (out == null)
		    throw new NullPointerException("buffer out is null");
		if (out.length < sz)
		    throw new IllegalArgumentException("buffer out size " + out.length
		            + " < minimum " + sz);
		if (fg == null)
		    throw new NullPointerException("buffer 'fg' is null");
		if (fg.length < sz)
		    throw new IllegalArgumentException("buffer fg size " + fg.length
		            + " < minimum " + sz * 3 / 2);
		int i, j;
		int Y, Cr = 0, Cb = 0;
		for (j = 0; j < height; j++) 
		{
			int pixPtr = j * width;
		    final int jDiv2 = j >> 1;
		    for (i = 0; i < width; i++) 
		    {
		        Y = fg[pixPtr];
		        if (Y < 0)
		            Y += 255;
		        if ((i & 0x1) != 1) {
		            final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
		            Cb = fg[cOff];
		            if (Cb < 0)
		                Cb += 127;
		            else
		                Cb -= 128;
		            Cr = fg[cOff + 1];
		            if (Cr < 0)
		                Cr += 127;
		            else
		                Cr -= 128;
		        }
		        int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
		        if (R < 0)
		            R = 0;
		        else if (R > 255)
		            R = 255;
		        int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
		                + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
		        if (G < 0)
		            G = 0;
		        else if (G > 255)
		            G = 255;
		        int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
		        if (B < 0)
		            B = 0;
		        else if (B > 255)
		        	B = 255;
		        out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
		    }
		}
	}
}