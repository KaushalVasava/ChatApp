package com.kmv.chatapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PhoneNumberActivity extends AppCompatActivity {
    EditText phoneBox;
    TextView phonelabel,appdesc;
    ImageView startImage;
    Button continueBtn;
    CountryCodePicker codePicker;
  //  ActivityPhoneNumberBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
   // BroadcastReceiver broadcastReceiver;
  String countryCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_phone_number);

        phoneBox = findViewById(R.id.phoneBox);
        phonelabel = findViewById(R.id.phonelabel);
        appdesc = findViewById(R.id.appdescription);
        startImage =findViewById(R.id.startImage);
        continueBtn = findViewById(R.id.continueBtn);
       // codePicker =findViewById(R.id.countryCode);

        codePicker = findViewById(R.id.countryCode);

        countryCode = codePicker.getSelectedCountryCodeWithPlus();
    //    Log.d("T",code);
        codePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                countryCode = codePicker.getSelectedCountryCodeWithPlus();
              // Log.d("T","selected :"+countryCode);
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("OTP sending...");
        progressDialog.setCancelable(false);

        phoneBox.requestFocus();
        //setContentView(binding.getRoot());
        getSupportActionBar().hide();
//        String countryCode="+91";

      //  String countryCode = codePicker.getSelectedCountryCode();
//        broadcastReceiver = new NetworkChangeReceiver();
//        registerNetworkBroadcastReceiver();
        Log.d("P","above");


        if(auth.getInstance().getCurrentUser()==null){
            Log.d("P","if");
//           // Intent intent=new Intent(PhoneNumberActivity.this,PhoneNumberActivity.class);
//            Log.d("P","name : ");
//
//            //startActivity(intent);
//           // finish();
        }
        else{
            Log.d("P","else");

            Intent intent=new Intent(PhoneNumberActivity.this, MainActivity.class);
            FirebaseUser user = auth.getInstance().getCurrentUser();
            Log.d("P","phone no : "+user.getPhoneNumber());
         //   Log.d("P","name : "+user.getPhotoUrl());
            startActivity(intent);
            finish();
        }
         continueBtn.setOnClickListener(v -> {
                if(!phoneBox.getText().toString().trim().isEmpty()) {
                    if (phoneBox.getText().toString().trim().length() == 10) {
                        progressDialog.show();
                        Log.d("T",countryCode);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                countryCode + phoneBox.getText().toString(),
                                60L,
                                TimeUnit.SECONDS,
                                PhoneNumberActivity.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        continueBtn.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        Toast.makeText(PhoneNumberActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String backedOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        continueBtn.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
                                        intent.putExtra("PhoneNumber",phoneBox.getText().toString());
                                        intent.putExtra("backendOtp",backedOtp);
                                        intent.putExtra("c-code",countryCode);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                        );
                    }
                    else{
                        Toast.makeText(PhoneNumberActivity.this,"Please enter correct number",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(PhoneNumberActivity.this,"Enter mobile number",Toast.LENGTH_LONG).show();
                }
            });
    }
//    protected void registerNetworkBroadcastReceiver(){
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
//          registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
//            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
//    }
//
//    protected void unregisterNetwork(){
//        try{
//            unregisterReceiver(broadcastReceiver);
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterNetwork();
//    }
}
