package com.nn.curl

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}
import java.net.URLEncoder
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger

import com.nn.{CURL, CurlGlue}
import org.apache.commons.io.IOUtils

import scala.collection.JavaConverters._
import scala.language.postfixOps

/**
  * Created by Ivan on 10.03.2017.
  */
case class CurlResult(responseCode: Int,
                      __content: Array[Byte],
                      contentType: String,
                      headers:Seq[(String, String)],
                      fileName:Option[String],
                      cg: CurlGlue) {
  private var _stream:InputStream = _

  private var _content = __content
  def content: Array[Byte] = {
    if (_content != null)
      _content
    else {
      val s = stream()
      try {
        _content = IOUtils.toByteArray(s)
      } finally {
        if (s != null) s.close()
      }
      _content
    }
  }
  def asString: String = asString("UTF-8")
  def asString(charset: String): String = if (content != null) new String(_content, charset) else ""

  /**
    * не забудь закрыть.
    * @return
    */
  def stream():InputStream = {
    if (_stream != null)
      _stream
    else if(fileName.nonEmpty){
      _stream = new FileIS(fileName.get)
      _stream
    } else {
      _stream = new ByteArrayInputStream(Array.empty[Byte])
      _stream
    }
  }

  def deleteFile(): Unit = fileName.map(new File(_)).foreach(f => if (f.exists()) f.delete())
}
case class CurlCookie(name: String, value: String, domain: Option[String] = None)


//Это блокирующий интерфейс!!!
object Curl {
  private final val curlTempFolder = System.getProperty("service.files", ".") + "/curl_temp/"

  {
    val f = new File(curlTempFolder)
    if (f.exists()) {
      new File(curlTempFolder).listFiles().foreach(_.delete())
    }
    //Чистим не закрытые
    Runtime.getRuntime.addShutdownHook(new Thread(
      () => {
        close()
      }))
  }

  /*
  private lazy val cg: CurlGlue = {
    new File(curlTempFolder).listFiles().foreach(_.delete())
    val cg = new CurlGlue()
    cg.init()
    cg
  }
   */
  def close(): Unit = {
    var polled: CurlGlue = null
    for (elem <- connectionToHostQueues.values) {
      val queue = elem._1
      while ({polled = queue.poll(); polled != null})
        polled.finalize()
    }
  }
  def close(host: String) = {

  }

  //TODO: make settable
  //Sometimes number of connections from the same host is limited by server
  private final val connectionsPerHost = 20
  //private final val maxConnectionQueueSize = 100
  //private final val minConnectionQueueSize = 3
  //private final val connectionDequeueTime = 1000L * 30 //30 секунд держит в poolе содинение

  def extractHost(url: String): String = {
    val protoInd = url.indexOf("://")
    val hostEndInd = if (protoInd>0) url.indexOf("/", protoInd + 3) else url.indexOf("/")
    if (hostEndInd > 0) url.substring(0, hostEndInd) else url
  }

  private val waitsCount = new AtomicInteger(0)
  private val notifiedCount = new AtomicInteger(0)
  private var connectionToHostQueues = Map[String, (java.util.concurrent.ConcurrentLinkedQueue[CurlGlue], Int)]()

  //TODO:
  //Очередь соединений с отметкой о времени последнего использования (после return to queue, у активных зануляем)
  //Неиспользуемые соединения удаляем

  //Сейчас в очереди соединений во втором аргументе tuple хранится количество активных соединений
  //val queue: java.util.concurrent.ConcurrentLinkedQueue[(CurlGlue, Long)] = new java.util.concurrent.ConcurrentLinkedQueue[(CurlGlue, Long)]()
  private def nextConnection(url: String, uid: String): CurlGlue = {
    val host = extractHost(url)
    val (queue, active) = connectionToHostQueues.synchronized {
      connectionToHostQueues.get(host) match {
        case Some(queue) =>
          //Logger.debug("Connection reuse")
          connectionToHostQueues += host -> (queue._1, queue._2 + 1)
          queue
        case _ =>
          val queue = new java.util.concurrent.ConcurrentLinkedQueue[CurlGlue]()
          connectionToHostQueues += host -> (queue -> 1)
          queue -> 1
      }
    }

    //Logger.debug("curl queue before synchronized " + uid)
    while (notifiedCount.get() > 0) {
      Thread.sleep(1)
    }
    queue.synchronized {
      //Logger.debug("curl queue " + uid + " : " + queue.hashCode())
      if (queue.size() - notifiedCount.get() == 0) {
        //Logger.debug("queue empty " + uid + " : " + queue.hashCode())
        if (active < connectionsPerHost) {
          //Logger.debug("NEW CG " + uid)
          val cg = new CurlGlue()
          cg.init()
          cg
        } else {
          waitsCount.getAndIncrement()
          //Logger.debug("queue poll before wait hear " + uid + " : " + queue.hashCode())
          queue.wait()
          notifiedCount.getAndDecrement()
          //Logger.debug("queue poll after wait " + uid + " : " + queue.hashCode() + " : " + queue.size() + " : " + (queue.peek() == null))
          queue.poll()
        }
      } else {
        //Logger.debug("queue poll " + uid + " : " + queue.hashCode() + " : " + queue.size() + " : " + (queue.peek() == null))
        queue.poll()
      }
    }
  }
  private def returnConnection(ret: CurlGlue, url: String): Unit = {
    val host = extractHost(url)
    val (queue, active) = connectionToHostQueues(host)
    queue.synchronized{
      if (queue.isEmpty && waitsCount.get() > 0) {
        queue.add(ret)
        //Logger.debug("returnConnection and notify: " + queue.hashCode())
        notifiedCount.getAndIncrement()
        queue.notify()
        waitsCount.getAndDecrement()
      } else
        queue.add(ret)
    }

    connectionToHostQueues += host -> (queue, active - 1)
    //Logger.trace("return connection: " + queue.size + " : " + (active - 1))
  }
  /*
  def dequeuOld(): Unit = {
    val t = System.currentTimeMillis()
    while (queue.size > minConnectionQueueSize && Option(queue.peek()).exists(_._2 + connectionDequeueTime < t)) {
      queue.poll()
    }
  }
   */


