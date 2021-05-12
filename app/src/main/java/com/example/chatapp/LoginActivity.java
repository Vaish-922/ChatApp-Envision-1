package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText code;
    private Button verify;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

       userIsLoggedIn();

        phoneNumber = findViewById(R.id.phoneNUmber);
        code=findViewById(R.id.code);
        verify=findViewById(R.id.verify);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mVerificationId != null){
                    verifyPhoneNumberWithCode();
                }
                else
                    startPhoneNumberVerification();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
               // System.out.println("Fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                mVerificationId = verificationId;
                verify.setText("Verify code");

            }
        };
    }

    private void verifyPhoneNumberWithCode(){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                   String userId = user.getUid();
                    System.out.println(userId);
                   if(user!=null){
                     try {
                         final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
                         //final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatapp-c1496-default-rtdb.firebaseio.com/");
                         //final DatabaseReference child1 = mUserDB.child("user");

                         //System.out.println("Hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
                         //System.out.println(mUserDB);


                         mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 if (!dataSnapshot.exists()) {
                                     //System.out.println("****************************************************************************");
                                     Map<String, Object> userMap = new HashMap<>();
                                     userMap.put("phone", user.getPhoneNumber());
                                     userMap.put("name", user.getPhoneNumber());
                                     mUserDB.updateChildren(userMap);
                                 }

                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {
                             }
                         });
                     }
                     catch (Exception e) {
                         System.out.println(e);
                     }
                     userIsLoggedIn();

                   }
                }

            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){
            startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
             finish();
             return;
        }
    }

    private void startPhoneNumberVerification() {


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber.getText().toString())
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );*/
    }
}