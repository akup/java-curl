/***************************************************************************
 *                                  _   _ ____  _
 *  Project                     ___| | | |  _ \| |
 *                             / __| | | | |_) | |
 *                            | (__| |_| |  _ <| |___
 *                             \___|\___/|_| \_\_____|
 *
 * Copyright (C) 1998 - 2004, Daniel Stenberg, <daniel@haxx.se>, et al.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution. The terms
 * are also available at http://curl.haxx.se/docs/copyright.html.
 *
 * You may opt to use, copy, modify, merge, publish, distribute and/or sell
 * copies of the Software, and permit persons to whom the Software is
 * furnished to do so, under the terms of the COPYING file.
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY
 * KIND, either express or implied.
 *
 * $Id: CURL.java,v 0.1 2004/10/21 12:21PM 
 ***************************************************************************/
package com.nn;


/**
 * Class <code>CURL</code>
 * This class defines the main libcurl option constants found in
 * curl.h. Options are passed to libcurl via the jni wrapper
 * to create curl objects.
 * 
 * <p><b>TODO:</b> Need to implement some of the other constants such
 * as CURLExxx for error checking and implement a throwable error
 * interface. When the error interface is implemented we'll have to
 * instantiate the class.</p>
 */
public class CURL {
	// **************************************************
	// ***** CURL OPTIONS *****

//	public static final int CURLOPT_URL = 10002;
//	public static final int CURLOPT_WRITEFUNCTION= 20011;
//	public static final int CURLOPT_WRITEDATA= 10001;
//	public static final int CURLOPT_SSLVERSION = 00032;
//	public static final int CURLOPT_SSL_VERIFYPEER = 00064;
//	public static final int CURLOPT_POSTFIELDS = 10015;
//	public static final int CURLOPT_COOKIEJAR = 10082;
//	public static final int CURLOPT_VERBOSE = 00041;
//	public static final int CURLOPT_FOLLOWLOCATION = 00052;
//	public static final int CURLOPT_COOKIEFILE = 10031;
//	public static final int CURLOPT_POST = 00047;

	public static final int INFO_RESPONSE_CODE = 0x00200002;
	public static final int INFO_CONTENT_TYPE  = 0x00100012;
	public static final int OPT_COOKIELIST = 10135;

	/**
	 * To set where libcurl output should be written to (value is 10001). 
	 */
	public static final int OPT_FILE = 10001;

	/**
	 * The full URL to get/put (value is 10002). If the given URL lacks the protocol 
	 * part ("http://" or "ftp://" etc), it will attempt to guess 
	 * which protocol to use based on the given host name.
	 * <p>NOTE: <code>OPT_URL</code> is the only option that must be set 
	 * before <code>jni_perform(int)</code></p>
	 */
	public static final int OPT_URL = 10002;

	/**
	 * Set the remote port number to connect to, instead of the one 
	 * specified in the URL or the default port for the used protocol (value is 3).
	 */
	public static final int OPT_PORT = 3;

	/** 
	 * Name of proxy to use (value is 10004). To specify port number in this string, 
	 * append :[port] to the end of the host name. The proxy string 
	 * may be prefixed with [protocol]:// since any such prefix will 
	 * be ignored. The proxy's port number may optionally be specified 
	 * with the separate option <code>OPT_PROXYPORT</code>.
	 * <p><b>NOTE:</b> when you tell the library to use an HTTP proxy, libcurl 
	 * will transparently convert operations to HTTP even if you specify 
	 * an FTP URL etc. This may have an impact on what other features of 
	 * the library you can use, such as <code>OPT_QUOTE</code> and similar FTP 
	 * specifics that don't work unless you tunnel through the HTTP proxy. 
	 * Such tunneling is activated with <code>OPT_HTTPPROXYTUNNEL</code>.</p>
	 * 
	 * @see #OPT_PROXYPORT OPT_PROXYPORT
	 * @see #OPT_QUOTE OPT_QUOTE
	 * @see #OPT_HTTPPROXYTUNNEL HTTPPROXYTUNNEL
	 */
	public static final int OPT_PROXY = 10004;

	/** 
	 * "name:password" to use when fetching 
	 * (value is 10005).
	 */
	public static final int OPT_USERPWD = 10005;

	/**
	 * "name:password" to use with proxy
	 * (value is 10006).
	 */
	public static final int OPT_PROXYUSERPWD = 10006;

	/**
	 * Range to get, specified as an ASCII string
	 * (value is 10007).
	 */
	public static final int OPT_RANGE = 10007;

	/**
	 * Specified file stream to upload from (use as input)(value is 10009).
	 */
	public static final int OPT_INFILE = 10009;

	/**
	 * Pass a buffer to libcurl to store more readable error messages in (value is 10010). 
	 * Currently not implemented.
	 * 
	 * @see #OPT_VERBOSE OPT_VERBOSE
	 */
	public static final int OPT_ERRORBUFFER = 10010;

	/**
	 * Used to set a callback function to handle the data returned by libcurl (value is 20011).
	 * An instance of the <code>CurlWrite</code> interface is required to 
	 * set this option. Data chunks returned by libcurl are in the
	 * form of a byte[] array.
	 * @see CurlWrite CurlWrite
	 */
	public static final int OPT_WRITEFUNCTION = 20011;

	/**
	 * Function that will be called to read the input (value is 20012). The
	 * parameters will use fread() syntax, make sure to follow them. Not implemented.
	 *  
	 */
	public static final int OPT_READFUNCTION = 20012;

	/** 
	 * Time-out the read operation after this amount of seconds (value is 13). 
	 */
	public static final int OPT_TIMEOUT = 13;

	/** 
	 * If the public static final int OPT_INFILE is used, this can be used to inform libcurl
	 * how large the file being sent really is (value is 14). That allows better error
	 * checking and better verifies that the upload was succcessful. 
	 * <li>-1 means unknown size</li>
	 * <p>For large file support, there is also a _LARGE version of the key
	 * which takes an off_t type, allowing platforms with larger off_t
	 * sizes to handle larger files.  See below for INFILESIZE_LARGE.</p>
	 * 
	 */
	public static final int OPT_INFILESIZE = 14;

	/**
	 * POST input fields (value is 10015). 
	 */
	public static final int OPT_POSTFIELDS = 10015;

	/** 
	 * Set the referer page (needed by some CGIs)(value is 10016).
	 */
	public static final int OPT_REFERER = 10016;

	/**
	 * Set the FTP PORT string (interface name, named or numerical IP address)
	 * Use i.e '-' to use default address (value is 10017).
	 * 
	 */
	public static final int OPT_FTPPORT = 10017;

