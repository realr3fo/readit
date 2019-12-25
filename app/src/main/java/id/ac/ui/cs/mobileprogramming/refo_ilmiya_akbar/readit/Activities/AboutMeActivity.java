package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.Activities;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.OpenGL.AboutMeSurfaceView;

public class AboutMeActivity extends Activity {
    private GLSurfaceView GLES20SurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLES20SurfaceView = new AboutMeSurfaceView(this);
        setContentView(GLES20SurfaceView);
    }

    @Override
    public void onPause() {
        super.onPause();
        GLES20SurfaceView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        GLES20SurfaceView.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}


