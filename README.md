LibCurl
Пример смотреть в Test.java.
Под х64 компилится нормально(Тест проходит), с x32 есть проблема в том что, используются хидеры curl не для х32, а все равно из /usr/include/curl для х64.
изза проерки переменных CURL_SIZEOF_LONG и  CURL_SIZEOF_CURL_OFF_T в curlrulkes, переменные определены в curlbuild.
В OpenWRT пока не хватает curl headers.