	/**
	 * Set the User-Agent string (examined by some CGIs) (value is 10018). 
	 */
	public static final int OPT_USERAGENT = 10018;

	/**
	 * If the download receives less than "low speed limit" bytes/second
	 * during "low speed time" seconds, the operations is aborted (value is 19).
	 * You could i.e if you have a pretty high speed connection, abort if
	 * it is less than 2000 bytes/sec during 20 seconds.
	 * Set the "low speed limit"
	 *  
	 */
	public static final int OPT_LOW_SPEED_LIMIT = 19;

	/**
	 *  Set the "low speed time" (value is 20). 
	 */
	public static final int OPT_LOW_SPEED_TIME = 20;

	/** 
	 * Set the continuation offset (value is 21).
	 * Note there is also a _LARGE version of this key which uses
	 * off_t types, allowing for large file offsets on platforms which
	 * use larger-than-32-bit off_t's.  Look below for RESUME_FROM_LARGE.
	 * 
	 */
	public static final int OPT_RESUME_FROM = 21;

	/**
	 * Set cookie in request (value is 10022). 
	 */
	public static final int OPT_COOKIE = 10022;

	/**
	 * This points to a linked list of headers, struct curl_slist kind (value is 10023). 
	 */
	public static final int OPT_HTTPHEADER = 10023;

	/**
	 * This points to a linked list of post entries, struct HttpPost (value is 10024). 
	 */
	public static final int OPT_HTTPPOST = 10024;

	/**
	 * name of the file keeping your private SSL-certificate (value is 10025).
	 */
	public static final int OPT_SSLCERT = 10025;

	/**
	 * password for the SSL-private key, keep this for compatibility (value is 10026). 
	 */
	public static final int OPT_SSLCERTPASSWD = 10026;

	/**
	 * password for the SSL private key (value is 10026). 
	 */
	public static final int OPT_SSLKEYPASSWD = 10026;

	/**
	 * send TYPE parameter (value is 27). 
	 */
	public static final int OPT_CRLF = 27;

	/**
	 * send linked-list of QUOTE commands (value is 10028).
	 */
	public static final int OPT_QUOTE = 10028;

	/**
	 * send FILE * or void * to store headers to, if you use a callback it
	 * is simply passed to the callback unmodified (value is 10029). 
	 */
	public static final int OPT_WRITEHEADER = 10029;

	/**
	 * point to a file to read the initial cookies from, also enables
	 * "cookie awareness" (value is 10031). 
	 */
	public static final int OPT_COOKIEFILE = 10031;

	/**
	 * What SSL version to specificly try to use (value is 32).
	 */
	public static final int OPT_SSLVERSION = 32;

	/**
	 * What kind of HTTP time condition to use, see defines (value is 33). 
	 */
	public static final int OPT_TIMECONDITION = 33;

	/**
	 * Time to use with the above condition, specified in number of seconds
	 * since 1 Jan 1970 (value is 24). 
	 */
	public static final int OPT_TIMEVALUE = 34;

	/* 35  = OBSOLETE */

	/**
	 * Custom request, for customizing the get command like (value is 10036):
	 * <li>HTTP: DELETE, TRACE and others</li>
	 * <li>FTP: to use a different list command</li>
	 */
	public static final int OPT_CUSTOMREQUEST = 10036;

	/**
	 * Set an optional stream instead of stderr when showing the 
	 * progress meter and displaying <code>OPT_VERBOSE</code> data (value is 10037).
	 * Currently not implemented.
	 * 
	 * @see #OPT_VERBOSE OPT_VERBOSE
	 */
	public static final int OPT_STDERR = 10037;

	/* 38 is not used */

	/**
	 * send linked-list of post-transfer QUOTE commands (value is 10039). 
	 */
	public static final int OPT_POSTQUOTE = 10039;

	/**
	 * Pass a pointer to string of the output using full variable-replacement
	 * as described elsewhere (value is 10040). 
	 */
	public static final int OPT_WRITEINFO = 10040;

	/**
	 * Set the parameter to non-zero to get the library to display a lot of
	 * verbose information about its operations (value is 41). Very useful 
	 * for libcurl and/or protocol debugging and understanding. The verbose 
	 * information will be sent to stderr.
	 * @see #OPT_STDERR OPT_STDERR
	 */
	public static final int OPT_VERBOSE = 41;

	/**
	 * A non-zero parameter tells the library to include the 
	 * header in the body output (value is 42). This is only relevant for protocols 
	 * that actually have headers preceding the data (like HTTP).
	 */
	public static final int OPT_HEADER = 42;

	/**
	 * A non-zero parameter tells the library to shut off the built-in 
	 * progress meter completely (value is 43). 
	 * <p><b>NOTE:</b> future versions of libcurl is likely to not have any 
	 * built-in progress meter at all.</p> 
	 */
	public static final int OPT_NOPROGRESS = 43;

	/**
	 * use HEAD to get http document (value is 44).
	 */
	public static final int OPT_NOBODY = 44;

	/**
	 * A non-zero (1) parameter tells the library to fail silently if 
	 * the HTTP code returned is equal to or larger than 300 (value is 45). 
	 * The default action would be to return the page normally, 
	 * ignoring that code.
	 */
	public static final int OPT_FAILONERROR = 45;

	/**
	 * this is an upload (value is 46) 
	 */
	public static final int OPT_UPLOAD = 46;

	/**
	 * HTTP POST method ENCTYPE='x-www-form-urlencoded'(value is 47). 
	 */
	public static final int OPT_POST = 47;

	/**
	 * Use NLST when listing ftp dir (value is 48). 
	 */
	public static final int OPT_FTPLISTONLY = 48;

	/**
	 * Append instead of overwrite on upload (value is 50).
	 */
	public static final int OPT_FTPAPPEND = 50;

	/**
	 * Specify whether to read the user+password from the netrc or the URL (value is 51).
	 * This must be one of the CURL_NETRC_* enums below.
	 * 
	 */
	public static final int OPT_NETRC = 51;

	/**
	 * use Location (value is 52).
	 */
	public static final int OPT_FOLLOWLOCATION = 52;

	/**
	 * transfer data in text/ASCII format (value is 53).
	 */
	public static final int OPT_TRANSFERTEXT = 53;

	/**
	 * HTTP PUT (value is 54).
	 */
	public static final int OPT_PUT = 54;

	/* 55  = OBSOLETE */

