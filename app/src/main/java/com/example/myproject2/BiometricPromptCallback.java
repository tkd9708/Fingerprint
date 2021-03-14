package com.example.myproject2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;


import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

@TargetApi(Build.VERSION_CODES.P)
public class BiometricPromptCallback implements CancellationSignal.OnCancelListener {

    private final Context context;
    BiometricPrompt biometricPrompt;
    CancellationSignal cancellationSignal;
    private static final String KEY_NAME = "mKey";
    Signature signature;
    AuthenticationListener authenticationListener;

    private static BiometricPromptCallback _instance;
    public static BiometricPromptCallback getInstance(Context context) {
        if (_instance == null) _instance = new BiometricPromptCallback(context);
        return _instance;
    }

    public BiometricPromptCallback(Context context) {
        this.context = context;
        biometricPrompt = new BiometricPrompt.Builder(context)
                .setDescription("Description")
                .setTitle("잠금 해제")
                .setNegativeButton("Cancel", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .build();
    }

    public void initialize(Context context, BiometricPrompt.AuthenticationCallback callback) {
        if(init()){

            cancellationSignal = new CancellationSignal();
            cancellationSignal.setOnCancelListener(this);
            biometricPrompt.authenticate(new BiometricPrompt.CryptoObject(signature),cancellationSignal, context.getMainExecutor(),callback);
        }else{
            authenticationListener.failed();
        }
    }

    // 암호화
    private KeyPairGenerator createkey(String keyName, boolean invalidatedByBiometricEnrollment) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);

        keyPairGenerator.initialize(builder.build());

        return keyPairGenerator;
    }


    public boolean init() {
        try {
            KeyPairGenerator keyPairGenerator = createkey(KEY_NAME, true);
            signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(keyPairGenerator.generateKeyPair().getPrivate());

            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void cancel(){
        cancellationSignal.cancel();
    }

    @Override
    public void onCancel() {

    }
}
