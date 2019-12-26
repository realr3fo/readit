package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.OpenGL;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AboutMeTextRenderer implements GLSurfaceView.Renderer {

    private TextObject theTextObj = new TextObject();

    private float theViewportHeight = 0.0f;

    private boolean mustRebuildText = true;

    AboutMeTextRenderer() {
        super();
    }

    void touchUp(float aX, float aY) {
        mustRebuildText = true;
    }

    void touchMove(float aX, float aY,
                   float aPrevX, float aPrevY) {
        theTextObj.setRelPos((aPrevY - aY) / (theViewportHeight / 4));
    }

    void touchDown(float aX, float aY) {
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        android.util.Log.i("INFO", "Extensions: " + GLES20.glGetString(GLES20.GL_EXTENSIONS));
        GLES20.glClearColor(0f, 0f, 0f, 0.5f);
        theTextObj.init();
        theTextObj.setText(stringAboutMe());
        theTextObj.setPosition(0.0f, 0.0f, -1.0f);

        checkGLError("onSurfaceCreated");
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        theTextObj.render();

        if (mustRebuildText) {
            theTextObj.update();
            mustRebuildText = false;
        }

        checkGLError("onDrawFrame");
    }

    static void checkGLError(final String aDesc) {
        int errorCode;
        do {
            errorCode = GLES20.glGetError();
            if (errorCode != GLES20.GL_NO_ERROR)
                android.util.Log.i("ERROR", "GL error: " + aDesc + " errorCode:" + errorCode);
        } while (errorCode != GLES20.GL_NO_ERROR);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        theViewportHeight = height;
        GLES20.glViewport(0, 0, width, height);
        theTextObj.updateCamera(width, height);
    }

    public native String stringAboutMe();

    static {
        System.loadLibrary("about-me");
    }
}
