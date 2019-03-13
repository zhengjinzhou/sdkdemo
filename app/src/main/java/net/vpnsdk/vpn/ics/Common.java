package net.vpnsdk.vpn.ics;


public interface Common {

	public final class VpnMsg {
		public static final int MSG_VPN_CONNECTING      = 1;
		public static final int MSG_VPN_CONNECTED       = 2;
		public static final int MSG_VPN_DISCONNECTING   = 3;
		public static final int MSG_VPN_DISCONNECTED    = 4;
		public static final int MSG_VPN_CONNECT_FAILED  = 5;
		public static final int MSG_VPN_CONFIG_REQUEST  = 6;
		public static final int MSG_VPN_RECONNECTING    = 7;	
		public static final int MSG_VPN_PROTECT_SOCK    = 8;
		public static final int MSG_VPN_GET_RESOURCE    = 22;
		public static final int MSG_VPN_CV_GLOBAL       = 12;
		public static final int MSG_VPN_CV_USER         = 20;
		public static final int MSG_VPN_SSO             = 23;
	}	
	
	public final class VpnStatus {
		public final static int IDLE = 0;
		public final static int CONNECTING = 1;
		public final static int CONNECTED = 2;
		public final static int DISCONNECTING = 3;
		public final static int RECONNECTING = 4;
	}

	// see ANDROID_LOG_xxx
	public final class LogLevel {
		public final static int LOG_DEBUG = 3;
		public final static int LOG_INFO = 4;
		public final static int LOG_WARNING = 5;
		public final static int LOG_ERROR = 6;
	}
	
	public final class AuditLogLevel {
		public final static int AUDIT_EMERG   = 0;   /* system is unusable */
		public final static int AUDIT_ALERT   = 1;   /* action must be taken immediately */
		public final static int AUDIT_CRIT    = 2;   /* critical conditions */
		public final static int AUDIT_ERR     = 3;   /* error conditions */
		public final static int AUDIT_WARNING = 4;   /* warning conditions */
		public final static int AUDIT_NOTICE  = 5;   /* normal but significant condition */
		public final static int AUDIT_INFO    = 6;   /* informational */
		public final static int AUDIT_DEBUG   = 7;   /* debug-level messages */
	}

	public final class AppType {
		public final static int APP_TYPE_TCP     = 1;
		public final static int APP_TYPE_RDP     = 2;
		public final static int APP_TYPE_HTTP    = 3;
		public final static int APP_TYPE_UDP     = 4;
		public final static int APP_TYPE_SIP_UDP = 5;
	}
	
	public final class ApplicationInfo {
		public String  Name;
		public String  Domain;
		public String  RealIP;
		public String  RealPort;
		public String  RealMask;
		public String  VirtualIP;
		public String  VirtualPort;
		public int     Type;
	}
	
	public final class VpnSpecialError {
		public final static int ERR_NO_AAA = 1;
	}
	
