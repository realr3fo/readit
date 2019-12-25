/* Copyright (c) 2013-2017, ARM Limited and Contributors
 *
 * SPDX-License-Identifier: MIT
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.OpenGL;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AboutMeTextRenderer implements GLSurfaceView.Renderer {

    private TextObject theTextObj = new TextObject();

    private float theViewportHeight = 0.0f;

    private boolean mustRebuildText = true;

    public AboutMeTextRenderer() {
        super();
    }

    // Touch UP event occured
    public void touchUp(float aX, float aY) {
        mustRebuildText = true;
    }

    // Touch UP event occured
    public void touchMove(float aX, float aY,
                          float aPrevX, float aPrevY) {
        theTextObj.setRelPos(0.0f, 0.0f, (aPrevY - aY)/(float)(theViewportHeight / 4));
    }

    // Touch UP event occured
    public void touchDown(float aX, float aY) {
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        android.util.Log.i("INFO", "Extensions: " + GLES20.glGetString(GLES20.GL_EXTENSIONS));

        // Set the background frame color
        GLES20.glClearColor(0f, 0f, 0f, 0.5f);

        // Initialize text object
        theTextObj.init();
        theTextObj.setText(stringAboutMe());
        theTextObj.setPosition(0.0f, 0.0f, -1.0f);

        checkGLError("onSurfaceCreated");
}

    public void onDrawFrame(GL10 unused) {

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Render text object
        theTextObj.render();

        if (mustRebuildText) {
            theTextObj.update();
            mustRebuildText = false;
        }

        checkGLError("onDrawFrame");
    }

    static public void checkGLError(final String aDesc) {
        int errorCode;
        do {
            errorCode =  GLES20.glGetError();
            if (errorCode != GLES20.GL_NO_ERROR)
                android.util.Log.i("ERROR", "GL error: " + aDesc + " errorCode:" + errorCode);
        } while (errorCode != GLES20.GL_NO_ERROR);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        theViewportHeight = height;
        // Update viewport
        GLES20.glViewport(0, 0, width, height);
        // Update Projection matrix
        theTextObj.updateCamera(width, height);
        }

    public native String stringAboutMe();

    static {
        System.loadLibrary("about-me");
    }
}
