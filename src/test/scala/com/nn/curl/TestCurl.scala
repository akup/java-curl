package com.nn.curl

import java.io.File
import java.util.concurrent.Executors

import com.nn.{CURL, CurlGlue}
import org.junit.Assert._
import org.junit._
import org.junit.runners.MethodSorters
import org.pmw.tinylog.{Configurator, Level, Logger}

/**
  * Created by Ivan on 09.03.2017.
  */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TestCurl {
  @Before def init(): Unit = {
    Configurator.currentConfig().level(Level.TRACE).activate()
    System.setProperty("curl.verbose", "true")
  }
  //@After def after() = cg.finalize()

  private def getTokenFromString(baseStr: String, token: String): (Int, String) =  {
    val tokenInd = baseStr.indexOf(token)
    if (tokenInd < 0) {
      val statusStr = baseStr.substring(baseStr.indexOf("status")+8);
      statusStr.substring(0, 1).toInt -> ""
    } else {
      val tokenStr = baseStr.substring(tokenInd+token.length()+3)

      0 -> tokenStr.substring(0, tokenStr.indexOf('"'))
    }
  }

  def time[A](name:String, f: => A): A = {
    val t0 = System.currentTimeMillis()
    Logger.info("start "+name)
    val r = f
    Logger.info(name+" time:{} ms "+ (System.currentTimeMillis() - t0))
    r
  }

    //@Test
  /*
  def testDelay() = {
    println("=========START testDelay=============")
    //для теста нужно выставить таймауты до 10 секунд в CurlGlue
    //val cg = Curl.nextConnection
    assertEquals(0, Curl.setopt(CURL.OPT_URL, "https://httpbin.org/delay/10"))
    assertEquals(0, Curl.performBlocking(cg))
    Curl.returnConnection(cg)

    println("===============END==========")
  }

//  @Test
  def test1Get() = {
    println("=========START test1Get=============")
    val cg = Curl.nextConnection
    assertEquals(0, Curl.setopt(cg, CURL.OPT_URL, "https://httpbin.org/get"))
    val t0 = System.currentTimeMillis
    assertEquals(0, Curl.performBlocking(cg))
//    println(cg.getData)

    val t1 = System.currentTimeMillis
    assertEquals(0,Curl.setopt(cg, CURL.OPT_URL, "https://httpbin.org/get?aaa=123"))
    assertEquals(0,Curl.performBlocking(cg))
    println(cg.getContentType)
    println(new String(cg.getContent))
    val t2 = System.currentTimeMillis
    println(" first get: %d ms;  second get %d ms".format(t1 - t0, t2 - t1))
    println("===============END==========")

    println("=========START Post=============")
    val m = new java.util.HashMap[String, String]()
    m.put("Content-Type", "text/plain;charset=UTF-8")
    cg.setHeaders(m)
    val text = "some test text"
    assertEquals(0, Curl.setopt(cg, CURL.OPT_POSTFIELDS, text))
    assertEquals(0, Curl.setopt(cg, CURL.OPT_URL, "https://httpbin.org/post"))
    assertEquals(0, Curl.performBlocking(cg))
    assertEquals(200, cg.getResponseCode)
    println(cg.getContentType)
    println(new String(cg.getContent))

    assertEquals(0,Curl.setopt(cg, CURL.OPT_HTTPGET, 1))
    assertEquals(0,Curl.setopt(cg, CURL.OPT_URL, "https://httpbin.org/get"))
    assertEquals(0, Curl.performBlocking(cg))
    println("===============END==========")

  }
*/

//  @Test
  def test4CurlScala(): Unit = {
    println("=========START test4CurlScala=============")
    val t0 = System.currentTimeMillis()
    var res = Curl.execute("https://httpbin.org/get?aaa=123")
    val t1 = System.currentTimeMillis()
    res = Curl.execute("https://httpbin.org/get?bbb=456")
    val t2 = System.currentTimeMillis()

    println("time 1: %d ms, 2: %d ms".format(t1-t0, t2-t1)) //1446ms 282ms

    println(res)
    println(res.asString)

    val text = "some test text"
    res = Curl.execute( "https://httpbin.org/post", postData = Some(text))
    println(res)
    println(res.asString)

    res = Curl.execute("https://httpbin.org/get?bbb=456", inFile = true)
    val f = new File(res.fileName.get)
    println("f.exists "+f.exists())
    println(scala.io.Source.fromFile(f).getLines().mkString("\n"))
    res.deleteFile()

    println("===============END==========")
  }

//  @Test
  def test5CurlScala(): Unit = {
    println("=========START test5CurlScala=============")
    println("secureLogin")
    var res = Curl.execute("http://stage.nexpo.me/auth/login", postForm = Map("email"->"pdpopd@gmail.com","pass"->""))
    val token = getTokenFromString(new String(res.content), "temptoken")
    val param = Map("ak-fivesec-token" -> token._2, "ak-fivesec-token-email" -> "pdpopd@gmail.com")
    res = Curl.execute("http://stage.nexpo.me", getParams = param)
    println("===============END==========")
  }

//  @Test
  def test6WebDav():Unit = {
    println("=========START test6WebDav=============")
    val url = "http://admin:archivaPSW99@repo.nexpo.me/repository/registration/"
    val h = Map("DEPTH" -> "1")

    var res = Curl.execute("http://admin:archivaPSW99@repo.nexpo.me/repository/registration/", headers = h, customRequest = Some("PROPFIND"))
    println(res)
    println(res.asString)

    res = Curl.execute("http://admin:archivaPSW99@repo.nexpo.me/repository/registration/0.0.1-TEST/arm.tar.gz", inFile = true)
    println(res)
    val f = new File(res.fileName.get)
    println("f.exists "+f.exists())
    println("f.length "+f.length()/1024.0/1024.0+" mb")
    f.delete()
    println("===============END==========")
  }

//    @Test
  /*
  def test7():Unit = {
    println("=========START test7=============")
    val url2 = "http://admin:archivaPSW99@repo.nexpo.me/repository/registration/0.0.1-TEST/arm.tar.gz"

    val cgs = Seq(new CurlGlue, new CurlGlue, new CurlGlue)
    cgs.foreach(_.init())

    val v = Executors.newFixedThreadPool(8)

    def load(i:Int) = {
      new Runnable {
        override def run(): Unit = {
          val res = Curl.execute(cgs(i), url2 , inFile = true)
          val f = new File(res.fileName.get)
          println("f.exists "+f.exists())
          println("f.length "+f.length()/1024.0/1024.0+" mb")
          f.delete()
        }
      }
    }

    v.execute(load(1))
    v.execute(load(1))
    v.execute(load(2))

    println("===============END==========")
  }
   */

}