	public final class VpnError {
		public final static int  ERR_SUCCESS 					    = 0; // success
		public final static int  ERR_FAILURE						= 1;
		public final static int  ERR_SSL_READ 					    = 2;
		public final static int  ERR_SSL_WRITE 					    = 3;
		public final static int  ERR_SSL_CONN 					    = 4;
		public final static int  ERR_SSL_CLIENT_CERT			    = 5;
		public final static int  ERR_SESS_INVALID				    = 6; // session is invalid
		public final static int  ERR_SESS_TIMEOUT				    = 7; // session is timeout
		public final static int  ERR_MEM_ALLOC					    = 8;
		public final static int  ERR_SOCK_CREATE					= 9;
		public final static int  ERR_SOCK_ACCEPT					= 10;
		public final static int  ERR_SOCK_RECV					    = 11;
		public final static int  ERR_SOCK_CLOSE					    = 12;
		public final static int  ERR_SOCK_BIND					    = 13;
		public final static int  ERR_SOCK_LISTEN					= 14;
		public final static int  ERR_SOCK_SEND					    = 15;
		public final static int  ERR_SOCK_CONN					    = 16;
		public final static int  ERR_ITEM_NOT_FOUND				    = 17;
		public final static int  ERR_PARSE_COOKIE				    = 18;
		public final static int  ERR_HTTP_NOT_REDIR				    = 19;
		public final static int  ERR_HTTP_NO_LOCATION			    = 20;
		public final static int  ERR_CONTROL_SOCK_BIND			    = 21;
		public final static int  ERR_APP_CONN_LIMIT				    = 22; // too many connections
		public final static int  ERR_BUF_SIZE					    = 23;
		public final static int  ERR_UNKNOWN_HOST				    = 24;
		public final static int  ERR_STILL_RUNNING				    = 25; // still running, don't start again
		public final static int  ERR_NOT_RUNNING					= 26; // not running, don't stop
		public final static int  ERR_THREAD_JOIN					= 27;
		public final static int  ERR_THREAD_CREATE				    = 28;
		public final static int  ERR_SOCK_SELECT					= 29;
		public final static int  ERR_SERVER_CONFIG				    = 30;
		public final static int  ERR_SESS_SECOND_LOGIN			    = 31;
		public final static int  ERR_CERT_PASS					    = 32;
		public final static int  ERR_CERT_PARSE					    = 33;
		public final static int  ERR_CERT_FILE_READ				    = 34;
		public final static int  ERR_CTL_SOCK_CONN				    = 35;
		public final static int  ERR_CTL_SOCK_SEND				    = 36;
		public final static int  ERR_WRONG_USER_PASS				= 37;
		public final static int  ERR_GET_AAA_METHOD				    = 38;
		public final static int  ERR_CONFIG_L3VPN				    = 39;
		public final static int  ERR_GET_L3VPN_CONFIG			    = 40;
		public final static int  ERR_INTERRUPTED					= 41;
		public final static int  ERR_PARAM_INVALID                  = 42;
		public final static int  ERR_TUN_CREATE		                = 43;
		public final static int  ERR_TUN_CONFIG		                = 44;
		public final static int  ERR_SETUP_UDP					    = 45;
		public final static int  ERR_UDP_SEND					    = 46;
		public final static int  ERR_UDP_RECV					    = 47;
		public final static int  ERR_TUN_DOWN                       = 48;
		public final static int  ERR_SERVER_AG                      = 49;
		public final static int  ERR_SERVER_NO_RESP		            = 50;
		public final static int  ERR_CLIENT_SECURITY		        = 51;
		public final static int  ERR_CHOOSE_METHOD                  = 52;
		public final static int  ERR_UNSUPPORT_METHOD               = 53;
		public final static int  ERR_SHUTDOWN_SOCKET                = 54;
		public final static int  ERR_HTTP_PROXY_START_FAILED        = 55;
		public final static int  ERR_SOCK_PROXY_START_FAILED        = 56;
		public final static int  ERR_HTTP_PROXY_STOP_FAILED         = 57;
		public final static int  ERR_SOCK_PROXY_STOP_FAILED         = 58;
		public final static int  ERR_HTTP_SOCK_PROXY_STOP_FAILED    = 59;
		public final static int  ERR_CALLBACK_FAILED                = 60;
		public final static int  ERR_DEVID_APPROVE_PENDING          = 61;
		public final static int  ERR_DEVID_APPROVE_DENY             = 62;
		public final static int  ERR_DEVID_USER_LIMIT               = 63;
		public final static int  ERR_DEVID_DEV_LIMIT                = 64;
		public final static int  ERR_DEVID_UNREG	                = 65;
		public final static int  ERR_CREATE_SESSION                 = 66;
		public final static int  ERR_CERT_NO                        = 67;
		public final static int  ERR_CERT_INVALID_SIGNTURE          = 68;
		public final static int  ERR_CERT_UNTRUSTED                 = 69;
		public final static int  ERR_CERT_EXPIRED                   = 70;
		public final static int  ERR_CERT_INVALID                   = 71;
		public final static int  ERR_CERT_REVOKED                   = 72;
		public final static int  ERR_CERT_SERVER_NO                 = 73;
		public final static int  ERR_CERT_SERVER_INVALID_SIGNTURE   = 74;
		public final static int  ERR_CERT_SERVER_UNTRUSTED          = 75;
		public final static int  ERR_CERT_SERVER_EXPIRED            = 76;
		public final static int  ERR_CERT_SERVER_INVALID            = 77;
		public final static int  ERR_CERT_SERVER_REVOKED            = 78;
		public final static int  ERR_CHANGE_PASSWORD                = 79;
		public final static int  ERR_AUTHORIZE_FAILED               = 80;
		public final static int  ERR_CONNECT_TIMEOUT                = 81;
		public final static int  ERR_PROXY_AUTH                     = 82;
		public final static int  ERR_SMS_GET_PHONE                  = 83;
		public final static int  ERR_SMS_SEND                       = 84;
		public final static int  ERR_SMS_TOO_MANY_RETRY             = 85;
		public final static int  ERR_IPC                            = 86;
		public final static int  ERR_HWID_DENY                      = 87;
		public final static int  ERR_HWID_PENDING                   = 88;
		public final static int  ERR_CS_DOWNLOAD                    = 89;
		public final static int  ERR_FILE_WRITE                     = 90;
		public final static int  ERR_FILE_OPEN                      = 91;
		public final static int  ERR_FILE_READ                      = 92;

