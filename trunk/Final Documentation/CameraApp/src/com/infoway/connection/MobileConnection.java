package com.infoway.connection;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class MobileConnection 
{
    private static Map<String,String> infoMap = new HashMap<String, String>();
	private static Map<Integer,String> phoneType = new HashMap<Integer, String>();
	private static Map<Integer,String> networkType = new HashMap<Integer, String>();

	
	/*Uncomment the network types if the ip address is not displayed*/
    static 
    {
 		//Initialise some mappings    	
    	phoneType.put(0,"None");
    	phoneType.put(1,"GSM");
    	phoneType.put(2,"CDMA");
    
    	//Initialize the network types supported by the Android Telephone Manager Class
    	networkType.put(0,"Unknown");
    	networkType.put(1,"GPRS");
    	networkType.put(2,"EDGE");
    	networkType.put(4,"CDMA");
    	//networkType.put(3,"UMTS");
    	//networkType.put(5,"EVDO_0");
    	//networkType.put(6,"EVDO_A");
    	//networkType.put(7,"1xRTT");
    	//networkType.put(8,"HSDPA");
    	//networkType.put(9,"HSUPA");
    	//networkType.put(10,"HSPA");
    	//networkType.put(11,"IDEN");

        infoMap.put("Cell", "false");
        infoMap.put("Mobile", "false");
        infoMap.put("Wi-Fi", "false");
    }
	
    public static void Update(Context context) 
    {
		// Initialise the network information mapping
        infoMap.put("Cell", "false");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        /*Provides access to information about the telephony services on the device. 
         * Applications can use the methods in this class to determine telephony services and states,
         * as well as to access some types of subscriber information. 
         * Applications can also register a listener to receive notification of telephony state changes. 
         * */
		if( tm != null ) 
		{
            infoMap.put("Cell", "true");
            if ( tm.getCellLocation() != null) 
            { 
                infoMap.put("Cell location", tm.getCellLocation().toString());	 //return the cell location
            }
			infoMap.put("Cell type", getPhoneType(tm.getPhoneType()));
		}

    	// Find out if we're connected to a network
        infoMap.put("Mobile", "false");
        infoMap.put("Wi-Fi", "false");
        
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        /*Class that answers queries about the state of network connectivity. 
         * It also notifies applications when network connectivity changes.*/
        
        NetworkInfo ni = (NetworkInfo) cm.getActiveNetworkInfo(); //Returns the Currently active network
        // may return null if no connection is present
        
       if ( ni != null && ni.isConnected() )	 
       {
    	   	NetworkInterface intf = getInternetInterface();		//returns the active network interface
    	   	
    	   	WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	   	//retrive the wifi service
    	   	
            infoMap.put("IP", getIPAddress(intf));
            //get the address of the mobile as the mobile itself is the host
            
            String type = (String) ni.getTypeName();	//Type can be mobile network eg gprs etc. or wifi
            if ( type.equalsIgnoreCase("mobile") ) 
            {
                infoMap.put("Mobile", "true");
                infoMap.put("Mobile type", getNetworkType(tm.getNetworkType())); 
                infoMap.put("Signal", "Good!");
            } 
            else if (  wifi.isWifiEnabled() ) 
            {
                WifiInfo wi = wifi.getConnectionInfo();                
                infoMap.put("Wi-Fi", "true");
                infoMap.put("SSID",  wi.getSSID());
                //Returns the service set identifier (SSID) of the current 802.11 network.
                infoMap.put("Signal", "Good!");
            }
        } 
       else
       {
    	   infoMap.put("IP", "192.168.43.1");
       }
    }

    public static String getInfo(String key) 
    {                                                              
        return infoMap.containsKey(key)? infoMap.get(key): "";
    }

    private static String getPhoneType(Integer key) 
    {
        if( phoneType.containsKey(key) ) 
        {
            return phoneType.get(key);            
        } 
        else 
        {
            return "unknown";
        }
    }
                   
    private static String getNetworkType(Integer key) 
    {     
        if( networkType.containsKey(key) ) 
        {
            return networkType.get(key);
        } 
        else 
        {
            return "unknown";
        }
    }

    private static String getIPAddress( NetworkInterface intf) 
    //returns the ip address of the mobile using the selected network interface  
    {
        String result = ""; 
        for( Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
            InetAddress inetAddress = enumIpAddr.nextElement();
            result = inetAddress.getHostAddress();
        }
        return result;
    }
    
    private static NetworkInterface getInternetInterface() 
    {
        try 
        {
        	//retrive all the network interfaces available
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                if( ! intf.equals(NetworkInterface.getByName("lo")))	
                //lo stands for loop back & we do not need the loop back interface 	
                {
                    return intf;
                }                   
            }   
        }
        catch (SocketException ex) {}   
        return null;
    }    
}
