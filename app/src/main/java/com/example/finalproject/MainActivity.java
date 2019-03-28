package com.example.finalproject;

import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.TextView;
import java.io.IOException;
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
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private ChirpConnect chirp;

    String CHIRP_APP_KEY = "Cb5c84Eb72b08C89aD4edf3bE";
    String CHIRP_APP_SECRET = "1F2FcE0d59DfEE6B278aBE4e789FEcF7e6907aa0cE71Fcff5b";
    String CHIRP_APP_CONFIG = "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";
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                    FingerprintHandler helper = new FingerprintHandler(this, chirp);
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
}


