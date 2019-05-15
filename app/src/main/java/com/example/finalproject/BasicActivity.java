package com.example.finalproject;


import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.models.ChirpError;

public class BasicActivity extends AppCompatActivity {

    ImageView authenticate;

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView errorview;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private ChirpConnect chirp;
    private String password = "saharsh";


    String CHIRP_APP_KEY = "Cb5c84Eb72b08C89aD4edf3bE";
    String CHIRP_APP_SECRET = "1F2FcE0d59DfEE6B278aBE4e789FEcF7e6907aa0cE71Fcff5b";
    String CHIRP_APP_CONFIG = "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";
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;

    TextView text,errors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);


        chirp = new ChirpConnect(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
        } else {
            Log.e("ChirpError: ", error.getMessage());
        }



        authenticate = findViewById(R.id.authenticate);
        text = findViewById(R.id.text);
        errors = findViewById(R.id.error);

        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setVisibility(View.VISIBLE);
                text.setText("Verifying......");
                errors.setVisibility(View.GONE);

                ConnectEventListener chirpEventListener = new ConnectEventListener() {

                    @Override
                    public void onSystemVolumeChanged(int i, int i1) {

                    }

                    @Override
                    public void onStateChanged(int i, int i1) {

                    }

                    @Override
                    public void onSent(@NotNull byte[] bytes, int i) {

                    }

                    @Override
                    public void onSending(@NotNull byte[] bytes, int i) {

                    }

                    @Override
                    public void onReceiving(int i) {

                    }

                    @Override
                    public void onReceived(byte[] data, int channel) {
                        if (data != null) {
                            String identifier = new String(data);
                            Log.v("ChirpSDK: ", "Received " + identifier);
                            Toast.makeText(BasicActivity.this, password, Toast.LENGTH_LONG).show();
                            if(identifier.equals(password))
                            {
                                Intent i = new Intent(BasicActivity.this,FinalActivity.class);
                                startActivity(i);
                            }
                        } else {

                            errors.setVisibility(View.VISIBLE);
                            text.setVisibility(View.GONE);
                            Log.e("ChirpError: ", "Decode failed");
                        }
                    }

                    };

                chirp.setListener(chirpEventListener);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
        else {
            ChirpError error = chirp.start();
            if (error.getCode() > 0) {
                Log.e("ChirpError: ", error.getMessage());
            } else {
                Log.v("ChirpSDK: ", "Started ChirpSDK");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ChirpError error = chirp.start();
                    if (error.getCode() > 0) {
                        Log.e("ChirpError: ", error.getMessage());
                    } else {
                        Log.v("ChirpSDK: ", "Started ChirpSDK");
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chirp.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chirp.stop();
        try {
            chirp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
