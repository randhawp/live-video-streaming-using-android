package com.tomgibara.android.camera;

import android.graphics.Canvas;
import android.hardware.CameraDevice;

/**
 * A CameraSource implementation that obtains its bitmaps directly from the
 * device camera.
 * 
 * @author Tom Gibara
 *
 */

public class GenuineCamera implements CameraSource {

	private final int width;
	private final int height;
	
	private CameraDevice device = null;
	
	public GenuineCamera(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public boolean open() {
		if (device != null) return true;
		device = CameraDevice.open();
		if (device == null) return false;
		
		//parameters for the device mostly as specified in sample app
		CameraDevice.CaptureParams param = new CameraDevice.CaptureParams();
		param.type = 1; // preview
		param.srcWidth = 1280;
		param.srcHeight = 960;
		param.leftPixel = 0;
		param.topPixel = 0;
		param.outputWidth = width;
		param.outputHeight = height;
		param.dataFormat = 2; // RGB_565
	
		//attempt to configure the device here
		if (!device.setCaptureParams(param)) {
			device.close();
			device = null;
			return false;
		}
		
		return true;
	}
	
	@Override
	public void close() {
		if (device == null) return;
		device.close();
		device = null;
	}
	
	@Override
	public boolean capture(Canvas canvas) {
		if (device == null) return false;
		return device.capture(canvas);
	}

}
