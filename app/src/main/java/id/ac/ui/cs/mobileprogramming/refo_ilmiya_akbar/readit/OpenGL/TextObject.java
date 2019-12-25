package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.OpenGL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class TextObject {

    private float theAnimRotZ = 0.0f;
    private Vector4f thePosition = new Vector4f();
    private String theText = "Empty";
    private int bitmapWidth;
    private int bitmapHeight;
    private boolean isUpdateNeeded = true;
    private int theViewportWidth = 0;
    private int theViewportHeight = 0;

    TextObject() {
        super();
        //
        bitmapWidth = 1;
        bitmapHeight = 1;
    }

    void setText(String aText) {
        theText = aText;
        isUpdateNeeded = true;
    }

    void setPosition(float aX, float aY, float aZ) {
        thePosition.set(aX, aY, aZ, 1.0f);
        isUpdateNeeded = true;
    }

    void setRelPos(float aZ) {
        setPosition(thePosition.x + (float) 0.0,
                thePosition.y + (float) 0.0,
                thePosition.z + aZ);

        if (thePosition.z > 0.9f)
            thePosition.z = 0.9f;
        if (thePosition.z < -4.0f)
            thePosition.z = -4.0f;
    }

    void update() {
        if (!isUpdateNeeded)
            return;
        Vector4f cLT = new Vector4f(-0.5f, -0.5f, 0.0f, 1.0f);
        Vector4f cLB = new Vector4f(-0.5f, 0.5f, 0.0f, 1.0f);
        Vector4f cRT = new Vector4f(0.5f, -0.5f, 0.0f, 1.0f);
        Vector4f cRB = new Vector4f(0.5f, 0.5f, 0.0f, 1.0f);

        cLT.makePixelCoords(mMVPMatrix, theViewportWidth, theViewportHeight);
        cLB.makePixelCoords(mMVPMatrix, theViewportWidth, theViewportHeight);
        cRT.makePixelCoords(mMVPMatrix, theViewportWidth, theViewportHeight);
        cRB.makePixelCoords(mMVPMatrix, theViewportWidth, theViewportHeight);

        Vector4f vl = Vector4f.sub(cLB, cLT);
        Vector4f vr = Vector4f.sub(cRB, cRT);
        float textSize = (vl.length3() + vr.length3()) / 2.0f;

        drawCanvasToTexture(theText, textSize);

        android.util.Log.i("INFO", "bmpSize[" + bitmapWidth + ", " + bitmapHeight + "]");

        isUpdateNeeded = false;
    }

    void init() {
        initShapes();

        String vertexShaderCode = "uniform mat4 uMVPMatrix;   \n" +

                "attribute vec4 vPosition;  \n" +
                "attribute vec4 vTexCoord;  \n" +

                "varying vec4 v_v4TexCoord; \n" +

                "void main(){               \n" +

                " v_v4TexCoord = vTexCoord; \n" +
                " gl_Position = uMVPMatrix * vPosition; \n" +

                "}  \n";
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        String fragmentShaderCode = "precision mediump float;  \n" +
                "uniform sampler2D u_s2dTexture; \n" +
                "varying vec4 v_v4TexCoord; \n" +
                "void main(){              \n" +
                " gl_FragColor = texture2D(u_s2dTexture, v_v4TexCoord.xy); \n" +
                "}                         \n";
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        AboutMeTextRenderer.checkGLError("loadShaders");

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        AboutMeTextRenderer.checkGLError("glAttachShader:vert");
        GLES20.glAttachShader(mProgram, fragmentShader);
        AboutMeTextRenderer.checkGLError("glAttachShader:frag");
        GLES20.glLinkProgram(mProgram);
        AboutMeTextRenderer.checkGLError("glLinkProgram");
        GLES20.glUseProgram(mProgram);
        AboutMeTextRenderer.checkGLError("glUseProgram");

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        AboutMeTextRenderer.checkGLError("glGetAttribLocation:vPosition");

        maTexCoordsHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoord");
        AboutMeTextRenderer.checkGLError("glGetAttribLocation:vTexCoord");

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        AboutMeTextRenderer.checkGLError("glGetUniformLocation:uMVPMatrix");

        int muTextureHandle = GLES20.glGetUniformLocation(mProgram, "u_s2dTexture");
        AboutMeTextRenderer.checkGLError("glGetUniformLocation:u_s2dTexture");

        GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_NICEST);
        AboutMeTextRenderer.checkGLError("glHint");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        AboutMeTextRenderer.checkGLError("glActiveTexture");
        GLES20.glGenTextures(1, textureId, 0);
        AboutMeTextRenderer.checkGLError("glGenTextures");

        GLES20.glUniform1i(muTextureHandle, 0);
        AboutMeTextRenderer.checkGLError("glUniform1i");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        AboutMeTextRenderer.checkGLError("glBindTexture");

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        AboutMeTextRenderer.checkGLError("glTexParameterf:GL_TEXTURE_MAG_FILTER");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        AboutMeTextRenderer.checkGLError("glTexParameterf:GL_TEXTURE_MIN_FILTER");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        AboutMeTextRenderer.checkGLError("glTexParameterf:GL_TEXTURE_WRAP_S");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        AboutMeTextRenderer.checkGLError("glTexParameterf:GL_TEXTURE_WRAP_T");
    }

    void render() {
        GLES20.glUseProgram(mProgram);

        theAnimRotZ += 0.5f;

        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.rotateM(mMMatrix, 0, theAnimRotZ, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(mMMatrix, 0, (float) bitmapWidth / (float) bitmapHeight, 1.0f, 1.0f);
        Matrix.translateM(mMMatrix, 0, thePosition.x, thePosition.y, thePosition.z);

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 12, quadVB);
        GLES20.glEnableVertexAttribArray(maPositionHandle);

        GLES20.glVertexAttribPointer(maTexCoordsHandle, 3, GLES20.GL_FLOAT, false, 12, quadCB);
        GLES20.glEnableVertexAttribArray(maTexCoordsHandle);

        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    void updateCamera(int aWidth,
                      int aHeight) {
        theViewportWidth = aWidth;
        theViewportHeight = aHeight;

        float ratio = (float) aWidth / (float) aHeight;

        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.1f, 100.0f);
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        isUpdateNeeded = true;
    }

    private void drawCanvasToTexture(
            String aText,
            float aFontSize) {

        if (aFontSize < 8.0f)
            aFontSize = 8.0f;

        if (aFontSize > 500.0f)
            aFontSize = 500.0f;

        Paint textPaint = new Paint();
        textPaint.setTextSize(aFontSize);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(255, 255, 255, 255);

        textPaint.setSubpixelText(true);
        textPaint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));

        float realTextWidth = textPaint.measureText(aText);

        bitmapWidth = (int) (realTextWidth + 2.0f);
        bitmapHeight = (int) aFontSize + 2;

        Bitmap textBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        textBitmap.eraseColor(Color.argb(0, 255, 255, 255));

        Canvas bitmapCanvas = new Canvas(textBitmap);

        bitmapCanvas.drawText(aText, 1, 1.0f + aFontSize * 0.75f, textPaint);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        AboutMeTextRenderer.checkGLError("glBindTexture");

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textBitmap, 0);

        textBitmap.recycle();

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        AboutMeTextRenderer.checkGLError("glGenerateMipmap");
    }

    private void initShapes() {

        float[] quadVerts = {
                -0.5f, -0.5f, 0,
                -0.5f, 0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0
        };

        float[] quadCoords = {
                0.0f, 1.0f, 0,
                0.0f, 0.0f, 0,
                1.0f, 1.0f, 0,
                1.0f, 0.0f, 0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(quadVerts.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        quadVB = vbb.asFloatBuffer();
        quadVB.put(quadVerts);
        quadVB.position(0);

        vbb = ByteBuffer.allocateDirect(quadCoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        quadCB = vbb.asFloatBuffer();
        quadCB.put(quadCoords);
        quadCB.position(0);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private int muMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mProjMatrix = new float[16];


    private int mProgram;
    private int maPositionHandle;
    private int maTexCoordsHandle;
    private int[] textureId = new int[1];
    private FloatBuffer quadVB;
    private FloatBuffer quadCB;
}
