/*****************************************************************************
 *                                  _   _ ____  _     
 *  Project                     ___| | | |  _ \| |    
 *                             / __| | | | |_) | |    
 *                            | (__| |_| |  _ <| |___ 
 *                             \___|\___/|_| \_\_____|
 *
 * Copyright (C) 2001, Daniel Stenberg, <daniel@haxx.se>, et al.
 *
 * In order to be useful for every potential user, curl and libcurl are
 * dual-licensed under the MPL and the MIT/X-derivate licenses.
 *
 * You may opt to use, copy, modify, merge, publish, distribute and/or sell
 * copies of the Software, and permit persons to whom the Software is
 * furnished to do so, under the terms of the MPL or the MIT/X-derivate
 * licenses. You may pick one of these licenses.
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY
 * KIND, either express or implied.
 *
 * $Id: javacurl.c,v 1.1.1.1 2001/10/01 07:36:58 bagder Exp $
 *****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

#include <curl.h> /* libcurl header */
#include "com_nn_CurlGlue.h"  /* the JNI-generated glue header file */


/*
 * This is a private struct allocated for every 'CurlGlue' object.
 */
struct javacurl {
  void *libcurl;
  void *whatever;
  void *headers;
//  int headers_cleaned;
  struct writecallback {
    jmethodID mid_header_callback;
    jmethodID mid_write_callback;
    JNIEnv *java;
    jclass cls; /* global reference */
    jobject object;
  } write;
};

struct javapost_last {
  struct curl_httppost* post;
  struct curl_httppost* last;
};


JNIEXPORT jlong JNICALL Java_com_nn_CurlGlue_jni_1init(JNIEnv *java,
                                               jobject myself)
{
  void *libhandle;
  struct javacurl *jcurl=NULL;
  struct curl_slist *headers=NULL;

  curl_global_init(CURL_GLOBAL_ALL);
  libhandle = curl_easy_init();
  if(libhandle) {
    jcurl=(struct javacurl *)malloc(sizeof(struct javacurl));
    if(jcurl) {
      memset(jcurl, 0, sizeof(struct javacurl));
      jcurl->libcurl = libhandle;
      jcurl->headers = headers;

      jclass clz = (*java)->GetObjectClass(java, myself);
      jclass cls = (*java)->NewGlobalRef(java, clz);
      jobject object = (*java)->NewGlobalRef(java, myself);
      jcurl->write.cls = cls;
      jcurl->write.object = object;

      jmethodID mid_header_callback = (*java)->GetMethodID(java, cls, "onHeaderCallback", "(Ljava/lang/String;)V");
      if(!mid_header_callback) {
        puts("no mid_header_callback method found");
        return 0;
      }
      jmethodID mid_write_callback = (*java)->GetMethodID(java, cls, "onWriteCallback", "([B)V");
      if(!mid_write_callback) {
        puts("no mid_write_callback method found");
        return 0;
      }

      jcurl->write.mid_write_callback = mid_write_callback;
      jcurl->write.mid_header_callback = mid_header_callback;
    }
    else {
      curl_easy_cleanup(libhandle);
      return 0;
    }
  }

  return (uintptr_t)jcurl; /* nasty typecast */
}

JNIEXPORT void JNICALL Java_com_nn_CurlGlue_jni_1cleanup(JNIEnv *java,
                                                  jobject myself,
                                                  jlong jcurl)
{

  struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;

  if(curl->write.cls) {
    /* a global reference we must delete */
    (*java)->DeleteGlobalRef(java, curl->write.cls);
    (*java)->DeleteGlobalRef(java, curl->write.object);
  }

  curl_easy_cleanup(curl->libcurl); /* cleanup libcurl stuff */

  free((void *)curl); /* free the struct too */
}

JNIEXPORT void JNICALL Java_com_nn_CurlGlue_jni_1reset(JNIEnv *java,
                                                  jobject myself,
                                                  jlong jcurl)
{

  struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
  curl_easy_reset(curl->libcurl);
}

