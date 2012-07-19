package com.pradnya.CameraApp;
import java.io.IOException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity 
{
	Camera c;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
      /*  Button btn = (Button) findViewById(R.id.btnCapture);
        btn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				c.takePicture(null, jpeg, jpeg);
			}
		});*/
    }
    
    PictureCallback jpeg = new PictureCallback() {
		
		
		public void onPictureTaken(byte[] data, Camera camera) {
	
			if( data != null ) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"title", "desc");
				
			}
		}
	};
	
	
	protected void onStart() {
		
		super.onStart();
		c = Camera.open();
		
		SurfaceView sv = (SurfaceView) findViewById(R.id.sview);
		SurfaceHolder holder = sv.getHolder();
		
		Canvas can = new Canvas();
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		can.drawLine(10, 10, 50, 20, paint );
		//sv.draw(can);
		
		
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(
				new Callback() {
			
			
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
			
			
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					c.setPreviewDisplay(holder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				c.startPreview();
				
			}
			
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		c.stopPreview();
		c.release();
	}
	
	
}