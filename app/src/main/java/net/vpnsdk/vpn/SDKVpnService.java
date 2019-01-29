package net.vpnsdk.vpn;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import net.vpnsdk.vpn.Common.VpnStatus;
import net.vpnsdk.vpn.ics.CIDRIP;
import net.vpnsdk.vpn.ics.NetworkSpace;
import net.vpnsdk.vpn.util.InetAddressUtils;

public class SDKVpnService extends VpnService  {
    private static String mCurPortal;
    public static String getCurPortal() {
        return mCurPortal;
    }

    public static void setCurPortal(String host) {
        mCurPortal = host;
    }
	class VpnInfo {
		public String  Host;
		public int     Status;
		public boolean ShowNotification;
		public String  Configuration;
		
		public VpnInfo()
		{
			Host = "";
			Status = VpnStatus.IDLE;
			ShowNotification = true;
		}
		
		public void clear()
		{
			Host = "";
			Status = VpnStatus.IDLE;
			ShowNotification = true;
			Configuration = "";
		}
	}

	private final static String               Tag = "SDKVpnService";
//	private Thread 			     mThreadStartVpn = null;
//	private Thread 				 mThreadStopVpn = null;
	private ParcelFileDescriptor mInterface = null;
	private int                  mInterfaceFd = -1;
	private PendingIntent 		 mConfigureIntent;
	private int 				 mMtu = 0;
	private VpnInfo              mVpnInfo = new VpnInfo();
	private final int            MAX_MTU = 1370;
	private final NetworkSpace mRoutes = new NetworkSpace();
	private final NetworkSpace mRoutesv6 = new NetworkSpace();
	private CIDRIP mLocalIP;
	public void onCreate() {
		Log.i(Tag, "onCreate");
	}

	public void onDestroy() {
		Log.i(Tag, "onDestroy");
	}

	public IBinder onBind(Intent intent) {
//		if (IVpnService.class.getName().equals(intent.getAction())) {
			return mBinder;
//		}

//		return null;
	}
	public void onRevoke()
	{
		Log.i(Tag, "onRevoke, will stop l3vpn");
		if (mInterface != null) {
			try {
				mInterface.close();
			} catch (IOException e) {			
				e.printStackTrace();
			}
			mInterface = null;
		}
		VPNManager.getInstance().stopVPN();
	}
	
	public void onTaskRemoved(Intent rootIntent) {
		Toast.makeText(this, "Task removed: " + rootIntent, Toast.LENGTH_LONG)
				.show();
	}

	private final IVpnService.Stub mBinder = new IVpnService.Stub() {

		@Override
		public int protectSocket(int socket) {
			boolean res = SDKVpnService.this.protect(socket);
			return Common.VpnError.ERR_SUCCESS;
		}

		@Override
		public int getVpnInterface(String configuration, String host) throws RemoteException {
			Log.d(Tag, "getVpnInterface: enter");
			mVpnInfo.Configuration = configuration;
			int index = mVpnInfo.Configuration.indexOf("Tunnel Socket:");
			mVpnInfo.Configuration = mVpnInfo.Configuration.substring(0, index);
			mVpnInfo.Host = host;
			Builder builder = new Builder();
			parseAndSet(builder, configuration);
			if (mInterface != null) {
				Log.i(Tag, "getVpnInterface: mInterface is not null, close the previous interface");
				try {
					mInterface.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.i(Tag, "getVpnInterface: Close the previous interface error: " + e.toString());
				}
				Log.i(Tag, "getVpnInterface: Close the previous interface finish");
			} else {
				Log.i(Tag, "getVpnInterface: mInterface is null, do not close it");
			}
			builder.setSession(mVpnInfo.Host);
			if (mMtu != 0)	builder.setMtu(mMtu);
			else            builder.setMtu(MAX_MTU);
			builder.setConfigureIntent(mConfigureIntent);
			
			try {
				mInterface = builder.establish();
			} catch (Exception e) {
				Log.i(Tag, "getVpnInterface: Failed to create interface: " + e.toString());
				mVpnInfo.Configuration = "";
			}
			
			if (mInterface == null) {
				Log.e(Tag, "getVpnInterface: mInterface is null!");
				mVpnInfo.Configuration = "";
			} else {
				mInterfaceFd = mInterface.getFd();
				Log.i(Tag, "getVpnInterface: New interface Created");
				return mInterfaceFd;									
			}				
			return 0;
		}

		@Override
		public void stop() throws RemoteException {
			SDKVpnService.this.stop();
		}
	};
	
