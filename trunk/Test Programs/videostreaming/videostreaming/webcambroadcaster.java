import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.DataSource;
import javax.media.util.BufferToImage;

/**
 * A disposable class that uses JMF to serve a still sequence captured from a
 * webcam over a socket connection. It doesn't use TCP, it just blindly
 * captures a still, JPEG compresses it, and pumps it out over any incoming
 * socket connection.
 * 
 * @author Tom Gibara
 *
 */

public class WebcamBroadcaster {

	public static boolean RAW = false;
	
	
	private static Player createPlayer(int width, int height) {
		try {
			Vector<CaptureDeviceInfo> devices = CaptureDeviceManager.getDeviceList(null);
			for (CaptureDeviceInfo info : devices) {
				
				Format[] formats = info.getFormats();
				for (Format format : formats) {
					if (!(format instanceof RGBFormat)) continue;
					RGBFormat rgb = (RGBFormat) format;
					Dimension size = rgb.getSize();
					if (size.width != width || size.height != height) continue;
					if (rgb.getPixelStride() != 3) continue;
					if (rgb.getBitsPerPixel() != 24) continue;
					if ( rgb.getLineStride() != width*3 ) continue;
					MediaLocator locator = info.getLocator();
					DataSource source = Manager.createDataSource(locator);
					source.connect();
					System.out.println();
					((CaptureDevice)source).getFormatControls()[0].setFormat(rgb);
					return Manager.createRealizedPlayer(source);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			e.printStackTrace();
		} catch (NoDataSourceException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		int[] values = new int[args.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.parseInt(args[i]);
		}
		
		WebcamBroadcaster wb;
		if (values.length == 0) {
			wb = new WebcamBroadcaster();
		} else if (values.length == 1) {
			wb = new WebcamBroadcaster(values[0]);
		} else if (values.length == 2) {
			wb = new WebcamBroadcaster(values[0], values[1]);
		} else {
			wb = new WebcamBroadcaster(values[0], values[1], values[2]);
		}
		
		wb.start();
	}
	
	public static final int DEFAULT_PORT = 9889;
	public static final int DEFAULT_WIDTH = 320;
	public static final int DEFAULT_HEIGHT = 240;
	
	private final Object lock = new Object();
	
	private final int width;
	private final int height;
	private final int port;
	
	private boolean running;
	
	private Player player;
	private FrameGrabbingControl control;
	private boolean stopping;
	private Worker worker;
	
	public WebcamBroadcaster(int width, int height, int port) {
		this.width = width;
		this.height = height;
		this.port = port;
	}

	public WebcamBroadcaster(int width, int height) {
		this(width, height, DEFAULT_PORT);
	}

	public WebcamBroadcaster(int port) {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, port);
	}

	public WebcamBroadcaster() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_PORT);
	}
	
	public void start() {
		synchronized (lock) {
			if (running) return;
			player = createPlayer(width, height);
			if (player == null) {
				System.err.println("Unable to find a suitable player");
				return;
			}
			player.start();
			control = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
			worker = new Worker();
			worker.start();
			running = true;
		}
	}

	public void stop() throws InterruptedException {
		synchronized (lock) {
			if (!running) return;
			if (player != null) {
				control = null;
				player.stop();
				player = null;
			}
			stopping = true;
			running = false;
			worker = null;
		}
		try {
			worker.join();
		} finally {
			stopping = false;
		}
	}

	private class Worker extends Thread {
		
		private final int[] data = new int[width*height];
		
		@Override
		public void run() {
			ServerSocket ss; 
			try {
				ss = new ServerSocket(port);
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			while(true) {
				FrameGrabbingControl c;
				synchronized (lock) {
					if (stopping) break;
					c = control;
				}
				Socket socket = null;
				try {
					socket = ss.accept();
					
					Buffer buffer = c.grabFrame();
					BufferToImage btoi = new BufferToImage((VideoFormat)buffer.getFormat());
					BufferedImage image = (BufferedImage) btoi.createImage(buffer);
					
					if (image != null) {
						OutputStream out = socket.getOutputStream();
						if (RAW) {
							image.getWritableTile(0, 0).getDataElements(0, 0, width, height, data);
							image.releaseWritableTile(0, 0);
							DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(out));
							for (int i = 0; i < data.length; i++) {
								dout.writeInt(data[i]);
							}
							dout.close();
						} else {
							ImageIO.write(image, "JPEG", out);
						}
					}
					
					socket.close();
					socket = null;
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (socket != null)
						try {
							socket.close();
						} catch (IOException e) {
							/* ignore */
						}
				}
				
			}
			
			try {
				ss.close();
			} catch (IOException e) {
				/* ignore */
			}
		}

	}
	
}
