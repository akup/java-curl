package com.nn;

public interface CurlWrite
{
  /**
   * handleString gets called by libcurl on each chunk of data
   * we receive from the remote server
   */
  public void onWriteCallback(byte s[]);
  public void onHeaderCallback(String str);
}

