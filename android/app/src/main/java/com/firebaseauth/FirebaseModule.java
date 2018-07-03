package com.firebaseauth;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirebaseModule  extends ReactContextBaseJavaModule {

    private static final String TAG = "ReactNativeJS";
    private static final String AppName = "bldm";

    private FirebaseAuth.AuthStateListener authStateListener;
    private ReactContext reactContext;

    public FirebaseModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
    }

    //<editor-fold desc="Auth State Listener ">

    @ReactMethod
    public void addAuthStateListener() {
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);

        if (authStateListener == null) {
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    WritableMap map = Arguments.createMap();
                    if (user != null) {
                        map.putMap("user", getUserMap(user));

                    }
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("onAuthState", map);
                }
            };

            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @ReactMethod
    public void removeAuthStateListener() {
        if (authStateListener != null) {
            FirebaseApp firebaseApp = FirebaseApp.getInstance();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Create User">
    @ReactMethod
    public void createUserWithEmailAndPassword(final String email, final String password, final Promise promise) {
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        WritableMap map = getUserMap(authResult.getUser());
                        promise.resolve(map);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        WritableMap error = getErrorMap(exception);
                        promise.reject(error.getString("code"), error.getString("message"), exception);
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Sign In ">
    @ReactMethod
    public void signInWithEmailAndPassword(final String email, final String password, final Promise promise) {
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    WritableMap map = getUserMap(authResult.getUser());
                    promise.resolve(map);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    WritableMap error = getErrorMap(exception);
                    promise.reject(error.getString("code"), error.getString("message"), exception);
                }
            });
    }
    //</editor-fold>

    @ReactMethod
    public void signOut(Promise promise) {
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);

        Log.d(TAG, "signOut");
        if (firebaseAuth == null || firebaseAuth.getCurrentUser() == null) {
            promise.reject("", "No User signed in");
        } else {
            firebaseAuth.signOut();
            promise.resolve(null);
        }
    }

    //<editor-fold desc="private methods">

    private WritableMap getErrorMap(Exception exception) {
        WritableMap map = Arguments.createMap();
        String code = "";
        String message = "";

        try {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            code = authException.getErrorCode();
            message = authException.getMessage();
        } catch (Exception e) {
            message = exception.getMessage();
        }
        map.putString("code", code);
        map.putString("message", message);

        return map;
    }

    private WritableMap getUserMap(FirebaseUser user) {
        //There are a lot of properties you can return. See:
        //https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser

        WritableMap map = Arguments.createMap();

        map.putString("email", user.getEmail());

        return map;
    }

    //</editor-fold>

    @Override
    public String getName() {
        return "FirebaseUtil";
    }
}
