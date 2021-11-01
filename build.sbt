import sbt._
import scala.sys.process._

name := "LibCurl"

organization := "com.nn"

version := "0.6.0"

scalaVersion := "2.12.3"

resolvers += "Typesafe Repository" at "http://typesafe.artifactoryonline.com/typesafe/repo"

scalacOptions ++= Seq("-deprecation")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//unmanagedJars in Compile += file("resources/lib/compiler_2.10.jar")

//seq(jrebelSettings: _*)

//jrebel.enabled := true

//EclipseKeys.withSource := true

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.tinylog" % "tinylog" % "1.1",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

compile := {
  val cmpl = (compile in Compile).value
  val dir = (resourceManaged in Compile).value
  val src = (sourceDirectory in Compile).value
  val classDir = (classDirectory in Compile).value
  val runPath = (managedClasspath in Compile).value

  val home = System.getProperty("java.home")
  val basePath = runPath.map(_.data.toString).reduceLeft(_ + ":" + _)
  val classpath = classDir.toString + ":" + basePath
  val srcPath = src.getAbsolutePath
  var li = srcPath.lastIndexOf(System.getProperty("file.separator"))
  var rootPath = srcPath.substring(0, li)
  var openwrt_sdk = "/var/development2/openwrt15"
  var openwrt_toolchain = "toolchain-arm_cortex-a9+vfpv3_gcc-4.8-linaro_uClibc-0.9.33.2_eabi"
  var openwrt_target = "target-arm_cortex-a9+vfpv3_uClibc-0.9.33.2_eabi"
  var openwrt_gcc = "arm-openwrt-linux-uclibcgnueabi-gcc"
  var target_arch = "arm_cortex-a9"
  var arch_flags: String = "-DARM -DARM_ARCH" //"-DMIPS_ARCH"
  li = rootPath.lastIndexOf(System.getProperty("file.separator"))
  rootPath = rootPath.substring(0, li + 1)
  var f64 = new File("generated/linux64")
  if (!f64.exists()) f64.mkdirs()
  var f32 = new File("generated/linux32")
  if (!f32.exists()) f32.mkdirs()
  var t_arch_f = new File("generated/" + target_arch)
  if (!t_arch_f.exists()) t_arch_f.mkdirs()
  if (sys.props("os.name") == "Mac OS X") {
    var fmac = new File("generated/macosx")
    if (!fmac.exists()) fmac.mkdirs()
    var result = Process(
            ("javah" :: "-d" :: "generated/headers/" ::
            "-classpath" :: classDir.toString :: "com.nn.CurlGlue" :: Nil).toSeq,
            new java.io.File(rootPath)
            ) ! ;
    if (result == 0) {
      result = Process(
        ("gcc" :: "-c" :: "-o" :: "generated/macosx/javacurl.o" ::
          "-I/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/include/" ::
          "-I/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/include/darwin/" ::
          "-I./generated/headers" :: "-I/usr/include" :: "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include/curl/" ::
          "src/main/jni/javacurl.c" :: Nil).toSeq,
        new java.io.File(rootPath)
      ) ! ;
      if (result == 0) {
        result = Process(
          ("gcc" :: "-g" :: "-Wall" :: "-fPIC" :: "-I/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/include/" ::
            "-I/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/include/darwin/" ::
            "-I../headers" :: "-I/usr/include" :: "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include/curl/" ::
            "-L/usr/lib/" ::
            "-Wno-int-to-pointer-cast" :: "-Wno-pointer-to-int-cast" :: "-Werror-implicit-function-declaration" :: "-Wfatal-errors" :: 
            "-dynamiclib" :: "-undefined" :: "suppress" :: "-flat_namespace" ::
            "-o" :: "libjavacurl.dylib" :: "../../src/main/jni/javacurl.c" :: "-lcurl" :: Nil).toSeq,
          new java.io.File(rootPath + System.getProperty("file.separator") + "generated/macosx")
        ) ! ;
        if (result != 0) System.out.println("HAS ERRORS")
      } else System.out.println("Can not compile")
    } else System.out.println("Can not generate java headers")
  } else { //linux
    var result = Process(
      ("/var/development/jdk1.7.0_55/bin/javah" :: "-d" :: "generated/headers/" ::
        "-classpath" :: classDir.toString :: "com.nn.CurlGlue" :: Nil).toSeq,
      new java.io.File(rootPath)
    ) ! ;
    println("INFO: create headers: "+result)
    if (result == 0) {
      result = Process(
        ("gcc" :: "-c" :: "-o" :: "generated/linux64/javacurl.o" ::
          "-I/var/development/jdk1.7.0_55/include" :: "-I/var/development/jdk1.7.0_55/include/linux" ::
          "-I./generated/headers" :: "-I/usr/include" :: "-I/usr/include/curl" ::
          "src/main/jni/javacurl.c" :: Nil).toSeq,
        new java.io.File(rootPath)
      ) ! ;
      println("INFO: gcc javacurl: "+result)
      if (result == 0) {
        result = Process(
          ("gcc" :: "-g" :: "-Wall" :: "-fPIC" :: "-I/var/development/jdk1.7.0_55/include" :: "-I/var/development/jdk1.7.0_55/include/linux" ::
            "-I../headers" :: "-I/usr/include" :: "-I/usr/include/curl" ::
            "-L/usr/lib/x86_64-linux-gnu/" ::
            "-Wno-int-to-pointer-cast" :: "-Wno-pointer-to-int-cast" :: "-Werror-implicit-function-declaration" :: "-Wfatal-errors" :: "-shared" ::
            "-o" :: "libjavacurl.so" :: "../../src/main/jni/javacurl.c" :: "-lcurl" :: Nil).toSeq,
          new java.io.File(rootPath + System.getProperty("file.separator") + "generated/linux64")
        ) ! ;
        println("INFO: compiled x64: "+result)
        if (result == 0) {
          result = Process(
              ("gcc" :: "-m32" :: "-c" :: "-o" :: "generated/linux64/javacurl.o" :: "-D__i386__" ::
                "-I/var/development/jdk1.7.0_55/include" :: "-I/var/development/jdk1.7.0_55/include/linux" ::
                "-I./generated/headers" :: "-I/usr/include" :: "-I./curl-32" ::
                "src/main/jni/javacurl.c" :: Nil).toSeq,
              new java.io.File(rootPath)
            ) ! ;
          println("INFO: gcc javacurl x32: "+result)
          if (result == 0) {
            result = Process(
              ("gcc" :: "-m32" :: "-g" :: "-Wall" :: "-fPIC" :: "-I/var/development/jdk1.7.0_55/include" :: "-I/var/development/jdk1.7.0_55/include/linux" ::
                "-I../headers" :: "-I/usr/include" :: "-I../../curl-32" ::
                "-L/usr/lib/i386-linux-gnu" ::
                "-Wno-int-to-pointer-cast" :: "-Wno-pointer-to-int-cast" :: "-Werror-implicit-function-declaration" :: "-Wfatal-errors" :: "-shared" ::
                "-o" :: "libjavacurl.so" :: "../../src/main/jni/javacurl.c" :: "-lcurl" :: Nil).toSeq,
              new java.io.File(rootPath + System.getProperty("file.separator") + "generated/linux32")
            ) ! ;
            if (result != 0) println("HAS ERRORS 32")
            if (result == 0) {
              Process(("./wrt.sh" :: openwrt_sdk :: openwrt_toolchain :: openwrt_target :: openwrt_gcc :: target_arch :: arch_flags :: Nil).toSeq: Seq[String], new java.io.File(rootPath)) ! ;
            }
          } else System.out.println("Can not compile 32")
        } else System.out.println("HAS ERRORS")
      } else System.out.println("Can not compile")
    } else System.out.println("Can not generate java headers")
  }
  cmpl
}

//javaOptions += "-Djava.library.path=../libs_for_tests/"
javaOptions in Test += s"-Djava.library.path=../libs_for_tests/"

credentials in ThisBuild += Credentials(
  "Repository Archiva Managed internal Repository",
  "repo.nexpo.me",
  "admin",
  "archivaPSW99"
)

//publishTo in ThisBuild := Some("internal" at "http://repo.nexpo.me/repository/internal/")