	/**
	 * Set a function that gets called by libcurl instead of its internal 
	 * equivalent with a frequent interval during data transfer (value is 20056).
	 * Also note that <code>OPT_NOPROGRESS</code> must be set to FALSE(0) to make 
	 * this function actually get called
	 * Currently not implemented. 
	 * 
	 * @see #OPT_NOPROGRESS OPT_NOPROGRESS
	 */
	public static final int OPT_PROGRESSFUNCTION = 20056;

	/**
	 * Pass data that will be untouched by libcurl and passed 
	 * as the first argument in the progress callback set with 
	 * <code>OPT_PROGRESSFUNCTION</code> (value is 10057).
	 * Currently not implemented.
	 * 
	 * @see #OPT_PROGRESSFUNCTION OPT_PROGRESSFUNCTION
	 *
	 */
	public static final int OPT_PROGRESSDATA = 10057;

	/**
	 * We want the referer field set automatically when following locations (value is 58). 
	 */
	public static final int OPT_AUTOREFERER = 58;

	/**
	 * Pass a long with this option to set the proxy port to connect to 
	 * unless it is specified in the proxy string <code>OPT_PROXY</code>
	 * (value is 59).
	 * @see #OPT_PROXY OPT_PROXY
	 */
	public static final int OPT_PROXYPORT = 59;

	/**
	 * size of the POST input data, if strlen() is not good to use (value is 60). 
	 */
	public static final int OPT_POSTFIELDSIZE = 60;

	/**
	 * Set the parameter to non-zero to get the library to tunnel all 
	 * operations through a given HTTP proxy (value is 61). Note that there is a big 
	 * difference between using a proxy and to tunnel through it. 
	 * If you don't know what this means, you probably don't want this 
	 * tunneling option.
	 * 
	 * @see #OPT_PROXY OPT_PROXY
	 */
	public static final int OPT_HTTPPROXYTUNNEL = 61;

	/**
	 * This set the interface name to use as an outgoing network 
	 * interface (value is 10062). The name can be an interface name, an IP address 
	 * or a host name.
	 */
	public static final int OPT_INTERFACE = 10062;

	/**
	 * Set the krb4 security level, this also enables krb4 awareness (value is 10063).  
	 * This is a string, 'clear', 'safe', 'confidential' or 'private'.  If the string is
	 * set but doesn't match one of these, 'private' will be used.
	 */
	public static final int OPT_KRB4LEVEL = 10063;

	/**
	 * Set if we should verify the peer in ssl handshake, set 1 to verify (value is 64). 
	 */
	public static final int OPT_SSL_VERIFYPEER = 64;

	/**
	 * The CApath or CAfile used to validate the peer certificate
	 * this option is used only if SSL_VERIFYPEER is true (value is 10065).
	 */
	public static final int OPT_CAINFO = 10065;

	/* 66  = OBSOLETE */
	/* 67  = OBSOLETE */

	/**
	 * Maximum number of http redirects to follow (value is 68). 
	 */
	public static final int OPT_MAXREDIRS = 68;

	/**
	 * Get the time/date of a requested document (value is 69).
	 * <li>1 to retrieve</li>
	 * <li>0 to shut off (default)</li>
	 */
	public static final int OPT_FILETIME = 69;

	/**
	 * This points to a linked list of telnet options (value is 10070). 
	 */
	public static final int OPT_TELNETOPTIONS = 10070;

	/**
	 * Max amount of cached alive connections (value is 71). 
	 */
	public static final int OPT_MAXCONNECTS = 71;

	/**
	 * What policy to use when closing connections when the cache is filled up
	 * (value is 72). 
	 */
	public static final int OPT_CLOSEPOLICY = 72;

	/* 73  = OBSOLETE */

	/**
	 * Set to explicitly use a new connection for the upcoming transfer (value is 74).
	 * Do not use this unless you're absolutely sure of this, as it makes the
	 * operation slower and is less friendly for the network.
	 * 
	 */
	public static final int OPT_FRESH_CONNECT = 74;

	/**
	 * Set to explicitly forbid the upcoming transfer's connection to be re-used
	 * when done (value is 75). Do not use this unless you're absolutely sure of 
	 * this, as it makes the operation slower and is less friendly for the network.
	 */
	public static final int OPT_FORBID_REUSE = 75;

	/**
	 * Set to a file name that contains random data for libcurl to use to
	 * seed the random engine when doing SSL connects (value is 10076). 
	 */
	public static final int OPT_RANDOM_FILE = 10076;

	/**
	 * Set to the Entropy Gathering Daemon socket pathname (value is 10077). 
	 */
	public static final int OPT_EGDSOCKET = 10077;

	/**
	 * Time-out connect operations after this amount of seconds, if connects
	 * are OK within this time, then fine (value is 78). This only aborts the connect
	 * phase. [Only works on unix-style/SIGALRM operating systems]
	 */
	public static final int OPT_CONNECTTIMEOUT = 78;

	/**
	 * Set a function that gets called by libcurl as soon as there 
	 * is received header data that needs to be written down (value is 20079). 
	 * The headers are guaranteed to be written one-by-one and 
	 * only complete lines are written.
	 * Currently not implemented.
	 */
	public static final int OPT_HEADERFUNCTION = 20079;

	/**
	 * Set this to force the HTTP request to get back to GET (value is 80). 
	 * Only really usable if POST, PUT or a custom request have been used first.
	 */
	public static final int OPT_HTTPGET = 80;

	/**
	 * Set if we should verify the Common name from the peer certificate in ssl
	 * handshake (value is 81). 
	 * <li>1 to check existence</li>
	 * <li>2 to ensure that it matches the provided hostname</li>
	 *  
	 */
	public static final int OPT_SSL_VERIFYHOST = 81;

	/**
	 * Specify which file name to write all known cookies in after completed
	 * operation (value is 10082). Set file name to "-" (dash) to make it go to stdout.
	 */
	public static final int OPT_COOKIEJAR = 10082;

	/**
	 * Specify which SSL ciphers to use (value is 10083). 
	 */
	public static final int OPT_SSL_CIPHER_LIST = 10083;

	/**
	 * Specify which HTTP version to use (value is 84). This must be set to one of the
	 * CURL_HTTP_VERSION* enums set below.
	 */
	public static final int OPT_HTTP_VERSION = 84;

	/**
	 * Specificly switch on or off the FTP engine's use of the EPSV command (value is 85).
	 * By default, that one will always be attempted before the more traditional
	 * PASV command.
	 *  
	 */
	public static final int OPT_FTP_USE_EPSV = 85;

