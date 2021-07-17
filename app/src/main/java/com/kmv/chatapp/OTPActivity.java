package com.kmv.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kmv.chatapp.databinding.ActivityOTPBinding;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class OTPActivity extends AppCompatActivity {
    ActivityOTPBinding otpBinding;
    FirebaseAuth auth;
    String backendOtp;
 //   ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpBinding = ActivityOTPBinding.inflate(getLayoutInflater());
        setContentView(otpBinding.getRoot());
        otpBinding.otpView.requestFocus();

        String phoneNumber = getIntent().getStringExtra("PhoneNumber");
        String countryCode = getIntent().getStringExtra("c-code");
        otpBinding.phoneLabel.setText("Verify "+countryCode + phoneNumber);
        backendOtp = getIntent().getStringExtra("backendOtp");
        getSupportActionBar().hide();
//        progressDialog=new ProgressDialog(this);
//        progressDialog.setMessage("Sending OTP...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        progressDialog.dismiss();
        otpBinding.continueBtn.setOnClickListener(v -> {
            if (otpBinding.otpView.getText().toString().trim().length() == 6) {
                String otpCode = otpBinding.otpView.getText().toString();
                if (backendOtp != null) {
                    otpBinding.continueBtn.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            backendOtp, otpCode
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(task -> {
                                otpBinding.continueBtn.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
//                                            progressDialog.dismiss();
                                    Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(OTPActivity.this, "Please Enter the correct OTP", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(OTPActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {

            }
        });
        otpBinding.resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CountDownTimer(60000,1000){
                    @Override
                    public void onTick(long l) {
                        otpBinding.resendOtpButton.setText(""+l/1000);
                        otpBinding.resendOtpButton.setEnabled(false);
                    }

                    @Override
                    public void onFinish() {
                        otpBinding.resendOtpButton.setText(" Resend");
                        otpBinding.resendOtpButton.setEnabled(true);
                    }
                }.start();

                Toast.makeText(OTPActivity.this,"Resend OTP successfully",Toast.LENGTH_SHORT).show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + phoneNumber,//otpBinding.phoneBox.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        OTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                progressDialog.dismiss();
                                backendOtp=newOtp;

                                Toast.makeText(OTPActivity.this,"Resend OTP Successfully", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
    }
}