package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.R;
import id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private static final int requestPermissionID = 101;

    ActivityMainBinding binding;

    CameraSource mCameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        setUpTextToSpeechButton(cm);
        setUpAboutMeButton(cm);

        checkCameraPermission();
        startCameraSource();
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            requestPermissionID);
                }
            }
        }
    }

    private void setUpAboutMeButton(ConnectivityManager cm) {
        final ConnectivityManager cmFinal = cm;
        binding.aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkInfo activeNetwork = cmFinal.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    Intent intent = new Intent(getApplicationContext(), AboutMeActivity.class);
                    startActivity(intent);
                } else {
                    String title = getResources().getString(R.string.no_internet);
                    String message = getResources().getString(R.string.enable_internet_for_about_me);
                    alertDialog(title, message);

                }
            }
        });
    }


    private void setUpTextToSpeechButton(ConnectivityManager cm) {
        final ConnectivityManager cmFinal = cm;
        binding.textToSpeech.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String toSpeak = binding.textView.getText().toString();
                if (toSpeak.equals("")) {
                    String title = getResources().getString(R.string.no_text_detected);
                    String message = getResources().getString(R.string.no_text_on_camera);
                    alertDialog(title, message);
                } else {
                    NetworkInfo activeNetwork = cmFinal.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                    if (isConnected) {
                        Intent intent = new Intent(getApplicationContext(), ReadItActivity.class);
                        intent.putExtra("text", toSpeak);
                        startActivity(intent);
                    } else {
                        String title = getResources().getString(R.string.no_internet);
                        String message = getResources().getString(R.string.enable_internet_for_text_to_speech);
                        alertDialog(title, message);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(binding.surfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            startActivity(new Intent(this, CameraPermissionActivity.class));
        }
    }

    private void startCameraSource() {
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (textRecognizer.isOperational()) {
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(binding.surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {

                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {

                        binding.textView.post(new Runnable() {

                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); i++) {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                binding.textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    public void backPressAlert() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.quit))
                .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        backPressAlert();
        return true;
    }

    @Override
    public void onBackPressed() {
        backPressAlert();
    }

    private void alertDialog(String title, String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