	/**
	 * type of the file keeping your SSL-certificate ("DER", "PEM", "ENG")
	 * (value is 10086).
	 */
	public static final int OPT_SSLCERTTYPE = 10086;

	/**
	 * name of the file keeping your private SSL-key
	 * (value is 10087). 
	 */
	public static final int OPT_SSLKEY = 10087;

	/** 
	 * type of the file keeping your private SSL-key ("DER", "PEM", "ENG")
	 * (value is 10088).
	 */
	public static final int OPT_SSLKEYTYPE = 10088;

	/**
	 * crypto engine for the SSL-sub system
	 * (value is 10089).
	 */
	public static final int OPT_SSLENGINE = 10089;

	/**
	 * set the crypto engine for the SSL-sub system as default
	 * the param has no meaning (value is 90).
	 */
	public static final int OPT_SSLENGINE_DEFAULT = 90;

	/**
	 * Obsolete (value is 91).
	 * @deprecated This option is obsolete. 
	 * Use <code>OPT_SHARE OPT_SHARE</code> instead
	 * @see #OPT_SHARE OPT_SHARE
	 */
	public static final int OPT_DNS_USE_GLOBAL_CACHE = 91;

	/**
	 * Sets the timeout in seconds (value is 92). Name resolves will be kept in memory 
	 * for this number of seconds. 
	 * <li>0 to completely disable caching</li> 
	 * <li>-1 to make the cached entries remain forever</li> 
	 * <li>default libcurl caches this info for 60 seconds</li>
	 */
	public static final int OPT_DNS_CACHE_TIMEOUT = 92;

	/**
	 * send linked-list of pre-transfer QUOTE commands (Wesley Laxton)
	 * (value is 93).
	 */
	public static final int OPT_PREQUOTE = 93;

	/**
	 * set the debug function callback
	 * (value is 20094). 
	 */
	public static final int OPT_DEBUGFUNCTION = 20094;

	/**
	 * set the data for the debug function
	 * (value is 10095).
	 */
	public static final int OPT_DEBUGDATA = 10095;

	/**
	 * mark this as start of a cookie session
	 * (value is 96).
	 */
	public static final int OPT_COOKIESESSION = 96;

	/**
	 * The CApath directory used to validate the peer certificate
	 * this option is used only if SSL_VERIFYPEER is true
	 * (value is 10097). 
	 */
	public static final int OPT_CAPATH = 10097;

	/**
	 * Sets the preferred size for the receive buffer in libcurl (value is 98) . The 
	 * main point of this would be that the write callback gets called 
	 * more often and with smaller chunks. This is just treated as a 
	 * request, not an order. You cannot be guaranteed to actually get 
	 * the given size.
	 */
	public static final int OPT_BUFFERSIZE = 98;

	/**
	 * Instruct libcurl to not use any signal/alarm handlers, even when using
	 * timeouts (value is 99). This option is useful for multi-threaded applications.
	 * See libcurl-the-guide for more background information.
	 *  
	 */
	public static final int OPT_NOSIGNAL = 99;

	/**
	 * Provide a CURLShare for mutexing non-ts data
	 * (value is 10100).
	 */
	public static final int OPT_SHARE = 10100;

	/**
	 * Indicates type of proxy (value is 101). Accepted values are:
	 * <li>CURLPROXY_HTTP (default)</li>
	 * <li>CURLPROXY_SOCKS4 and CURLPROXY_SOCKS5. Currently not implemented</li>
	 * @see #OPT_PROXY
	 * @see #OPT_PROXYPORT
	 */
	public static final int OPT_PROXYTYPE = 101;

	/**
	 * Set the Accept-Encoding string (value is 10102). Use this to tell a server 
	 * you would like the response to be compressed.
	 *  
	 */
	public static final int OPT_ENCODING = 10102;

	/**
	 * Set pointer to private data
	 * (value is 10103).
	 */
	public static final int OPT_PRIVATE = 10103;

	/**
	 * Set aliases for HTTP 200 in the HTTP Response header
	 * (value is 10104).
	 */
	public static final int OPT_HTTP200ALIASES = 10104;

	/**
	 * Continue to send authentication (user+password) when following locations,
	 * even when hostname changed (value is 105). This can potentionally send off the name
	 * and password to whatever host the server decides.
	 */
	public static final int OPT_UNRESTRICTED_AUTH = 105;

	/**
	 * Specificly switch on or off the FTP engine's use of the EPRT command ( it
	 * also disables the LPRT attempt) (value is 106). By default, those ones 
	 * will always be attempted before the good old traditional PORT command.
	 */
	public static final int OPT_FTP_USE_EPRT = 106;

	/**
	 * Set this to a bitmask value to enable the particular authentications
	 * methods you like (value is 107). Use this in combination with public 
	 * static final int OPT_USERPWD. Note that setting multiple bits may 
	 * cause extra network round-trips.
	 */
	public static final int OPT_HTTPAUTH = 107;

	/**
	 * Set the ssl context callback function, currently only for OpenSSL ssl_ctx
	 * in second argument (value is 20108). The function must be matching the
	 * curl_ssl_ctx_callback proto.
	 *  
	 */
	public static final int OPT_SSL_CTX_FUNCTION = 20108;

	/**
	 * Set the userdata for the ssl context callback function's third
	 * argument (value is 10109). 
	 */
	public static final int OPT_SSL_CTX_DATA = 10109;

	/**
	 * FTP Option that causes missing dirs to be created on the remote server
	 * (value is 110).
	 */
	public static final int OPT_FTP_CREATE_MISSING_DIRS = 110;

	/**
	 * Set this to a bitmask value to enable the particular authentications
	 * methods you like (value is 111). Use this in combination with 
	 * <code>OPT_PROXYUSERPWD</code>.
	 * Note that setting multiple bits may cause extra network round-trips.
	 *  
	 */
	public static final int OPT_PROXYAUTH = 111;

	/**
	 * FTP option that changes the timeout, in seconds, associated with
	 * getting a response (value is 112).  This is different from transfer 
	 * timeout time and essentially places a demand on the FTP server to 
	 * acknowledge commands in a timely manner.
	 */
	public static final int OPT_FTP_RESPONSE_TIMEOUT = 112;

	/**
	 * Set this option to one of the CURL_IPRESOLVE_* defines (see below) to
	 * tell libcurl to resolve names to those IP versions only (value is 113). 
	 * This only has affect on systems with support for more than one, 
	 * i.e IPv4 _and_ IPv6.
	 */
	public static final int OPT_IPRESOLVE = 113;

