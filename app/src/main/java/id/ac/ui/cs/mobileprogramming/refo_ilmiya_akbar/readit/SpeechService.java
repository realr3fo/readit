package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.readit;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class SpeechService extends Service implements TextToSpeech.OnInitListener {

    public static final String EXTRA_WORD = "word";

    private TextToSpeech tts;
    private String words;
    private boolean isInit;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(getApplicationContext(), this);
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacksAndMessages(null);

        words = intent.getStringExtra(SpeechService.EXTRA_WORD);

        if (isInit) {
            speak();
        }

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                stopSelf();
            }

        }, 15 * 1000);

        return SpeechService.START_NOT_STICKY;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                speak();
                isInit = true;
            }
        }
    }

    private void speak() {
        if (tts != null) {
            tts.speak(words, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}