/*
 * setopt() int + string
 */
JNIEXPORT jint JNICALL Java_com_nn_CurlGlue_jni_1setopt__JILjava_lang_String_2
  (JNIEnv *java, jobject myself, jlong jcurl, jint option, jstring value)
{
  /* get the actual string C-style */
  const char *str = (*java)->GetStringUTFChars(java, value, 0);

  void *handle = (void *)((struct javacurl*)(uintptr_t)jcurl)->libcurl;

  // printf("setopt int + string: %d -> %s\n", option, str);
  
  jint res = (jint)curl_easy_setopt(handle, (CURLoption)option, str);
  //(*java)->ReleaseStringUTFChars(java, value, str);

  return (jint)res;
}

/*
 * setopt() int + int 
 */
JNIEXPORT jint JNICALL Java_com_nn_CurlGlue_jni_1setopt__JII
  (JNIEnv *java, jobject myself, jlong jcurl, jint option, jint value)
{
  void *handle = (void *)((struct javacurl*)(uintptr_t)jcurl)->libcurl;
  //CURLoption opt = (CURLoption)option;

//  printf("setopt int + int: %d -> %d\n", option, value);

  switch(option) {
  case CURLOPT_FILE:
    /* silently ignored, we don't need user-specified callback data when
       we have an object, and besides the CURLOPT_FILE is not exported
       to the java interface */
    return 0;
  }

  return (jint)curl_easy_setopt(handle, (CURLoption)option, value);
}

static int javacurl_write_callback(char *ptr,
                                   size_t size,
                                   size_t nmemb,
                                   void  *userdata)
{
  struct javacurl *curl = (struct javacurl *)userdata;
  size_t realsize = size * nmemb;
  JNIEnv *java = curl->write.java;
  jbyteArray jb=NULL;
  //int ret=0;

  jb=(*java)->NewByteArray(java, realsize);
  if (jb == NULL) {
      fprintf(stderr, "out of memory error thrown:\n curl ptr=%p, java=%p cls=%p\n",  curl, java, curl->write.cls);
      return 0; //  out of memory error thrown
  }

  (*java)->SetByteArrayRegion(java, jb, 0,  realsize, (jbyte*)ptr);
  (*java)->CallVoidMethod(java,
         curl->write.object,
         curl->write.mid_write_callback,
         jb);

//  jstring jstr = (*java)->NewStringUTF(java,ptr);
//  (*java)->CallVoidMethod(java, curl->write.object, curl->write.mid, jstr);

  return realsize;
}

static size_t write_data_to_file(void *ptr, size_t size, size_t nmemb, void *stream)
{
  size_t written = fwrite(ptr, size, nmemb, (FILE *)stream);
  return written;
}

static size_t javacurl_header_callback(char *buffer, size_t size, size_t nitems, void *userdata)
{
  /* received header is nitems * size long in 'buffer' NOT ZERO TERMINATED */
  /* 'userdata' is set with CURLOPT_HEADERDATA */
  struct javacurl *curl = (struct javacurl *)userdata;
//  size_t realsize = size * nmemb;
  JNIEnv *java = curl->write.java;

//  printf("received header %s\n", buffer);
  jstring jstr = (*java)->NewStringUTF(java,buffer);
  (*java)->CallVoidMethod(java, curl->write.object, curl->write.mid_header_callback, jstr);

  return nitems * size;
}

/*
 * setopt() int + object
 */