		// syferLock start 
		public final static int ERR_DD_CLIENT_VERIFY_FAIL       	= 93;
		public final static int ERR_CLIENT_SECURITY_FAIL        	= 94;
		public final static int ERR_CLIENT_NEED_UPDATE         	 	= 95;
		public final static int ERR_HTTP_NO_POST_URL            	= 96;
		public final static int ERR_HTTP_NO_REALM               	= 97;
		public final static int ERR_GET_LOGIN_RESP_FAILED       	= 98;
		public final static int ERR_POST_START_RESP_FAILED      	= 99;
		public final static int ERR_HTTP_NO_CSRF                	= 100;
		public final static int ERR_HTTP_NO_DYNAMIC_DATA        	= 101;
		public final static int ERR_GET_GRID_RESP_FAILED        	= 102;
		public final static int ERR_POST_GRID_RESP_FAILED       	= 103;
		public final static int ERR_GET_BASE64_RESP_FAILED      	= 104;
		public final static int ERR_HTTP_NO_SECGRID             	= 105;
		public final static int ERR_DECODE_FAILED               	= 106;
		public final static int ERR_POST_SECGRID_RESP_FAILED    	= 107;
		public final static int ERR_NOT_SYFERLOCK_AUTH_SITE     	= 108;
		public final static int   ERR_NAME_ADDR_NUM_MISMATCH        = 109;
		public final static int   ERR_INVALID_IP_ADDRESS            = 110;
		public final static int   ERR_INVALID_DOMAIN_NAME           = 111;
		public final static int   ERR_USER_LOCKED                   = 112;

		public final static int ERR_MAX                         	= 113;
		
	}
    
	public final class MsgID {
		public final static int  ERR_ACCOUNT_LOCKED                 = 560;
	}
	
	/*HashMap<Integer, Integer> errorMsgInfo = new HashMap<Integer, Integer>(){
		{
			put(MsgID.ERR_ACCOUNT_LOCKED, R.string.account_locked_hint);
		}
	};*/
	
	class ControlPacket {
		public int type;
		public int result;
		public int data_len;
		public byte[] data;
	}
	
	public final class ControlCode {
		
		public final static int COM_NOT_USE = 0;

		public final static int CMD_START_CLIENTAPP   = 1;
		public final static int CMD_STOP_CLIENTAPP    = 2;

		public final static int CMD_START_L3VPN = 20;
		public final static int CMD_STOP_L3VPN = 21;
		public final static int CMD_GET_L3VPN_CONFIG = 22;
		public final static int CMD_GET_L3VPN_STAT = 23;
		public final static int CMD_GET_L3VPN_SITECOOKIES = 24;

		public final static int CMD_CLENTAPP_CONN_FAIL    = 40;
		public final static int CMD_CLENTAPP_CONN_SUCCESS = 41;
		public final static int CMD_CLENTAPP_EXIT         = 42;

