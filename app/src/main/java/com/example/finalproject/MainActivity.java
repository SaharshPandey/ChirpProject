package com.example.finalproject;

import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import android.util.Log;
import android.widget.Toast;

import com.mattprecious.swirl.SwirlView;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.models.ChirpError;
import io.chirp.connect.interfaces.ConnectEventListener;


public class MainActivity extends AppCompatActivity {

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView errorview;
    private ImageView imageView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private ChirpConnect chirp;


    String CHIRP_APP_KEY = "Cb5c84Eb72b08C89aD4edf3bE";
    String CHIRP_APP_SECRET = "1F2FcE0d59DfEE6B278aBE4e789FEcF7e6907aa0cE71Fcff5b";
    String CHIRP_APP_CONFIG = "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";

    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;
    public Typeface MR, MRR;
    public TextView text1,text2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setFinishOnTouchOutside(false);



        MR = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");
        MRR = Typeface.createFromAsset(getAssets(), "fonts/myriadregular.ttf");
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.textView2);

        imageView = findViewById(R.id.imageView);

        text1.setTypeface(MR);
        text2.setTypeface(MRR);
//
//        SwirlView swirlView = findViewById(R.id.swirl);
//        swirlView.setState(SwirlView.State.ON);

        authenticate();



    }

    public void running()
    {

        new GetUrlContentTask().execute("http://[2400:6180:100:d0::4fd:1004]:15000");
    }

public void authenticate(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        //instantiating the Chirp....
        chirp = new ChirpConnect(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
        } else {
            Log.e("ChirpError: ", error.getMessage());
        }



        //Get an instance of KeyguardManager and FingerprintManager//
        keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        errorview = findViewById(R.id.error);

        //Check whether the device has a fingerprint sensor//
        if (!fingerprintManager.isHardwareDetected()) {
            // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
            errorview.setText("Your device doesn't support fingerprint authentication");
        }
        //Check whether the user has granted your app the USE_FINGERPRINT permission//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // If your app doesn't have this permission, then display the following text//
            errorview.setText("Please enable the fingerprint permission");
        }

        //Check that the user has registered at least one fingerprint//
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            // If the user hasn’t configured any fingerprints, then display the following message//
            errorview.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
        }

        //Check that the lockscreen is secured//
        if (!keyguardManager.isKeyguardSecure()) {
            // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
            errorview.setText("Please enable lockscreen security in your device's Settings");
        } else {
            try {
                generateKey();
            } catch (FingerprintException e) {
                e.printStackTrace();
            }

            if (initCipher()) {
                //If the cipher is initialized successfully, then create a CryptoObject instance//
                cryptoObject = new FingerprintManager.CryptoObject(cipher);

                // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                FingerprintHandler helper = new FingerprintHandler(this, chirp,imageView);
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        }
    }
}



    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//

    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new

                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
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
    public void onBackPressed() {
        super.onBackPressed();
    }


    private class GetUrlContentTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
                Log.d("check","Url is : " +url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedReader rd = null;

            try {
                rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            // this is executed on the main thread after the process is over
            // update your UI here
            /*ImageView imageView = findViewById(R.id.imageView);
            TextView textView = findViewById(R.id.textView2);
            */
            /*setContentView(R.layout.activity_main);
            imageView.setImageResource(R.drawable.kj_success);
            text2.setText("Authorised");*/

        }
    }
}


