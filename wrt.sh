echo $1 $2 $3 $4 $5 $6

export SDK=$1
export STAGING_DIR=$SDK/staging_dir/$2
export PATH=$PATH:$STAGING_DIR/bin
export e=$SDK/staging_dir/$3
export LDFLAGS="-L$BASE/lib -L$BASE/usr/lib"
export CFLAGS="-I$BASE/usr/include $LDFLAGS -Wl,-rpath=$BASE/usr/lib"

$4 -c -o generated/$5/javacurl.o $6 -I/var/development/jdk1.7.0_55/include -I/var/development/jdk1.7.0_55/include/linux -I./generated/headers -I$SDK/staging_dir/$3/usr/include -I$SDK/staging_dir/$3/usr/include/curl src/main/jni/javacurl.c
cd generated/$5 && $4 -g -Wall -fPIC -I/var/development/jdk1.7.0_55/include $6 -I/var/development/jdk1.7.0_55/include/linux -I../../generated/headers -I$SDK/staging_dir/$3/usr/include -I$SDK/staging_dir/$3/usr/include/curl -L$SDK/staging_dir/$3/usr/lib -Wno-int-to-pointer-cast -Wno-pointer-to-int-cast -Werror-implicit-function-declaration -Wfatal-errors -shared -o libjavacurl.so ../../src/main/jni/javacurl.c -lcurl