		public final static int CMD_L3VPN_CONN_FAIL     = 60;
		public final static int CMD_L3VPN_CONN_SUCCESS  = 61;
		public final static int CMD_L3VPN_EXIT          = 62;
		public final static int CMD_L3VPN_CONFIG_REQ    = 63;
		public final static int CMD_L3VPN_INFO          = 64;
		
		public final static int CMD_UDP_ERROR    = 74;
		public final static int CMD_UDP_WARNING  = 75;
		public final static int CMD_UDP_INFO     = 76;
		public final static int CMD_SETUP_ERROR  = 77;
	}
	
	public final class TrafficDispatchRule {
		public final static int AlltoTCP 		   = 0;
		public final static int TCPtoTCP_OtherUDP  = 1;
		public final static int TCPtoUDP_OtherTCP  = 2;
		public final static int AlltoUDP 		   = 3;
	}	
	
	public final class ServerType {
		public final static int INVALID_DEVICE = 0;
		public final static int SPX_VPN_DEVICE = 1;
		public final static int AG_VPN_DEVICE = 2;
		public final static int MN_DEVICE = 3;
		public final static int DUMMY_DEVICE = 4;
	}

	public final class VpnInfo {
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
	
	class VpnErrorInfo {
		public int      ErrorCode;
		public String	Information;
		public String   Suggestion;
		public VpnErrorInfo (int err, String msg, String sugg) {
			ErrorCode = err;
			Information = msg;
			Suggestion = sugg;
		}
	};
	