JNIEXPORT jint JNICALL Java_com_nn_CurlGlue_jni_1perform_1file(JNIEnv *java, jobject myself, jlong jcurl, jstring filename)
{
  struct javacurl *curl=(struct javacurl*)(uintptr_t)jcurl;
  curl->write.java = java;
//  printf("perform to file curl ptr=%p, java=%p \n", curl, java);

  const char *pagefilename = (*java)->GetStringUTFChars(java, filename, 0);


  CURL *curl_handle = curl->libcurl;
  FILE *pagefile;
  int out = 0;

  /* disable progress meter, set to 0L to enable and disable debug output */
  curl_easy_setopt(curl_handle, CURLOPT_NOPROGRESS, 1L);

  /* send all data to this function  */
  curl_easy_setopt(curl_handle, CURLOPT_HEADERFUNCTION, javacurl_header_callback);
  curl_easy_setopt(curl_handle, CURLOPT_WRITEFUNCTION, write_data_to_file);

  /* open the file */
  pagefile = fopen(pagefilename, "wb");
  if(pagefile) {

    /* write the page body to this file handle */
    curl_easy_setopt(curl_handle, CURLOPT_WRITEDATA, pagefile);
    curl_easy_setopt(curl_handle, CURLOPT_HEADERDATA, curl);
    curl_easy_setopt(curl_handle, CURLOPT_USERAGENT, "libcurl-agent/1.0");

    /* get it! */
    out = curl_easy_perform(curl_handle);

    /* close the header file */
    fclose(pagefile);
  }

  return (jint)out;
}

JNIEXPORT jint JNICALL Java_com_nn_CurlGlue_jni_1setopt__JILcom_nn_CurlWrite_2
  (JNIEnv *java, jobject myself, jlong jcurl, jint option, jobject object)
{
  jclass cls_local = (*java)->GetObjectClass(java, object);
  jmethodID mid_write_callback;
  jmethodID mid_header_callback;
  struct javacurl *curl = (struct javacurl *)(uintptr_t)jcurl;
  jclass cls;
  //jobject obj_global;

  switch(option) {
  case CURLOPT_WRITEFUNCTION:
    /* this makes a reference that'll be alive until we kill it! */
    cls = (*java)->NewGlobalRef(java, cls_local);

//    printf("setopt int + object, option = %d cls= %p\n", option, cls);

    if(!cls) {
      puts("couldn't make local reference global");
      return 0;
    }


    mid_header_callback = (*java)->GetMethodID(java, cls, "onHeaderCallback", "(Ljava/lang/String;)V");
    if(!mid_header_callback) {
      puts("no mid_header_callback method found");
      return 0;
    }
    mid_write_callback = (*java)->GetMethodID(java, cls, "onWriteCallback", "([B)V");
    if(!mid_write_callback) {
      puts("no mid_write_callback method found");
      return 0;
    }

    //obj_global = (*java)->NewGlobalRef(java, object);

    curl->write.mid_write_callback = mid_write_callback;
    curl->write.mid_header_callback = mid_header_callback;
    //curl->write.cls = cls;
    //curl->write.object = obj_global;
    /*curl->write.java = java; stored on perform */

//    fprintf(stderr, "setopt write callback and write file pointer %p, java = %p\n",  curl, java);

    curl_easy_setopt(curl->libcurl, CURLOPT_HEADERFUNCTION, javacurl_header_callback);
    curl_easy_setopt(curl->libcurl, CURLOPT_WRITEFUNCTION, javacurl_write_callback);
    curl_easy_setopt(curl->libcurl, CURLOPT_WRITEDATA, curl);
    curl_easy_setopt(curl->libcurl, CURLOPT_HEADERDATA, curl);
    curl_easy_setopt(curl->libcurl, CURLOPT_USERAGENT, "libcurl-agent/1.0");
    //curl_easy_setopt(curl->libcurl, CURLOPT_FILE,
      //               curl);

    break;
  }
  return 0;
}





