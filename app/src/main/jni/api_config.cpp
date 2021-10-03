#include <jni.h>
#include <string>

std::string SERVER_URL = "https://roboticsmex.com/movies_pop/rest-api/";
std::string API_KEY = "zllmosywo8rxorv919demjp2";
std::string PURCHASE_CODE = "14bb6f1d-de69-4a78-8fc9-13608288b18e";
std::string YOUTUBE_API_KEY = "xxxxxxxxxxxxxxxxxxxx";




//WARNING: ==>> Don't change anything below.
extern "C" JNIEXPORT jstring JNICALL
Java_com_peliculandia_pop_AppConfig_getApiServerUrl(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(SERVER_URL.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peliculandia_pop_AppConfig_getApiKey(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(API_KEY.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peliculandia_pop_AppConfig_getPurchaseCode(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(PURCHASE_CODE.c_str());
}