	VpnErrorInfo VpnErrorInfoArray[] = new VpnErrorInfo[] {
			new VpnErrorInfo (
			  VpnError.ERR_SUCCESS,
			  "",
			  ""
			),

			new VpnErrorInfo (
			  VpnError.ERR_FAILURE,
			  "generic failure",
			  ""
			),
			new VpnErrorInfo (
			  VpnError.ERR_SSL_READ,
			  "ssl read failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SSL_WRITE,
			  "ssl read failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SSL_CONN,
			  "ssl connection failed",
			  "please check network connection and your certificate"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SSL_CLIENT_CERT,
			  "server need client certificate",
			  "please specify a certificate file"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SESS_INVALID,
			  "session invalid",
			  "please login and try again"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SESS_TIMEOUT,
			  "session timeout",
			  "please login and try again"
			),

			new VpnErrorInfo (
			  VpnError.ERR_MEM_ALLOC,
			  "allocate memory failed",
			  "please kill all Array Netwoks' process and try again"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_CREATE,
			  "socket create failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_ACCEPT,
			  "socket accept failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_RECV,
			  "socket read failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_CLOSE,
			  "socket close failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_BIND,
			  "socket bind failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_LISTEN,
			  "socket listen failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_SEND,
			  "socket send failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_CONN,
			  "socket connect failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_ITEM_NOT_FOUND,
			  "item not found",
			  "item not found"
			),

			new VpnErrorInfo (
			  VpnError.ERR_PARSE_COOKIE,
			  "parse cookie failed",
			  "parse cookie failed"
			),

			new VpnErrorInfo (
			  VpnError.ERR_HTTP_NOT_REDIR,
			  "not http redirect",
			  "not http redirect"
			),

			new VpnErrorInfo (
			  VpnError.ERR_HTTP_NO_LOCATION,
			  "http header don't have a location",
			  "http header don't have a location"
			),

			new VpnErrorInfo (
			  VpnError.ERR_CONTROL_SOCK_BIND,
			  "bind control socket failed",
			  "please kill all vpn process and try again"
			),

			new VpnErrorInfo (
			  VpnError.ERR_APP_CONN_LIMIT,
			  "exceed application limit",
			  "please check application count"
			),

			new VpnErrorInfo (
		      VpnError.ERR_BUF_SIZE,
			  "buffer size is small",
			  "buffer size is small"
			),

			new VpnErrorInfo (
			  VpnError.ERR_UNKNOWN_HOST,
			  "unknown host",
			  "please check the host name"
			),

			new VpnErrorInfo (
			  VpnError.ERR_STILL_RUNNING,
			  "process or thread is still running",
			  "process or thread is still running"
			),

			new VpnErrorInfo (
			  VpnError.ERR_NOT_RUNNING,
			  "process or thread is not running",
			  "process or thread is not running"
			),

			new VpnErrorInfo (
					  VpnError.ERR_THREAD_JOIN,
			  "join thread failed",
			  "please kill all Array Netwoks' process and try again"
			),

			new VpnErrorInfo (
					   VpnError.ERR_THREAD_CREATE,
			  "create thread failed",
			  "please kill all Array Netwoks' process and try again"
			),

			new VpnErrorInfo (
					   VpnError.ERR_SOCK_SELECT,
			  "socket select failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
					   VpnError.ERR_SERVER_CONFIG,
			  "server configuration is invalid",
			  "please check server configuration"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SESS_SECOND_LOGIN,
			  "same user has logged in from another computer",
			  "please logout from all computer and try again"
			),

			new VpnErrorInfo (
			  VpnError.ERR_CERT_PASS,
			  "password of certificate is wrong",
			  "please check certificate password"
			),

			new VpnErrorInfo (
			VpnError.ERR_CERT_PARSE,
			  "parse certificate file failed",
			  "please check your certificate file and your permission"
			),

			new VpnErrorInfo (
					VpnError.ERR_CERT_FILE_READ,
			  "read certificate file failed",
			  "please check your certificate file and your permission"
			),

			new VpnErrorInfo (
					 VpnError.ERR_CTL_SOCK_CONN,
			  "connect to control sock failed",
			  "please kill all Array Netwoks' process and try again"
			),

			new VpnErrorInfo (
					VpnError.ERR_CTL_SOCK_SEND,
			  "send to control sock failed",
			  "please kill all Array Netwoks' process and try again"
			),

			new VpnErrorInfo (
					 VpnError.ERR_WRONG_USER_PASS,
			  "login failed",
			  "please check your username and password"
			),

			new VpnErrorInfo (
					 VpnError.ERR_GET_AAA_METHOD,
			  "get AAA method failed",
			 "please check you virtual site configuration or network connection"
			),

			new VpnErrorInfo (
					 VpnError.ERR_CONFIG_L3VPN,
			  "config l3vpn tunnel failed",
			  "please check your installation"
			),

			new VpnErrorInfo (
					 VpnError.ERR_GET_L3VPN_CONFIG,
			  "get configuration of l3vpn failed",
			  "please check you virtual site configuration or network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_INTERRUPTED,
			  "",
			  ""
			),

			new VpnErrorInfo (
					 VpnError.ERR_PARAM_INVALID,
			  "parameter is invalid",
			  "parameter is invalid"
			),

			new VpnErrorInfo (
					 VpnError.ERR_TUN_CREATE,
			  "failed to create tun device",
			  "failed to create tun device"
			),

			new VpnErrorInfo (
					VpnError.ERR_TUN_CONFIG,
			  "failed to configure tun device",
			  "failed to configure tun device"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SETUP_UDP,
			  "failed to setup speed tunnel",
			  "failed to setup speed tunnel"
			),

			new VpnErrorInfo (
			  VpnError.ERR_UDP_SEND,
			  "udp send failed",
			  "please check network connection"
			),

			new VpnErrorInfo (
			  VpnError.ERR_UDP_RECV,
			  "udp receive failed",
			  "please check network connection"
			),	

			new VpnErrorInfo (
			  VpnError.ERR_TUN_DOWN,
			  "tun device is down",
			  "tun device is down"
			),
					
			new VpnErrorInfo (
			  VpnError.ERR_SERVER_AG,
			  "the vpn server is not supported",
			  "please contract your IT administrator"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_SERVER_NO_RESP,
			  "no response from server",
			  "please check network connection"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CLIENT_SECURITY,
			  "Client Security is not supported",
			  "please contract your IT administrator"
			),

			new VpnErrorInfo (
			  VpnError.ERR_CHOOSE_METHOD,
			  "Too much methods",
			  "please contract your IT administrator"
			),

			new VpnErrorInfo (
			  VpnError.ERR_UNSUPPORT_METHOD,
			  "Method is not supported",
			  "please contract your IT administrator"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SHUTDOWN_SOCKET,
			  "shut down socket failed",
			  "shut down socket failed"
			),

			new VpnErrorInfo (
			  VpnError.ERR_HTTP_PROXY_START_FAILED,
			  "http proxy start failed",
			  "the http proxy failed to start"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_PROXY_START_FAILED,
			  "socket proxy start failed",
			  "the socket proxy failed to start"
			),

			new VpnErrorInfo (
			  VpnError.ERR_HTTP_PROXY_STOP_FAILED,
			  "http proxy stop failed",
			  "the http proxy failed to stop"
			),

			new VpnErrorInfo (
			  VpnError.ERR_SOCK_PROXY_STOP_FAILED,
			  "socket proxy stop failed",
			  "the socket proxy failed to stop"
			),

			new VpnErrorInfo (
			  VpnError.ERR_HTTP_SOCK_PROXY_STOP_FAILED,
			  "proxy stop failed",
			  "the http proxy and the socket proxy failed to stop"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CALLBACK_FAILED,
			  "upper layer callback failed",
			  "please check your input"
			),

			new VpnErrorInfo (
			  VpnError.ERR_DEVID_APPROVE_PENDING,
			  "Your device is not approved yet",
			  "Please contact your administrator"
			),

			new VpnErrorInfo (
			  VpnError.ERR_DEVID_APPROVE_DENY,
			  "Your device is denied",
			  "Please contact your administrator"
			),

			new VpnErrorInfo (
			  VpnError.ERR_DEVID_USER_LIMIT,
			  "The number of users who can use the device has reached the upper limit",
			  "Please contact your administrator"
			),

			new VpnErrorInfo (
			  VpnError.ERR_DEVID_DEV_LIMIT,
			  "User device number has reached the upper limit",
			  "Please contact your administrator"
			),

			new VpnErrorInfo (
			  VpnError.ERR_DEVID_UNREG,
			  "Your device is not registered yet",
			  "Please register it"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CREATE_SESSION,
			  "Ceate session failed",
			  "Ceate session failed"
			),
					
			new VpnErrorInfo (
			  VpnError.ERR_CERT_NO,
			  "Certificate Error",
			  "Please select a certificate"
			),
					
			new VpnErrorInfo (
			  VpnError.ERR_CERT_INVALID_SIGNTURE,
			  "Certificate Error",
			  "The certificate of client has an invalid signature"
			),
							
			new VpnErrorInfo (
			  VpnError.ERR_CERT_UNTRUSTED,
			  "Certificate Error",
			  "The certificate of client is untrusted"
			),
									
			new VpnErrorInfo (
			  VpnError.ERR_CERT_EXPIRED,
			  "Certificate Error",
			  "The certificate of client is expired"
			),
													
			new VpnErrorInfo (
			  VpnError.ERR_CERT_INVALID,
			  "Certificate Error",
			  "The certificate of client is invalid"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CERT_REVOKED,
			  "Certificate Error",
			  "The certificate of client is revoked"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CERT_SERVER_NO,
			  "Certificate Error",
			  "The server doesn't provide a certificate"
			),
																			
			new VpnErrorInfo (
			  VpnError.ERR_CERT_SERVER_INVALID_SIGNTURE,
			  "Certificate Error",
			  "The certificate of the server has an invalid signature"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CERT_SERVER_UNTRUSTED,
			  "Certificate Error",
			  "The certificate of the server is untrusted"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CERT_SERVER_EXPIRED,
			  "Certificate Error",
			  "The certificate of the server is expired"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CERT_SERVER_INVALID,
			  "Certificate Error",
			  "The certificate of the server is invalid"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CERT_SERVER_REVOKED,
			  "Certificate Error",
			  "The certificate of the server is revoked"
			),
			
			new VpnErrorInfo (
		      VpnError.ERR_CHANGE_PASSWORD,
				  "",
				  ""
		    ),
			
			new VpnErrorInfo (
			  VpnError.ERR_AUTHORIZE_FAILED,
				  "Authorization Failed",
				  "Please contact your adminsitrator to get a valid identity"
			),

			new VpnErrorInfo (
					VpnError.ERR_CONNECT_TIMEOUT,
					"Connect timeout",
					"Please check your network and try again!"
			),

			new VpnErrorInfo (
					VpnError.ERR_PROXY_AUTH,
					"",
					""
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_SMS_GET_PHONE,
			      "SMS Error",
			      "The system failed to get your phone number to do OTP authentication"
			),
			
			new VpnErrorInfo (
		      VpnError.ERR_SMS_SEND,
				  "SMS Error",
				  "The system failed to send message to your phone"
		    ),
			
			new VpnErrorInfo (
			  VpnError.ERR_SMS_TOO_MANY_RETRY,
				  "SMS Error",
				  "You have entered wrong SMS verification code for too many times"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_IPC,
				  "Error",
				  "ERR_IPC"
			),

			new VpnErrorInfo (
			  VpnError.ERR_HWID_DENY,
				  "Error",
				  "Hardware ID is denied, please contact the administrator!"
			),

			new VpnErrorInfo (
			  VpnError.ERR_HWID_PENDING,
				  "Error",
				  "Hardware ID needs approval, please contact the administrator!"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_CS_DOWNLOAD,
				  "ERR_CS_DOWNLOAD",
				  "ERR_CS_DOWNLOAD"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_FILE_WRITE,
				  "ERR_FILE_WRITE",
				  "ERR_FILE_WRITE"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_FILE_OPEN,
				  "ERR_FILE_OPEN",
				  "ERR_FILE_OPEN"
			),
			
			new VpnErrorInfo (
			  VpnError.ERR_FILE_READ,
				  "ERR_FILE_READ",
				  "ERR_FILE_READ"
			),
			
			new VpnErrorInfo(VpnError.ERR_DD_CLIENT_VERIFY_FAIL, "", ""),
			new VpnErrorInfo(VpnError.ERR_CLIENT_SECURITY_FAIL, "", ""),
			new VpnErrorInfo(VpnError.ERR_CLIENT_NEED_UPDATE, "", ""),
			new VpnErrorInfo(VpnError.ERR_HTTP_NO_POST_URL, "", ""),
			new VpnErrorInfo(VpnError.ERR_HTTP_NO_REALM, "", ""),
			new VpnErrorInfo(VpnError.ERR_GET_LOGIN_RESP_FAILED, "", ""),
			new VpnErrorInfo(VpnError.ERR_POST_START_RESP_FAILED, "", ""),
			new VpnErrorInfo(VpnError.ERR_HTTP_NO_CSRF, "", ""),
			new VpnErrorInfo(VpnError.ERR_HTTP_NO_DYNAMIC_DATA, "", ""),
			new VpnErrorInfo(VpnError.ERR_GET_GRID_RESP_FAILED, "", ""),
			new VpnErrorInfo(VpnError.ERR_POST_GRID_RESP_FAILED, "", ""),
			new VpnErrorInfo(VpnError.ERR_GET_BASE64_RESP_FAILED, "", ""),
			new VpnErrorInfo(VpnError.ERR_HTTP_NO_SECGRID, "", ""),
			new VpnErrorInfo(VpnError.ERR_DECODE_FAILED, "", ""),
			new VpnErrorInfo(VpnError.ERR_POST_SECGRID_RESP_FAILED, "", ""),
			new VpnErrorInfo(VpnError.ERR_NOT_SYFERLOCK_AUTH_SITE, "", ""),
			new VpnErrorInfo(VpnError.ERR_NAME_ADDR_NUM_MISMATCH, "", ""),
			new VpnErrorInfo(VpnError.ERR_INVALID_IP_ADDRESS, "", ""),
			new VpnErrorInfo(VpnError.ERR_INVALID_DOMAIN_NAME, "", ""),

			new VpnErrorInfo (
			  VpnError.ERR_MAX,
			  "This error doesn't exist",
			  ""
			),
		};

}
