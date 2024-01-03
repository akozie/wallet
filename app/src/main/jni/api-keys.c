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
    return (*env)->NewStringUTF(env, "https://paytally.netpluspay.com/");
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
    return (*env)->NewStringUTF(env, "https://test.tally.netpluspay.com/api/transactions/");
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

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getTallyConstant(JNIEnv *env, jobject thiz) {
    return (*env) ->NewStringUTF(env, "Tally");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getMerchantHeaderToken(JNIEnv *env,
                                                                              jobject thiz) {
    return (*env) ->NewStringUTF(env, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdG9ybUlkIjoiYzIzMDY1MTctNmE5Mi0xMWVhLTk1N2MtZjIzYzkyOWIwMDU3IiwidGVybWluYWxJZCI6IjIxMDFKQTI2IiwiYnVzaW5lc3NOYW1lIjoib2xhbWlkZUB3ZWJtYWxsLm5nIiwibWlkIjoiMCIsInBhcnRuZXJJZCI6bnVsbCwiZG9tYWlucyI6WyJuZXRwb3MiXSwicm9sZXMiOlsiYWRtaW4iXSwiaXNzIjoic3Rvcm06YWNjb3VudHMiLCJzdWIiOiJ1c2VyIiwiaWF0IjoxNjY3MjU3NDI3LCJleHAiOjE2OTg3OTM0Mjd9.5pI7PDOYGB6FdfbZNs7R6ewlMWFlw95eSZM6H6Gpl0g");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getMerchantHeaderTokenName(JNIEnv *env,
                                                                                  jobject thiz) {
    return (*env) ->NewStringUTF(env, "token");
}

JNIEXPORT jstring JNICALL
Java_com_woleapp_netpos_qrgenerator_utils_UtilityParam_getGoogleAppLink(JNIEnv *env, jobject thiz) {
    return (*env) ->NewStringUTF(env, "Fast, safe, secure contactless transactions all at your fingertips on the Tally network. Join me on https://play.google.com/store/apps/details?id=com.woleapp.netpos.qrgenerator and use my number ${Singletons().getCurrentlyLoggedInUser(context)?.mobile_phone} as referral code during registration");
}