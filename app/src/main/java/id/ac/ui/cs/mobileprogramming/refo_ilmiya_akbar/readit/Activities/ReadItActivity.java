package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.R;
import id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.Services.SpeechService;
import id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit.databinding.ActivityReadItBinding;

public class ReadItActivity extends AppCompatActivity {
    private final String CHANNEL_ID = "id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReadItBinding readItBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_read_it);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String toSpeak = intent.getStringExtra("text");
        readItBinding.tv.setText(toSpeak);

        boolean fromNotification = intent.getBooleanExtra("fromNotification", false);

        if (!fromNotification) {
            startSpeech(toSpeak, intent);
        }
    }

    private void startSpeech(String toSpeak, Intent intent) {
        Intent speechIntent = new Intent(getApplicationContext(), SpeechService.class);
        getApplicationContext().stopService(intent);

        speechIntent.putExtra(SpeechService.EXTRA_WORD, toSpeak);

        getApplicationContext().startService(speechIntent);


        createNotificationChannel();
        notificate(toSpeak);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void notificate(String toSpeak) {
        Intent intent = new Intent(this, ReadItActivity.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("text", toSpeak);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = createNotifBuilder(toSpeak, pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }

    private NotificationCompat.Builder createNotifBuilder(String toSpeak, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getResources().getString(R.string.notif_subject))
                .setContentText(toSpeak)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(toSpeak))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.readit_channel);
            String description = getResources().getString(R.string.readit_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
