package com.nn;/*
 * The curl class is a JNI wrapper for libcurl. Please bear with me, I'm no
 * true java dude (yet). Improve what you think is bad and send me the updates!
 * daniel.se
 *
 * This is meant as a raw, crude and low-level interface to libcurl. If you
 * want fancy stuff, build upon this.
 */

import org.pmw.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class CurlGlue implements CurlWrite {
  private static boolean loaded = false;
  private List<String> responseHeaders = new ArrayList<String>(20);
  private ByteArrayOutputStream bab = new ByteArrayOutputStream();
  public ReentrantLock locker = new ReentrantLock();

  private static java.lang.reflect.Field LIBRARIES;
  static {
    try {
      LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
      LIBRARIES.setAccessible(true);
    } catch (NoSuchFieldException e) {
      Logger.error(e);
      //e.printStackTrace();
    }
  }
  public static String[] getLoadedLibraries(final ClassLoader loader) throws IllegalAccessException {
    if (LIBRARIES.get(loader) instanceof java.util.HashSet) {
      final java.util.HashSet<String> libraries = (java.util.HashSet<String>) LIBRARIES.get(loader);
      return libraries.toArray(new String[] {});
    } else if (LIBRARIES.get(loader) instanceof java.util.Vector) {
      final Vector<String> libraries = (Vector<String>) LIBRARIES.get(loader);
      return libraries.toArray(new String[] {});
    }

    return new String[] {};
  }

  static {
    if (!loaded)
      try {
        // Loading up libjavacurl.so
        Logger.debug("Loaded libraries");
        boolean loadedBefore = false;
        for (String lib: getLoadedLibraries(ClassLoader.getSystemClassLoader())) {
          int lastSlash = lib.lastIndexOf("/");
          if (lastSlash > -1)
            lib = lib.substring(lastSlash + 1);
          int extention = lib.lastIndexOf(".");
          if (extention > -1)
            lib = lib.substring(0, extention);
          Logger.debug("Lib: " + lib);

          if (lib.equals("libjavacurl")) {
            loadedBefore = true;
            break;
          }
        }

        if (!loadedBefore)
          System.loadLibrary("javacurl");

        loaded=true;
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  public CurlGlue() {
      try {
        javacurl_handle = jni_init();
        System.out.println("jni_init "+javacurl_handle);
      } catch (Exception e) {
        e.printStackTrace();
      }
  }
  
  private void logFromC(String msg) {
    if (msg == null) System.out.println("NULL");
    else {
      System.out.println("LOG FROM C: " + msg);
    }
  }

  public void init(){
  	//System.out.println("INIT CURL");
    int res = setopt(CURL.OPT_WRITEFUNCTION, this);
    if ("true".equalsIgnoreCase(System.getProperty("curl.verbose", "false")))
      setopt(CURL.OPT_VERBOSE, 1);
    //System.out.println("INIT CURL OK: CURLOPT_WRITEFUNCTION "+res);
  }

  private boolean _finalized = false;
  public boolean isFinalized() {
    return _finalized;
  }
  public void finalize() {
    _finalized = true;
    jni_cleanup(javacurl_handle);
  }

  public void reset() {
    jni_reset(javacurl_handle);
  }

  private long javacurl_handle;

  /* constructor and destructor for the libcurl handle */
  private native long jni_init();
  private native void jni_cleanup(long javacurl_handle);
  private native void jni_reset(long javacurl_handle);
  private native synchronized int jni_perform(long javacurl_handle);
  private native synchronized int jni_perform_file(long javacurl_handle, String filename);
  // Instead of varargs, we have different functions for each
  // kind of type setopt() can take
  private native int jni_setopt(long libcurl, int option, String value);
  private native int jni_setopt(long libcurl, int option, int value);
  private native int jni_setopt(long libcurl, int option, CurlWrite value);
  
  private native long jni_mimefpart(long libcurl, long javacurl_mime, String fName, String fPath);
  private native long jni_mimedpart(long libcurl, long javacurl_mime, String fieldName, String value);
  private native int jni_mimesetopt(long libcurl, long javacurl_mime);
  private native void jni_formfree(long libcurl, long javacurl_mime);

  private native int jni_free_headers(long libcurl); //step 0
  private native int jni_append_header(long libcurl, String value); //step 1..n
  private native int jni_set_headers(long libcurl); //step n+1
  private native String[] jni_get_cookies(long libcurl);

  public native long jni_getinfo(long libcurl, int option);
  public native String jni_getinfo_str(long libcurl, int option);

  public int perform(Integer connection_timeout,
                     Integer timeout) {
    responseHeaders.clear();
    bab.reset();

    int conn_to = 20;
    if (connection_timeout != null) conn_to = connection_timeout;
    int to = 60;
    if (timeout != null) to = timeout;

    jni_setopt(javacurl_handle, CURL.OPT_CONNECTTIMEOUT, conn_to);
    jni_setopt(javacurl_handle, CURL.OPT_TIMEOUT, to);

    return jni_perform(javacurl_handle);
  }

  public int perform_file(String filename,
                          Integer connection_timeout,
                          Integer timeout) {
    responseHeaders.clear();
    bab.reset();

    int conn_to = 20;
    if (connection_timeout != null) conn_to = connection_timeout;
    int to = 60;
    if (timeout != null) to = timeout;

    jni_setopt(javacurl_handle, CURL.OPT_CONNECTTIMEOUT, conn_to);
    jni_setopt(javacurl_handle, CURL.OPT_TIMEOUT, to);

    return jni_perform_file(javacurl_handle, filename);
  }

  public int setopt(int option, int value) {
    return jni_setopt(javacurl_handle, option, value);
  }
  public int setopt(int option, String value) {
    return jni_setopt(javacurl_handle, option, value);
  }
  public int setopt(int option, CurlWrite value) {
    return jni_setopt(javacurl_handle, option, value);
  }
  public long addMimeFilePart(long javacurl_mime, String fName, String fPath) {
  	return jni_mimefpart(javacurl_handle, javacurl_mime, fName, fPath);
  }
  public long addMimeDataPart(long javacurl_mime, String fieldName, String value) {
  	return jni_mimedpart(javacurl_handle, javacurl_mime, fieldName, value);
  }
  public void mimeSetopt(long javacurl_mime) {
  	if (javacurl_mime>-1L)
  		jni_mimesetopt(javacurl_handle, javacurl_mime);
  }
  public void formfree(long javacurl_mime) {
  	if (javacurl_mime>-1L)
  		jni_formfree(javacurl_handle, javacurl_mime);
  }
  
  public int cleanHeaders() {
    return jni_free_headers(javacurl_handle);
  }
  public int addHeader(String name, String value) {
    return jni_append_header(javacurl_handle, name+":"+value);
  }
  public long getInfo(int option) {
    return jni_getinfo(javacurl_handle, option);
  }
  public String getInfoStr(int option) {
    return jni_getinfo_str(javacurl_handle, option);
  }

  public int setHeaders() {
    return jni_set_headers(javacurl_handle);
  }

  public int getResponseCode(){
    return (int)jni_getinfo(javacurl_handle, CURL.INFO_RESPONSE_CODE);
  }

  public String getContentType(){
    return jni_getinfo_str(javacurl_handle, CURL.INFO_CONTENT_TYPE);
  }

  public void setHeaders(Map<String,String> headers){
    cleanHeaders();
    for(Map.Entry<String,String> he : headers.entrySet()){
      addHeader(he.getKey(), he.getValue());
    }
    setHeaders();
  }

  public byte[] getContent() {
    return bab.toByteArray();
  }

  public List<String> getResponseHeaders() {
    return responseHeaders;
  }

  public void onHeaderCallback(String str) {
    responseHeaders.add(str);
  }

  public void onWriteCallback(byte s[]) {
    try {
      bab.write(s);
    } catch (Exception e){
      System.err.println(e.getMessage());
    }
  }

  public int putCookies(String domain, String name, String value) {
    String domainStr;
    if (domain == null) domainStr = "";
    else domainStr = String.format(" Domain=%s;", domain);

    String s = String.format("Set-Cookie: %s=%s;%s", name, value, domainStr);
    return setopt(CURL.OPT_COOKIELIST, s);
  }
}