	public void stop() {
		Log.i(Tag, "vpn service stop,  will close interface");
		if (mInterface != null) {
			try {
				mInterface.close();
				mInterface = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

    public short getNetMaskLen(String ip) // 255.255.0.0
    {
        short totalLen = 0;
        int len;
        InetAddress addr;
        try {
            addr = InetAddress.getByName(ip);
            byte [] bytes = addr.getAddress();
            for (int i=0; i< bytes.length; i++) {
                len = Integer.bitCount(bytes[i]);
                if (len > 8) len -= 24;
                totalLen += len;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return totalLen;
    }
	private boolean parseSocket(Builder builder, String str)
	{
		str = str.substring("Tunnel Socket:".length()).trim();
		if (str.length() > 0) {
			String[] array = str.split(",");
			int sock;
			for (int i=0; i<array.length; ++i) {
				sock = Integer.parseInt(array[i].trim());
				Log.i(Tag, "Tunnel socket " + sock);
				if (sock != 0) this.protect(sock);
			}
		}
		return true;		
	}
	
	private boolean parseDomain(Builder builder, String str)
	{
		str = str.substring("Search Domain:".length()).trim();
		if (str.length() > 0) builder.addSearchDomain(str);
		return true;		
	}
	
	private boolean parseDns(Builder builder, String str)
	{
		str = str.substring("DNS Server:".length());
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter(",");
		while (scanner.hasNext()) {
			String dns = scanner.next().trim();
			if (dns.length() > 0) builder.addDnsServer(dns);
		}
		return true;
	}
	
	private boolean parseZone(Builder builder, String str)
	{
        //builder.addRoute("0.0.0.0", getNetMaskLen("0.0.0.0"));
        //return true;/*
		str = str.substring("Zone List:".length());
		if (str.length() == 0)
		{
			builder.addRoute("0.0.0.0", getNetMaskLen("0.0.0.0"));
		}
		else
		{
			Scanner scanner = new Scanner(str);
			scanner.useDelimiter(",");
			while (scanner.hasNext()) {
				String zone = scanner.next();
				Scanner scanner2 = new Scanner(zone);
				scanner2.useDelimiter("/");
				String netStr = scanner2.next().trim();
				String maskStr = scanner2.next().trim();
				int len = getNetMaskLen(maskStr);
				Log.i(Tag, "net: [" + netStr + "], mask:[" + maskStr + "]"+ ", len " + len);			
				builder.addRoute(netStr, len);
			}
		}
		return true;
	}
	private boolean parseExcludeList(Builder builder, String str) {
		Log.d(Tag, "parseExcludeList : str =" + str);
		str = str.substring("Exclude List:".length());
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter(",");
		while (scanner.hasNext()) {
			String zone = scanner.next();
			Scanner scanner2 = new Scanner(zone);
			scanner2.useDelimiter("/");
			String netStr = scanner2.next().trim();
			String maskStr = scanner2.next().trim();
			int len = getNetMaskLen(maskStr);
			Log.i(Tag, "net: [" + netStr + "], mask:[" + maskStr + "]"+ ", len " + len);
			mRoutes.addIP(new CIDRIP(netStr, len), false);
			if (scanner2 != null) {
				scanner2.close();
			}
		}
		if (scanner != null) {
			scanner.close();
		}
		return true;
	}

	private boolean parseIP(Builder builder, String str)
	{
		str = str.substring("Assigned IP:".length());
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter("/");	
		String ipStr = scanner.next().trim();
		String maskStr = scanner.next().trim();
		int len = getNetMaskLen(maskStr);
        mLocalIP = new CIDRIP(ipStr, len);
		Log.i(Tag, "ip: [" + ipStr + "], mask:[" + maskStr + "]" + ", len " + len);
		
		builder.addAddress(ipStr, len);
		return true;
	}
	private int parseAndSet(Builder builder, String cfgString)
	{
		Log.i(Tag, "parseAndSet enter");
		mRoutes.clear();
		mRoutesv6.clear();
		Scanner scanner = null;
		try {
			scanner = new Scanner(cfgString);
			String  line;

			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.startsWith("Assigned IP:")) {
					parseIP(builder, line);
				} else if (line.startsWith("Zone List:")) {
					parseZone(builder, line);
				} else if (line.startsWith("Exclude List:")) {
					parseExcludeList(builder, line);
				} else if (line.startsWith("DNS Server:")) {
					parseDns(builder, line);
				} else if (line.startsWith("Search Domain:")) {
					parseDomain(builder, line);
				} else if (line.startsWith("Tunnel Socket:")) {
					parseSocket(builder, line);
				}
				//ipv6
				else if (line.startsWith("Assigned IPv6:")) {
					parseIPv6Address(builder, line);
				} else if (line.startsWith("IPv6Zone List:")) {
					parseIPv6ZoneList(builder, line);
				} else if (line.startsWith("IPv6 Exclude List:")) {
					parseIPv6ExcludeList(line);
				} else if (line.startsWith("IPv6DNS Server:")) {
					parseIPv6DNS(builder, line);
				}
			}
			addLocalNetworksToRoutes();
			addRoute2Tun(builder);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		return 0;
	}

	private boolean parseIPv6DNS(Builder builder, String str) {
		str = str.substring("IPv6DNS Server:".length());
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter(",");
		while (scanner.hasNext()) {
			String dns = scanner.next().trim();
			if (dns.length() > 0) {
				try {
					builder.addDnsServer(dns);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (scanner != null) {
			scanner.close();
		}
		return true;
	}

	private boolean parseIPv6ZoneList(Builder builder, String str) {

		str = str.substring("IPv6Zone List:".length());
		if (str.length() == 0)
		{
			builder.addRoute("::", 0);

		} else {
			Scanner scanner = new Scanner(str);
			scanner.useDelimiter(",");
			while (scanner.hasNext()) {
				String zone = scanner.next();
				Scanner scanner2 = new Scanner(zone);
				scanner2.useDelimiter("/");
				String netStr = scanner2.next().trim();
				String netmask = scanner2.next().trim();
				int len = getIPv6NetMaskLen(netmask);
				Log.i(Tag, "Zone List netv6: [" + netStr + "], maskv6:[" + netmask + "]"+ ", len " + len);
				addRoutev6(netStr, true);
				if (scanner2 != null) {
					scanner2.close();
				}
			}
			if (scanner != null) {
				scanner.close();
			}
		}
		return true;
	}

	private boolean parseIPv6ExcludeList(String str) {

		str = str.substring("IPv6 Exclude List:".length());
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter(",");
		while (scanner.hasNext()) {
			String zone = scanner.next();
			Scanner scanner2 = new Scanner(zone);
			scanner2.useDelimiter("/");
			String netStr = scanner2.next().trim();
			String netmask = scanner2.next().trim();
			int len = getIPv6NetMaskLen(netmask);
			Log.i(Tag, "Zone List netv6: [" + netStr + "], maskv6:[" + netmask + "]"+ ", len " + len);
			addRoutev6(netStr, false);
			if (scanner2 != null) {
				scanner2.close();
			}
		}
		if (scanner != null) {
			scanner.close();
		}
		return true;
	}

	private boolean parseIPv6Address(Builder builder, String str) {
		str = str.substring("Assigned IPv6:".length());
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter("/");
		String ipv6Addr = scanner.next().trim();
		String ipv6mask = scanner.next().trim();
		String ipv6gateway = scanner.next().trim();
		int prefixLen = getIPv6NetMaskLen(ipv6mask);
		Log.i(Tag, "ipv6: [" + ipv6Addr + "], mask:[" + ipv6mask + "]" + ", gateway :" + ipv6gateway + ", len " + prefixLen);

		try {
			builder.addAddress(ipv6Addr, prefixLen);
		} catch (IllegalArgumentException e) {

		}
		if (scanner != null) {
			scanner.close();
		}
		return true;
	}

	private int getIPv6NetMaskLen(String netmask) {
		InetAddress inetAddress;
		int totalLen = 0;
		int len = 0;
		try {
			inetAddress = Inet6Address.getByName(netmask);
			byte[] bytes = inetAddress.getAddress();
			for (int i = 0; i < bytes.length; i ++) {
				len = Integer.bitCount(bytes[i]);
				if (len > 8) {
					len -= 24;
				}
				totalLen += len;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return totalLen;
	}


	private void addLocalNetworksToRoutes() {

		// Add local network interfaces
		String[] localRoutes = NativeLib.getIfconfig();

		for (int i = 0; i < localRoutes.length; i += 3) {
			String intf = localRoutes[i];
			String ipAddr = localRoutes[i + 1];
			String netMask = localRoutes[i + 2];
			Log.d(Tag, "intf " + intf + " addr " + ipAddr + " netMask " + netMask);

			if (intf == null || intf.equals("lo") ||
					intf.startsWith("tun") || intf.startsWith("rmnet"))
				continue;

			if (ipAddr == null || netMask == null) {
				Log.e(Tag, "Local routes are invalid?!" + TextUtils.join("|", localRoutes));
				continue;
			}

			if (ipAddr.equals(mLocalIP.mIp))
				continue;

			if (!InetAddressUtils.isIPv4Address(ipAddr)) {
				continue;
			}
			if (allowLocalLan()) {
				Log.i(Tag, "allow local lan true");
				mRoutes.addIP(new CIDRIP(ipAddr, netMask), false);
			} else {
				Log.i(Tag, "allow local lan false");
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
					mRoutes.addIPSplit(new CIDRIP(ipAddr, netMask), true);
				} else {
					mRoutes.addIP(new CIDRIP(ipAddr, netMask), true);
				}
			}
		}
	}

	private boolean allowLocalLan() {
		return ((NativeLib.getResourceFlags() & NativeLib.VPN_RESOURCE_EXCLUDEDCLIENTSUBNET) != 0);
	}
	private void addRoute2Tun(Builder builder) {
		// Exclude AG IP

		String address = "121.10.6.251";//getCurPortal();
		Log.e(Tag,"address:"+address);
		if (!InetAddressUtils.isIPv4Address(address) && !InetAddressUtils.isIPv6Address((address))) {
			try {
				InetAddress ip = InetAddress.getByName(address);
				address = ip.getHostAddress();
			} catch (Exception e) {
				Log.e(Tag, e.toString());
				e.printStackTrace();
				address = null;
			}
		}
		if (InetAddressUtils.isIPv4Address(address)) {
			mRoutes.addIP(new CIDRIP(address, 32), false);
		}

		Collection<NetworkSpace.ipAddress> positiveIPv4Routes = mRoutes.getPositiveIPList();
		Collection<NetworkSpace.ipAddress> positiveIPv6Routes = mRoutesv6.getPositiveIPList();


		for (NetworkSpace.ipAddress route : positiveIPv4Routes) {
			try {
				builder.addRoute(route.getIPv4Address(), route.networkMask);
			} catch (IllegalArgumentException ia) {
				Log.e(Tag, "route rejected " + route + " " + ia.getLocalizedMessage());
			}
		}

		for (NetworkSpace.ipAddress route6 : positiveIPv6Routes) {
			try {
				builder.addRoute(route6.getIPv6Address(), route6.networkMask);
			} catch (IllegalArgumentException ia) {
				Log.e(Tag, "route_rejected " + route6 + " " + ia.getLocalizedMessage());
			}
		}
	}
	public void addRoutev6(String network, boolean included) {
		String[] v6parts = network.split("/");
		// Tun is opened after ROUTE6, no device name may be present
		try {
			Inet6Address ip = (Inet6Address) InetAddress.getAllByName(v6parts[0])[0];
			int mask = Integer.parseInt(v6parts[1]);
			mRoutesv6.addIPv6(ip, mask, included);

		} catch (UnknownHostException e) {
			Log.e(Tag, "" + e.getMessage());
		}


	}
}
