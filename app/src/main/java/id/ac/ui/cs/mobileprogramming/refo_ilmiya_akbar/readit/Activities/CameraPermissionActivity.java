package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.Activities;

import android.content.Intent;
import android.os.Bundle;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.R;

public class CameraPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_permission);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