	/**
	 * Set this option to limit the size of a file that will be downloaded from
	 * an HTTP or FTP server (value is 114).
	 * Note there is also _LARGE version which adds large file support for
	 * platforms which have larger off_t sizes.
	 * @see #OPT_MAXFILESIZE_LARGE
	 */
	public static final int OPT_MAXFILESIZE = 114;

	/**
	 * See the comment for <code>OPT_INFILESIZE</code> above, but in short, specifies
	 * the size of the file being uploaded (value is 30115).  
	 * <li>-1 means unknown</li>
	 * @see #OPT_INFILESIZE
	 */
	public static final int OPT_INFILESIZE_LARGE = 30115;

	/**
	 * Sets the continuation offset (value is 30116).  There is also a LONG version of this;
	 * look above for <code>OPT_RESUME_FROM</code>.
	 * 
	 */
	public static final int OPT_RESUME_FROM_LARGE = 30116;

	/** 
	 * Sets the maximum size of data that will be downloaded from
	 * an HTTP or FTP server (value is 30117).  
	 * See <code>OPT_MAXFILESIZE</code> above for the LONG version.
	 * @see #OPT_MAXFILESIZE
	 */
	public static final int OPT_MAXFILESIZE_LARGE = 30117;

	/**
	 * Set this option to the file name of your <em>.netrc</em> file you want libcurl
	 * to parse (using the <code>OPT_NETRC</code> option) (value is 10118). 
	 * If not set, libcurl will do a poor attempt to find the user's home directory 
	 * and check for a <em>.netrc</em> file in there.
	 * @see #OPT_NETRC
	 */
	public static final int OPT_NETRC_FILE = 10118;

	/**
	 * Enable SSL/TLS for FTP (value is 119), pick one of:
	 * <li>CURLFTPSSL_TRY     - try using SSL, proceed anyway otherwise</li>
	 * <li>CURLFTPSSL_CONTROL - SSL for the control connection or fail</li>
	 * <li>CURLFTPSSL_ALL     - SSL for all communication or fail</li>
	 */
	public static final int OPT_FTP_SSL = 119;

	/**
	 * The _LARGE version of the standard <code>OPT_POSTFIELDSIZE</code> 
	 * option (value is 30120).
	 * @see #OPT_POSTFIELDSIZE OPT_POSTFIELDSIZE
	 */
	public static final int OPT_POSTFIELDSIZE_LARGE = 30120;

	/**
	 * Pass a long specifying whether the TCP_NODELAY option should 
	 * be set or cleared (value is 121).
	 * <li>1 = set</li>
	 * <li>0 = clear (default)</li>
	 * <p>This will have no effect after the connection has been established.
	 * Setting this option will disable TCP's Nagle algorithm. The 
	 * purpose of this algorithm is to try to minimize the number of small 
	 * packets on the network (where "small packets" means TCP segments 
	 * less than the Maximum Segment Size (MSS) for the network).</p>
	 * <p>Maximizing the amount of data sent per TCP segment is good 
	 * because it amortizes the overhead of the send. However, in some 
	 * cases (most notably telnet or rlogin) small segments may need to 
	 * be sent without delay. This is less efficient than sending larger 
	 * amounts of data at a time, and can contribute to congestion on the 
	 * network if overdone.</p>
	 */
	public static final int OPT_TCP_NODELAY = 121;

	/**
	 * When doing 3rd party transfer, set the source host name with this
	 * (value is 10122). 
	 */
	public static final int OPT_SOURCE_HOST = 10122;

	/**
	 * When doing 3rd party transfer, set the source user and password with this
	 * (value is 10123).
	 */
	public static final int OPT_SOURCE_USERPWD = 10123;

	/**
	 * When doing 3rd party transfer, set the source file path with this
	 * (value is 10124).
	 */
	public static final int OPT_SOURCE_PATH = 10124;

	/**
	 * When doing 3rd party transfer, set the source server's port number
	 * with this (value is 125). 
	 */
	public static final int OPT_SOURCE_PORT = 125;

	/**
	 * When doing 3rd party transfer, decide which server that should get the
	 * PASV command (and the other gets the PORT) (value is 121).
	 * <li>0 (default) - The target host issues PASV.</li>
	 * <li>1           - The source host issues PASV </li>
	 */
	public static final int OPT_PASV_HOST = 126;

	/**
	 * When doing 3rd party transfer, set the source pre-quote linked list
	 * of commands with this (value is 10127). 
	 */
	public static final int OPT_SOURCE_PREQUOTE = 10127;

	/**
	 * When doing 3rd party transfer, set the source post-quote linked list
	 * of commands with this (value is 10128). 
	 */
	public static final int OPT_SOURCE_POSTQUOTE = 10128;


	public static final int OPT_TCP_KEEPALIVE = 213;
	public static final int OPT_TCP_KEEPIDLE = 214;
	public static final int OPT_TCP_KEEPINTVL = 215;

	
	/* Returns code for curl_formadd()
	 *
	 * Returns:
	 * CURL_FORMADD_OK             on success
	 * CURL_FORMADD_MEMORY         if the FormInfo allocation fails
	 * CURL_FORMADD_OPTION_TWICE   if one option is given twice for one Form
	 * CURL_FORMADD_NULL           if a null pointer was given for a char
	 * CURL_FORMADD_MEMORY         if the allocation of a FormInfo struct failed
	 * CURL_FORMADD_UNKNOWN_OPTION if an unknown option was used
	 * CURL_FORMADD_INCOMPLETE     if the some FormInfo is not complete (or error)
	 * CURL_FORMADD_MEMORY         if a HttpPost struct cannot be allocated
	 * CURL_FORMADD_MEMORY         if some allocation for string copying failed.
	 * CURL_FORMADD_ILLEGAL_ARRAY  if an illegal option is used in an array
	 *
	 ***************************************************************************/
	public static final int FORM_COPYNAME = 1;
	public static final int FORM_FILE = 2;
	public static final int FORM_CONTENTTYPE = 3;
	public static final int FORMADD_OK = 0;
	public static final int FORMADD_MEMORY= 1;
	public static final int FORMADD_OPTION_TWICE = 2;
	public static final int FORMADD_NULL = 3;
	public static final int FORMADD_UNKNOWN_OPTION = 4;
	public static final int FORMADD_INCOMPLETE = 5;

	
	// **************************************************
	// Some Fatal Error codes not covered by libcurl
	/**
	 * An unspecified error (value is 5000).
	 */
	public static final int ERROR_UNSPECIFIED = 5000;
	
