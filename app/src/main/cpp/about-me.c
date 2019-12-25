#include <string.h>
#include <jni.h>

JNIEXPORT jstring JNICALL
Java_id_ac_ui_cs_mobileprogramming_refo_1ilmiya_1akbar_readit_OpenGL_AboutMeTextRenderer_stringAboutMe(
        JNIEnv *env,
        jobject thiz) {
    jstring result = "Created By Refo Ilmiya";
    return (*env)->NewStringUTF(env, result);
}

