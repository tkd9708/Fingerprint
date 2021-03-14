package com.example.myproject2;

        import android.content.Context;
        import android.content.Intent;
        import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback;
        import android.os.Build;
        import android.util.Log;

        import static android.hardware.biometrics.BiometricPrompt.*;

public abstract class BioCore {
    private static final String TAG = "BioCore";

    private static final String AUTHENTICATION_FAIL_MSG = "지문 인식에 실패하였습니다.";
    public static final int AUTHENTICATION_FAIL_CODE = -1;
    private BiometricPromptCallback biometricPromptCallback;

    public void init(final Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            biometricPromptCallback = BiometricPromptCallback.getInstance(context);

            biometricPromptCallback.initialize(context, new AuthenticationCallback(){
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Log.d(TAG, "Error Code : " + errorCode + ", MSG : " + errString);
                    onFail(errorCode, errString.toString());
                }
                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    Log.d(TAG, "Help Code : " + helpCode + ", MSG : " + helpString);
                    onFail(helpCode, helpString.toString());
                }
                @Override
                public void onAuthenticationSucceeded(AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    onSuccess(result.getCryptoObject());
                }
                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Log.d(TAG, "Failed");
                    onFail(AUTHENTICATION_FAIL_CODE, AUTHENTICATION_FAIL_MSG);
                }
            });
        }

    }

    public void cancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d(TAG, "Core cancel");
            if (biometricPromptCallback != null) {
                biometricPromptCallback.cancel();
                biometricPromptCallback = null;
            }
        }
    }

    public abstract void onSuccess(CryptoObject cryptoObject);

    public abstract void onFail(int code, String msg);
}
