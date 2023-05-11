#include <jni.h>


JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getTallyBaseUrl(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "https://getqr.netpluspay.com/");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getTransactionBaseUrl(JNIEnv *env,
                                                                              jobject thiz) {
    return (*env)->NewStringUTF(env, "https://device.netpluspay.com/");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getMerchantBaseUrl(JNIEnv *env,
                                                                          jobject thiz) {
    return (*env)->NewStringUTF(env, "https://contactless.netpluspay.com/");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getCheckoutBaseUrl(JNIEnv *env,
                                                                          jobject thiz) {
    return (*env)->NewStringUTF(env, "https://api.netpluspay.com/");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getAuthUserName(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "qrUser2022");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getAuthPassword(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Qr3*fgZ(vBcdfP0^%klo51r");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getCheckoutMerchantId(JNIEnv *env,
                                                                             jobject thiz) {
    return (*env)->NewStringUTF(env, "MID63dbdc67badab");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getWebViewBaseUrl(JNIEnv *env,
                                                                         jobject thiz) {
    return (*env)->NewStringUTF(env, "https://api.netpluspay.com/transactions/requery/");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getTallyWalletBaseUrl(JNIEnv *env,
                                                                             jobject thiz) {
    return (*env)->NewStringUTF(env, "https://tally.netpluspay.com/api/");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getWalletXAPIToken(JNIEnv *env,
                                                                          jobject thiz) {
    return (*env) ->NewStringUTF(env, "27403342c95d1d83a40c0a8523803ec1518e2e5d6edd64b6296a81e8f94b1091");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getBearerToken(JNIEnv *env, jobject thiz) {
    return (*env) ->NewStringUTF(env, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTcsImVtYWlsIjoiZXpla2llbEBnbWFpbC5jb20iLCJmdWxsbmFtZSI6IlRlc3QiLCJtb2JpbGVfcGhvbmUiOiIwNzAzMzQ3NDE5OCIsImlhdCI6MTY4MjQxMTUxMiwiZXhwIjoxNzEzOTQ3NTEyfQ.LLEUvp_NplojFDsWXJ02vj_QwyU8HjgY9x32QWucQwM");
}