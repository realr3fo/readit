package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.OpenGL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class AboutMeSurfaceView extends GLSurfaceView {

    private AboutMeTextRenderer theRenderer = new AboutMeTextRenderer();
    private float prevX = 0.0f;
    private float prevY = 0.0f;

    public AboutMeSurfaceView(Context context) {
        super(context);

        setEGLConfigChooser(false);
        setEGLContextClientVersion(2);
        setRenderer(theRenderer);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            prevX = event.getRawX();
            prevY = event.getRawY();
            theRenderer.touchDown(prevX, prevY);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            theRenderer.touchUp(event.getRawX(), event.getRawY());
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getRawX();
            float y = event.getRawY();
            theRenderer.touchMove(x, y, prevX, prevY);
            prevX = x;
            prevY = y;
        }
        return true;
    }
}