	/**
	 * Thrown when a non-existent curl option is passed
	 * to <code>setopt</code>() (value is 5001).
	 * @see CurlGlue#setopt(int, int)
	 */
	public static final int ERROR_BAD_ARGUMENT = 5001;
	
	/**
	 * Thrown when the callback interface has not been instantiated
	 * (value is 5002).
	 */
	public static final int ERROR_NO_CALLBACK_INSTANCE = 5002;
	
	/**
	 * Critical: the JVM cannot load one or more of libraries
	 * javacurl depends on, including libcurl itself (value is 5003).
	 */
	public static final int ERROR_FAILED_LOAD_LIBRARY = 5003;
	
	/**
	 * There is a Security Manager present on this machine that is
	 * preventing JVM from loading a critical library (value is 5004).
	 */
	public static final int ERROR_SECURITY_MANAGER_VIOLATION = 5004;
	
	/**
	 * A critical error occured instantiating the javacurl library
	 * handle (value is 5005).
	 */
	public static final int ERROR_NO_JAVACURL_HANDLE_AVAILABLE = 5005;
	
	/**
	 * Coder is calling a method in libcurl that has not been implemented 
	 * in this JNI (value is 5006). Judgement call: better to throw a visible 
	 * error than to let it slide.
	 */
	public static final int ERROR_NOT_IMPLEMENTED = 5006;
	
	/**
	 * A null argument was passed to a function expecting a String
	 * (value is 5007).
	 */
	public static final int ERROR_NULL_ARGUMENT = 5007;

	/**
	 * Good return status (value is 0).
	 */
	public static final int CURL_OK = 0;
	
	/**
	 * Protocol is not suppored, check URL spelling (value is 1).
	 */
	public static final int ERROR_UNSUPPORTED_PROTOCOL = 1;
	
	/**
	 * Very early initialization code failed, this is likely to 
	 * be an internal error or problem (value is 2).
	 */
	public static final int ERROR_FAILED_INIT=2;
	
	/**
	 * The URL was not properly formatted (value is 3).
	 */
	public static final int ERROR_URL_MALFORMAT=3;
	
	/**
	 * The user-part of the URL syntax was not correct (value is 4).
	 */
	public static final int ERROR_URL_MALFORMAT_USER=4;
	
	/**
	 * The given proxy host could not be resolved (value is 5).
	 */
	public static final int ERROR_COULDNT_RESOLVE_PROXY=5;
	
	/**
	 * The given remote host was not resolved (value is 6).
	 */
	public static final int ERROR_COULDNT_RESOLVE_HOST=6;
	
	/**
	 * Failed to connect() to host or proxy (value is 7).
	 */
	public static final int ERROR_COULDNT_CONNECT=7;
	
	/**
	 * After connecting to an FTP server, libcurl expects to get a 
	 * certain reply back (value is 8). This error code implies that it got a 
	 * strange or bad reply. The given remote server is probably 
	 * not an OK FTP server. 
	 */
	public static final int ERROR_FTP_WEIRD_SERVER_REPLY=8;
	
	/**
	 * We were denied access when trying to login to an FTP 
	 * server or when trying to change working directory to 
	 * the one given in the URL (value is 9).
	 */
	public static final int ERROR_FTP_ACCESS_DENIED=9;
	
	/**
	 * The FTP server rejected access to the server after 
	 * the password was sent to it (value is 10).
	 */
	public static final int ERROR_FTP_USER_PASSWORD_INCORRECT=10;
	
	/**
	 * After having sent the FTP password to the server, libcurl 
	 * expects a proper reply (value is 11). This error code indicates that an 
	 * unexpected code was returned. 
	 */
	public static final int ERROR_FTP_WEIRD_PASS_REPLY=11;
	
	/**
	 * After having sent user name to the FTP server, libcurl 
	 * expects a proper reply (value is 12). This error code indicates that 
	 * an unexpected code was returned.
	 */
	public static final int ERROR_FTP_WEIRD_USER_REPLY=12;
	
	/**
	 * libcurl failed to get a sensible result back from the server 
	 * as a response to either a PASV or a EPSV command (value is 13). 
	 * The server is flawed.
	 */
	public static final int ERROR_FTP_WEIRD_PASV_REPLY=13;
	
	/**
	 * FTP servers return a 227-line as a response to a PASV 
	 * command (value is 14). If libcurl fails to parse that line, this return 
	 * code is passed back.
	 */
	public static final int ERROR_FTP_WEIRD_227_FORMAT=14;
	
	/**
	 * An internal failure to lookup the host used for the new 
	 * connection (value is 15).
	 */
	public static final int ERROR_FTP_CANT_GET_HOST=15;
	
	/**
	 * A bad return code on either PASV or EPSV was sent by the 
	 * FTP server, preventing libcurl from being able to 
	 * continue (value is 16).
	 */
	public static final int ERROR_FTP_CANT_RECONNECT=16;
	
	/**
	 * Received an error when trying to set the transfer mode to 
	 * binary (value is 17).
	 */
	public static final int ERROR_FTP_COULDNT_SET_BINARY=17;
	
	/**
	 * A file transfer was shorter or larger than expected. This 
	 * happens when the server first reports an expected transfer 
	 * size, and then delivers data that doesn't match the previously 
	 * given size (value is 18).
	 */
	public static final int ERROR_PARTIAL_FILE=18;
	
	/**
	 * This was either a weird reply to a 'RETR' command or a 
	 * zero byte transfer complete (value is 19).
	 */
	public static final int ERROR_FTP_COULDNT_RETR_FILE=19;
	
	/**
	 * After a completed file transfer, the FTP server did not respond 
	 * a proper "transfer successful" code (value is 20).
	 */
	public static final int ERROR_FTP_WRITE_ERROR=20;
	
	/**
	 * When sending custom "QUOTE" commands to the remote server, 
	 * one of the commands returned an error code that was 
	 * 400 or higher (value is 21).
	 */
	public static final int ERROR_FTP_QUOTE_ERROR=21;
	
	/**
	 * This is returned if <code>OPT_FAILONERROR</code> is set TRUE 
	 * and the HTTP server returns an error code that is 
	 * >= 400 (value is 22).
	 * @see #OPT_FAILONERROR OPT_FAILONERROR
	 */
	public static final int ERROR_HTTP_RETURNED_ERROR=22;
	
	/**
	 * An error occurred when writing received data to a 
	 * local file, or an error was returned to libcurl from 
	 * a write callback (value is 23).
	 */
	public static final int ERROR_WRITE_ERROR=23;
	