  private var default_connection_timeout: Option[Int] = None
  private var default_timeout: Option[Int] = None

  def setDefaultConnectionTimeout(timeout: Int): Unit = default_connection_timeout = Some(timeout)
  def clearDefaultConnectionTimeout(): Unit = default_connection_timeout = None
  def setDefaultTimeout(timeout: Int): Unit = default_timeout = Some(timeout)
  def clearDefaultTimeout(): Unit = default_timeout = None

  private def givenUsingPlainJava_whenGeneratingRandomStringBounded_thenCorrect() = {
    val leftLimit = 97; // letter 'a'
    val rightLimit = 122; // letter 'z'
    val targetStringLength = 10;
    val random = new Random();
    val buffer = new StringBuilder(targetStringLength);
    var i = 0
    while (i < targetStringLength) {
      val randomLimitedInt = leftLimit +
        (random.nextFloat() * (rightLimit - leftLimit + 1)).toInt
      buffer.append(randomLimitedInt.toChar)

      i += 1
    }
    val generatedString = buffer.toString();

    generatedString
  }
  /**
  *
  * @param url url адрес
  * @param postData если не пусто, то post запрос с Content-Type: text/plain;charset=UTF-8
  * @param postForm если не пусто, то post запрос с Content-Type: application/x-www-form-urlencoded;charset=UTF-8
  * @param headers заголовки запроса
  * @param getParams если не пусто, то добавляются к url
  */
  def execute(url: String,
              postData: Option[String] = None,
              postFiles: Seq[File] = Nil,
              postForm: Map[String, String] = Map.empty,
              headers: Map[String, String] = Map.empty,
              getParams: Map[String, String] = Map.empty,
              inFile:Boolean = false,
              requestType: Option[String] = None,
              withCookie: Seq[CurlCookie] = Nil,
              putRequest: Boolean = false,
              deleteRequest: Boolean = false,
              _cg: Option[CurlGlue] = None,
              connection_timeout: Option[Int] = None,
              timeout: Option[Int] = None): CurlResult = {

    val uid = givenUsingPlainJava_whenGeneratingRandomStringBounded_thenCorrect()
    val cg = _cg.flatMap(cg => if (cg.isFinalized) None else Some(cg)).getOrElse(nextConnection(url, uid))
    withCookie.foreach(withCookie => {
      cg.putCookies(withCookie.domain.orNull, withCookie.name, withCookie.value)
    })

    val t0 = System.currentTimeMillis()
//    var res:CurlResult = CurlResult(404, Array.empty[Byte], "", Seq.empty[(String, String)], None)
    //Logger.trace("wait lock. " +Thread.currentThread().getName)
    //Logger.debug("cg " + uid + " : " + cg + " : " + _cg)
    cg.locker.lock()
    //Logger.trace("locked" +Thread.currentThread().getName)
    try {
      var post = false
      var _headers = headers
      var fullUrl = url
      
      var javacurl_mime = -1L

      if (getParams.nonEmpty) {
        fullUrl = url + "?" + paramToFormText(getParams)
        //Logger.trace("getParams: {}", getParams)
      }
      //Logger.trace(fullUrl)

      cg.reset()
      if (postFiles.isEmpty) {
        //Logger.info("CurlWrapper execute: " + postForm)
        postData.foreach(text => {
	        //Logger.trace("post text: {}", text)
          if (!_headers.exists(kv => {
            kv._1.toLowerCase() == "content-type"
          })) _headers += ("Content-Type" -> "text/plain;charset=UTF-8")
	        setopt(cg, CURL.OPT_POSTFIELDS, text)
	        post = true
	      })
	
	      if (postForm.nonEmpty) {
	        _headers += ("Content-Type" -> "application/x-www-form-urlencoded;charset=UTF-8")
	        val str = paramToFormText(postForm)
	        //Logger.info("post  form: {}", str)
	        setopt(cg, CURL.OPT_POSTFIELDS, str)
	        post = true
	      }
      } else {
        //Logger.info("postFiles: " + postFiles)
        
        postFiles.foreach(f => {
          javacurl_mime = cg.addMimeFilePart(javacurl_mime, f.getName, f.getAbsolutePath)
          //Logger.info("javacurl_mime: " + javacurl_mime)
        })
        //Logger.info("files ok")
        postForm.foreach(pf => {
          javacurl_mime = cg.addMimeDataPart(javacurl_mime, pf._1, pf._2)
          //Logger.info("javacurl_mime data: " + javacurl_mime)
        })
        //Logger.info("data ok")
        
        _headers += ("Content-Type" -> "multipart/form-data;charset=UTF-8")
        post = true
      }

      if (_headers.nonEmpty) {
        _headers.foreach(x => addHeader(cg, x._1, x._2))
        //Logger.trace("headers: {}", _headers)
        cg.setHeaders()
      }

      if (!post && !putRequest)
        setopt(cg, CURL.OPT_HTTPGET, 1)

      if (putRequest)
        setopt(cg, CURL.OPT_CUSTOMREQUEST, "PUT")
      if (deleteRequest)
        setopt(cg, CURL.OPT_CUSTOMREQUEST, "DELETE")
      requestType.foreach(rt => setopt(cg, CURL.OPT_CUSTOMREQUEST, rt))
        
      cg.mimeSetopt(javacurl_mime)
      //Logger.info("BEFORE PERFORM")

      //customRequest.foreach(s => setopt(cg, CURL.OPT_CUSTOMREQUEST, s))

      //Logger.info("fullUrl:{} isPost:{} inFile:{}", fullUrl, post.toString, inFile.toString)
      setopt(cg, CURL.OPT_URL, fullUrl)
      setopt(cg, CURL.OPT_TCP_KEEPALIVE, 1)

      val d = new File(curlTempFolder)
      if (!d.exists()) d.mkdirs()

      val fileName = curlTempFolder + System.nanoTime
      val curlCode = check("perform",
        if (inFile)
          cg.perform_file(fileName,
            connection_timeout.map(new Integer(_)).orNull,
            timeout.map(new Integer(_)).orNull)
        else
          cg.perform(
            connection_timeout.map(new Integer(_)).orNull,
            timeout.map(new Integer(_)).orNull),
        if (inFile) fileName else ""
      )

      //customRequest.foreach(_ => cg.reset())

      if (_headers.nonEmpty) {
        cg.cleanHeaders()
        cg.setHeaders()
      }
      //Logger.info("BEFORE FREE")
      cg.formfree(javacurl_mime)
      //Logger.info("AFTER FREE")

      val responseHeaders = cg.getResponseHeaders.asScala.flatMap(s => {
        val a = s.split(":", 2)
        if (a.length == 2)
          Some((a(0).trim, a(1).trim))
        else if (a.length == 1)
          Some((a(0).trim, ""))
        else None
      })

      //Logger.trace("time: " + (System.currentTimeMillis() - t0))

      //Logger.info("fileName: " + inFile + " : " + fileName)
      if (curlCode == 28)
        CurlResult(-1, null, null, Nil, None, null)
      else
        CurlResult(cg.getResponseCode,
          if (inFile) null else cg.getContent,
          if (cg.getContentType != null) cg.getContentType.trim else null,
          responseHeaders,
          if (inFile) Some(fileName) else None,
          cg
        )
    } finally {
      if (_cg.isEmpty)
        returnConnection(cg, url)
      cg.locker.unlock()
      //Logger.trace("unlock" +Thread.currentThread().getName)
    }
  }