JNIEXPORT jlong JNICALL Java_com_nn_CurlGlue_jni_1mimefpart
  (JNIEnv *java, jobject myself, jlong jcurl, jlong jcurl_post_last, jstring fName, jstring fPath)
{
  jclass clz = (*java)->GetObjectClass(java, myself);
  jmethodID logmsg = (*java)->GetMethodID(java, clz, "logFromC", "(Ljava/lang/String;)V");
  char out_txt[500];
  sprintf(out_txt, "Java_com_nn_CurlGlue_jni_1mimefpart");
  jstring jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  struct curl_httppost* post = NULL;
  struct curl_httppost* last = NULL;
  struct javapost_last* post_last = NULL;
  if (jcurl_post_last > -1) {
    printf("jcurl_post_last > -1\n");
    post_last = (struct javapost_last *)(uintptr_t)jcurl_post_last;
    post = post_last->post;
    last = post_last->last;
    printf("post: %ld -> last: %ld\n", post, last);
  } else {
    post_last = calloc(1, sizeof(struct javapost_last));
    post_last->post = NULL;
    post_last->last = NULL;
  }

  const char *fname_ = (*java)->GetStringUTFChars(java, fName, 0);
  const char *value_ = (*java)->GetStringUTFChars(java, fPath, 0);

  curl_formadd(&post, &last, CURLFORM_COPYNAME, fname_,
              CURLFORM_FILE, value_, CURLFORM_END);

  post_last->post = post;
  post_last->last = last;

  sprintf(out_txt, "AFTER post: %ld -> last: %ld", post, last);
  jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  sprintf(out_txt, "AFTER post2: %ld -> last: %ld", post_last->post, post_last->last);
  jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  sprintf(out_txt, "post_last: %ld", post_last);
  jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  return (uintptr_t)post_last;
}

JNIEXPORT jlong JNICALL Java_com_nn_CurlGlue_jni_1mimedpart
  (JNIEnv *java, jobject myself, jlong jcurl, jlong jcurl_post_last, jstring fieldName, jstring value)
{
  jclass clz = (*java)->GetObjectClass(java, myself);
  jmethodID logmsg = (*java)->GetMethodID(java, clz, "logFromC", "(Ljava/lang/String;)V");
  char out_txt[500];
  sprintf(out_txt, "Java_com_nn_CurlGlue_jni_1mimedpart");
  jstring jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  struct curl_httppost* post = NULL;
  struct curl_httppost* last = NULL;
  struct javapost_last* post_last = NULL;

  if (jcurl_post_last > -1) {
    sprintf(out_txt, "jcurl_post_last > -1\n");
    jstr = (*java)->NewStringUTF(java, out_txt);
    (*java)->CallObjectMethod(java, myself, logmsg, jstr);
    sprintf(out_txt, "post_last: %ld\n", jcurl_post_last);
    jstr = (*java)->NewStringUTF(java, out_txt);
    (*java)->CallObjectMethod(java, myself, logmsg, jstr);


    post_last = (struct javapost_last *)(uintptr_t)jcurl_post_last;
    post = post_last->post;
    last = post_last->last;


    sprintf(out_txt, "post: %ld -> last: %ld\n", post, last);
    jstr = (*java)->NewStringUTF(java, out_txt);
    (*java)->CallObjectMethod(java, myself, logmsg, jstr);
    sprintf(out_txt, "!!! post: %ld -> last: %ld\n", post_last->post, post_last->last);
    jstr = (*java)->NewStringUTF(java, out_txt);
    (*java)->CallObjectMethod(java, myself, logmsg, jstr);
  } else {
    post_last = calloc(1, sizeof(struct javapost_last));
    post_last->post = NULL;
    post_last->last = NULL;
  }

  const char *fname_ = (*java)->GetStringUTFChars(java, fieldName, 0);
  const char *value_ = (*java)->GetStringUTFChars(java, value, 0);

  sprintf(out_txt, "Java_com_nn_CurlGlue_jni_1mimedpart  2\n");
  jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  curl_formadd(&post, &last, CURLFORM_COPYNAME, fname_,
              CURLFORM_COPYCONTENTS, value_, CURLFORM_END);

  sprintf(out_txt, "Java_com_nn_CurlGlue_jni_1mimedpart  3\n");
  jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  post_last->post = post;
  post_last->last = last;

  sprintf(out_txt, "AFTER post: %ld -> last: %ld\n", post, last);
  jstr = (*java)->NewStringUTF(java, out_txt);
  (*java)->CallObjectMethod(java, myself, logmsg, jstr);

  return (uintptr_t)post_last;
}