	/**
	 * Malformat user, user name badly specified (value is 24).
	 *  *Not currently used*
	 */
	public static final int ERROR_MALFORMAT_USER=24;
	
	/**
	 * FTP couldn't STOR file (value is 25). The server denied the STOR operation. 
	 * The error buffer usually contains the server's explanation 
	 * to this.
	 * @see #OPT_ERRORBUFFER OPT_ERRORBUFFER
	 */
	public static final int ERROR_FTP_COULDNT_STOR_FILE=25;
	
	/**
	 * There was a problem reading a local file or an error returned 
	 * by the read callback (value is 26).
	 */
	public static final int ERROR_READ_ERROR=26;
	
	/**
	 * Out of memory (value is 27). A memory allocation request failed. This is 
	 * serious badness and things are severely screwed up if this 
	 * ever occur.
	 */
	public static final int ERROR_OUT_OF_MEMORY=27;
	
	/**
	 * Operation timeout (value is 28). The specified time-out period was 
	 * reached according to the conditions.
	 */
	public static final int ERROR_OPERATION_TIMEOUTED=28;
	
	/**
	 * libcurl failed to set ASCII transfer type (TYPE A) (value is 29).
	 */
	public static final int ERROR_FTP_COULDNT_SET_ASCII=29;
	
	/**
	 * The FTP PORT command returned error (value is 30). This mostly happen when 
	 * you haven't specified a good enough address for libcurl to use.
	 * 
	 * @see #OPT_FTPPORT OPT_FTPPORT
	 */
	public static final int ERROR_FTP_PORT_FAILED=30;
	
	/**
	 * The FTP REST command returned error (value is 31). This should never happen 
	 * if the server is sane.
	 */
	public static final int ERROR_FTP_COULDNT_USE_REST=31;
	
	/**
	 * The FTP SIZE command returned error (value is 32). SIZE is not a kosher 
	 * FTP command, it is an extension and not all servers support 
	 * it. This is not a surprising error.
	 */
	public static final int ERROR_FTP_COULDNT_GET_SIZE=32;
	
	/**
	 * The HTTP server does not support or accept range requests
	 * (value is 33).
	 */
	public static final int ERROR_HTTP_RANGE_ERROR=33;
	
	/**
	 * This is an odd error that mainly occurs due to internal 
	 * confusion (value is 34).
	 */
	public static final int ERROR_HTTP_POST_ERROR=34;
	
	/**
	 * A problem occurred somewhere in the SSL/TLS handshake (value is 35). 
	 * You really want the error buffer and read the message 
	 * there as it pinpoints the problem slightly more. Could 
	 * be certificates (file formats, paths, permissions), 
	 * passwords, and others.
	 */
	public static final int ERROR_SSL_CONNECT_ERROR=35;
	
	/**
	 * Attempting FTP resume beyond file size (value is 36).
	 */
	public static final int ERROR_BAD_DOWNLOAD_RESUME=36;
	
	/**
	 * A file given with FILE:// couldn't be opened (value is 37). Most 
	 * likely because the file path doesn't identify an existing 
	 * file. Did you check file permissions?
	 */
	public static final int ERROR_FILE_COULDNT_READ_FILE=37;
	
	/**
	 * LDAP cannot bind, LDAP bind operation failed (value is 38).
	 */
	public static final int ERROR_LDAP_CANNOT_BIND=38;
	
	/**
	 * LDAP search failed (value is 39).
	 */
	public static final int ERROR_LDAP_SEARCH_FAILED=39;
	
	/**
	 * The LDAP library was not found (value is 40).
	 */
	public static final int ERROR_LIBRARY_NOT_FOUND=40;
	
	/**
	 * A required LDAP function was not found (value is 41).
	 */
	public static final int ERROR_FUNCTION_NOT_FOUND=41;
	
	/**
	 * Aborted by callback (value is 42). A callback returned "abort" to 
	 * libcurl.
	 */
	public static final int ERROR_ABORTED_BY_CALLBACK=42;
	
	/**
	 * Internal error (value is 43). A function was called with a bad 
	 * parameter.
	 */
	public static final int ERROR_BAD_FUNCTION_ARGUMENT=43;
	
	/**
	 * Internal error (value is 44). A function was called in a bad order.
	 */
	public static final int ERROR_BAD_CALLING_ORDER=44;
	
	/**
	 * Interface error (value is 45). A specified outgoing interface could not 
	 * be used. Set which interface to use for outgoing connections' 
	 * source IP address with <code>OPT_INTERFACE</code>.
	 * @see #OPT_INTERFACE OPT_INTERFACE
	 */
	public static final int ERROR_INTERFACE_FAILED=45;
	
	/**
	 * Bad password entered (value is 46). An error was signaled when the 
	 * password was entered. This can also be the result of a 
	 * "bad password" returned from a specified password 
	 * callback.
	 */
	public static final int ERROR_BAD_PASSWORD_ENTERED=46;
	
	/**
	 * Too many redirects (value is 47). When following redirects, libcurl 
	 * hit the maximum amount. Set your limit with 
	 * <code>OPT_MAXREDIRS</code>.
	 * @see #OPT_MAXREDIRS OPT_MAXREDIRS
	 */
	public static final int ERROR_TOO_MANY_REDIRECTS=47;
	
	/**
	 * An option set with <code>OPT_TELNETOPTIONS</code> was not 
	 * recognized/known (value is 48).
	 * @see #OPT_TELNETOPTIONS OPT_TELNETOPTIONS
	 */
	public static final int ERROR_UNKNOWN_TELNET_OPTION=48;
	
	/**
	 * A telnet option string was Illegally formatted (value is 49).
	 */
	public static final int ERROR_TELNET_OPTION_SYNTAX =49;
	
	/**
	 * This code is unused (value is 50).
	 * @deprecated
	 */
	public static final int ERROR_OBSOLETE=50;
	
	/** 
	 * The remote server's SSL certificate was deemed not OK (value is 51).
	 */
	public static final int ERROR_SSL_PEER_CERTIFICATE=51;
	
	/**
	 * Nothing was returned from the server, and under the circumstances, 
	 * getting nothing is considered an error (value is 52).
	 */
	public static final int ERROR_GOT_NOTHING=52;
	
	/**
	 * The specified crypto engine wasn't found (value is 53).
	 */
	public static final int ERROR_SSL_ENGINE_NOTFOUND=53;
	
	/**
	 * Failed setting the selected SSL crypto engine as default 
	 * (value is 54).
	 */
	public static final int ERROR_SSL_ENGINE_SETFAILED=54;
	