  def paramToFormText(params: Map[String, String]): String =  params.map(x =>
    URLEncoder.encode(x._1, "UTF-8")+"="+URLEncoder.encode(x._2, "UTF-8")
  ).mkString("&")

  private def check(action: String,  res:Int, value:AnyRef): Int ={
    if (res != 0 )
      println("Curl %s value: %s %d".format(action, value.toString, res))
      //Logger.error("Curl %s value: %s %d".format(action, value.toString, res))
    res
  }

  private[curl] def setopt(cg: CurlGlue, option:Int, value:String ): Unit = check("setopt", cg.setopt(option, value), (option, value))
  private[curl] def setopt(cg: CurlGlue, option:Int, value: Int ): Unit = check("setopt", cg.setopt(option, value), (option, value))
  private def addHeader(cg: CurlGlue, name:String, value:String) = cg.addHeader(name, value)

  private[curl] def performBlocking(cg: CurlGlue): Unit =  check("perform",
    cg.perform(null, null), "")
  private[curl] def performBlockingToFile(cg: CurlGlue, file: String): Unit =  check("perform file",
    cg.perform_file(file, null, null), "")
}

private class FileIS(fn: String) extends FileInputStream(fn) {
  override def close(): Unit = {
    super.close()
    new File(fn).delete()
  }
}