JNIEXPORT jint JNICALL Java_com_nn_CurlGlue_jni_1mimesetopt
  (JNIEnv *java, jobject myself, jlong jcurl, jlong jcurl_post_last)
{
  printf("Java_com_nn_CurlGlue_jni_1mimesetopt\n");
  struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
  if (jcurl_post_last > -1) {
    struct javapost_last* post_last = (struct javapost_last *)(uintptr_t)jcurl_post_last;
    return (jint)curl_easy_setopt(curl->libcurl, CURLOPT_HTTPPOST, post_last->post);
  }
  return (jint)-1;
}

JNIEXPORT void JNICALL Java_com_nn_CurlGlue_jni_1formfree
  (JNIEnv *java, jobject myself, jlong jcurl, jlong jcurl_post_last)
{
  printf("Java_com_nn_CurlGlue_jni_1formfree\n");
  //struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
  if (jcurl_post_last > -1) {
    struct javapost_last* post_last = (struct javapost_last *)(uintptr_t)jcurl_post_last;
    curl_formfree(post_last->post);
  }
}




JNIEXPORT jlong JNICALL Java_com_nn_CurlGlue_jni_1getinfo
  (JNIEnv *java, jobject value, jlong jcurl, jint option)
{
    struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
    long ret = 0;
    curl_easy_getinfo (curl->libcurl, option, &ret);
    return (jlong)ret;
}

JNIEXPORT jstring JNICALL Java_com_nn_CurlGlue_jni_1getinfo_1str
  (JNIEnv *java, jobject value, jlong jcurl, jint option)
{
  struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
  char *ct;
  curl_easy_getinfo(curl->libcurl, option, &ct);
  jstring jstr = (*java)->NewStringUTF(java,ct);
  return jstr;
}

JNIEXPORT jint JNICALL Java_com_nn_CurlGlue_jni_1perform
  (JNIEnv *java, jobject myself, jlong jcurl)
{
  struct javacurl *curl=(struct javacurl*)(uintptr_t)jcurl;
  curl->write.java = java;
//  printf("perform curl ptr=%p, java=%p \n", curl, java);
  curl_easy_setopt(curl->libcurl, CURLOPT_HEADERFUNCTION, javacurl_header_callback);
  curl_easy_setopt(curl->libcurl, CURLOPT_WRITEFUNCTION, javacurl_write_callback);
  curl_easy_setopt(curl->libcurl, CURLOPT_WRITEDATA, curl);
  curl_easy_setopt(curl->libcurl, CURLOPT_HEADERDATA, curl);
  curl_easy_setopt(curl->libcurl, CURLOPT_USERAGENT, "libcurl-agent/1.0");
  return (jint)curl_easy_perform(curl->libcurl);
}


JNIEXPORT jint Java_com_nn_CurlGlue_jni_1free_1headers(JNIEnv *java, jobject myself, jlong jcurl)
{
  struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
  curl_slist_free_all(curl->headers);
  curl->headers = NULL;
  return 0;
}

JNIEXPORT jint Java_com_nn_CurlGlue_jni_1append_1header(JNIEnv *java, jobject myself, jlong jcurl, jstring value)
{
  struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
  const char *str = (*java)->GetStringUTFChars(java, value, 0);
  curl->headers = curl_slist_append(curl->headers , str);
  //(*java)->ReleaseStringUTFChars(java, value, str);
  return 0;
}

JNIEXPORT jint Java_com_nn_CurlGlue_jni_1set_1headers(JNIEnv *java, jobject myself, jlong jcurl)
{
  struct javacurl *curl = (struct javacurl*)(uintptr_t)jcurl;
  curl_easy_setopt(curl->libcurl, CURLOPT_HTTPHEADER, curl->headers);
  return 0;
}