	/**
	 * Failed sending network data (value is 55).
	 */
	public static final int ERROR_SEND_ERROR=55;

	/**
	 * Failure with receiving network data (value is 56).
	 */
	public static final int ERROR_RECV_ERROR=56;
	
	/**
	 * Share is in use (value is 57).
	 */
	public static final int ERROR_SHARE_IN_USE=57;
	
	/**
	 * Problem with the local client certificate (value is 58).
	 */
	public static final int ERROR_SSL_CERTPROBLEM=58;
	
	/**
	 * Couldn't use specified cipher (value is 59).
	 */
	public static final int ERROR_SSL_CIPHER=59;
	
	/**
	 * Problem with the CA cert (value is 60). 
	 * (did you check path and access rights?) 
	 */
	public static final int ERROR_SSL_CACERT=60;
	
	/**
	 * Unrecognized transfer encoding (value is 61).
	 */
	public static final int ERROR_BAD_CONTENT_ENCODING=61;
	
	/**
	 * Invalid LDAP URL (value is 62).
	 */
	public static final int ERROR_LDAP_INVALID_URL=62;
	
	/**
	 * Maximum file size exceeded (value is 63).
	 */
	public static final int ERROR_FILESIZE_EXCEEDED=63;
	
	/**
	 * Requested FTP SSL level failed (value is 64).
	 */
	public static final int ERROR_FTP_SSL_FAILED=64;




	/**
	 * Answers a concise, human readable description of the error code.
	 *
	 * @param code the CURL.ERROR_ error code.
	 * @return a description of the error code.
	 *
	 * @see CURL
	 */
	static String findErrorText (int code) {
		switch (code) {
			case ERROR_UNSPECIFIED: 
				return "Unspecified Error";
			case ERROR_BAD_ARGUMENT: 
				return "Illegal cURL option argument";
			case ERROR_NULL_ARGUMENT:
				return "NULL cURL option argument passed";
			case ERROR_NO_CALLBACK_INSTANCE:
				return "cURL callback function has not been instantiated";
			case ERROR_FAILED_LOAD_LIBRARY: 
				return "Critical libraries couldn't be loaded";
			case ERROR_SECURITY_MANAGER_VIOLATION: 
				return "The Security Manager on this system does not allow me to load one or more critical libraries";
			case ERROR_NO_JAVACURL_HANDLE_AVAILABLE: 
				return "Critical libcurl: no javacurl handle returned";
			case  ERROR_OUT_OF_MEMORY:
				return "A critical libcurl memory allocation request failed";
			case ERROR_FAILED_INIT:
				return "Internal libcurl: cURL failed to initialize";
			case ERROR_NOT_IMPLEMENTED: 
				return "Option/Callback/Method not implmented";
			case ERROR_UNSUPPORTED_PROTOCOL:
				return "Protocol not supported by cURL";
			case ERROR_URL_MALFORMAT:
				return "Bad URL format";
			case ERROR_COULDNT_RESOLVE_PROXY:
				return "cURL could not resolve proxy host";
			case ERROR_COULDNT_RESOLVE_HOST:
				return "cURL could not resolve host";
			case ERROR_COULDNT_CONNECT:
				return "cURL could not make a connection";
		}
		// Uh oh...
		return "Unknown error";
	}

	/**
	 * Throws an appropriate exception based on the passed in error code.
	 *
	 * @param code the CURL error code
	 */
	public static void error (int code) {
		error (code, null);
	}

	/**	 
 	 * Throws an appropriate exception based on the passed in error code.
	 * The <code>throwable</code> argument should be either null, or the
	 * throwable which caused CURL to throw an exception.
	 */
	public static void error (int code, Throwable throwable) {
		error (code, throwable, null);
	}

	
	/**
	 * Throws an appropriate exception based on the passed in error code.
	 * The <code>throwable</code> argument should be either null, or the
	 * throwable which caused CURL to throw an exception.<br>
	 * <p>It was easier to combine all the error throwing code into one
	 * area for simple management.</p>
	 *
	 * @param code the CURL error code.
	 * @param throwable the exception which caused the error to occur.
	 * @param detail more information about error.
	 *
	 * @see CURLError
	 * @see CURLException
	 * @see IllegalArgumentException
	 */
	public static void error (int code, Throwable throwable, String detail) {
		/*
		* This code prevents the creation of "chains" of CURLErrors and
		* CURLExceptions which in turn contain other CURLErrors and 
		* CURLExceptions as their throwable. This can occur when low level
		* code throws an exception past a point where a higher layer is
		* being "safe" and catching all exceptions. (Note that, this is
		* _a_bad_thing_ which we always try to avoid.)
		*
		* On the theory that the low level code is closest to the
		* original problem, we simply re-throw the original exception here.
		*/
//		if (throwable instanceof CURLError) throw (CURLError) throwable;
//		if (throwable instanceof CURLException) throw (CURLException) throwable;
//
//		String message = findErrorText (code);
//		if (detail != null) message += detail;
//		switch (code) {
//
//			/* Illegal Arguments (non-fatal) */
//			case ERROR_BAD_ARGUMENT:
//			case ERROR_NOT_IMPLEMENTED: {
//				throw new IllegalArgumentException (message);
//			}
//
//			/* CURL Exceptions (non-fatal) */
//			case ERROR_NULL_ARGUMENT:
//			case ERROR_URL_MALFORMAT:
//			case ERROR_NO_CALLBACK_INSTANCE:
//			case ERROR_COULDNT_RESOLVE_PROXY:
//			case ERROR_UNSUPPORTED_PROTOCOL: {
//				CURLException exception = new CURLException (code, message);
//				exception.throwable = throwable;
//				throw exception;
//			}
//
//			/* OS Failure/Limit (fatal)*/
//			case ERROR_NO_JAVACURL_HANDLE_AVAILABLE:
//			case ERROR_FAILED_LOAD_LIBRARY:
//			//FALL THROUGH
//
//			/* CURL Failure (fatal) */
//			case ERROR_FAILED_INIT:
//			case ERROR_OUT_OF_MEMORY:
//			case ERROR_SECURITY_MANAGER_VIOLATION:
//			case ERROR_UNSPECIFIED: {
//				CURLError error = new CURLError (code, message);
//				error.throwable = throwable;
//				throw error;
//			}
//		}
//
//		/* Unknown/Undefined Error */
//		CURLError error = new CURLError (code, message);
//		error.throwable = throwable;
//		throw error;
	}
}