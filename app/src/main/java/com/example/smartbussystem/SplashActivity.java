package com.example.smartbussystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();

        progressBar =
                findViewById(R.id.progressBar);

        startLoading();
    }

    private void startLoading() {

        new Thread(() -> {

            for (int i = 0; i <= 100; i++) {

                int progress = i;

                runOnUiThread(() ->
                        progressBar.setProgress(progress)
                );

                try {
                    Thread.sleep(30);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {

                Intent intent;

                if (auth.getCurrentUser() != null) {

                    intent = new Intent(
                            SplashActivity.this,
                            MainActivity.class
                    );

                } else {

                    intent = new Intent(
                            SplashActivity.this,
                            LoginActivity.class
                    );
                }

                startActivity(intent);

                overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );

                finish();

            });

        }).start();
    }
}