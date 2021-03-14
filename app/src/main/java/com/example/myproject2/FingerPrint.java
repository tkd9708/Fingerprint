package com.example.myproject2;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

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


@TargetApi(Build.VERSION_CODES.M)
public class FingerPrint {

    private static final String TAG = "FingerPrint";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_NAME = "mKey";

    private static FingerPrint _instance;
    public static FingerPrint getInstance() {
        if (_instance == null) _instance = new FingerPrint();
        return _instance;
    }

    private FingerprintManager fingerprintManager;
    private FingerprintManager.CryptoObject cryptoObject;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private CancellationSignal cancellationSignal;

    @TargetApi(Build.VERSION_CODES.M)
    public void initialize(Context context, FingerprintManager.AuthenticationCallback callback) {
        setKeyStore(context);
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject
                , cancellationSignal
                , 0
                , callback
                , null);
        if(!isHardwareAvailable()) return;
        if(!isFingerPassCode()) return;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean isHardwareAvailable() {
        return fingerprintManager.isHardwareDetected();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean isFingerPassCode() {
        return fingerprintManager.hasEnrolledFingerprints();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setKeyStore(Context context) {
        fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        cryptoObject = new FingerprintManager.CryptoObject(cipher);

        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        } catch (NoSuchProviderException
                | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME
                    , KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (IOException
                | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | CertificateException e) {
            e.printStackTrace();
        }

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES
                    + "/" + KeyProperties.BLOCK_MODE_CBC
                    + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (IOException
                | NoSuchAlgorithmException
                | UnrecoverableKeyException
                | CertificateException
                | InvalidKeyException
                | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        cancellationSignal.cancel();
    }
}