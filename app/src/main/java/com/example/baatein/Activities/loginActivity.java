package com.example.baatein.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.baatein.Dialog.SplashScreen;
import com.example.baatein.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import com.example.baatein.Dialog.LoadingDialog1;
import com.example.baatein.Dialog.LoadingDialog2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class loginActivity extends AppCompatActivity {
private FloatingActionButton sendcode;

CountryCodePicker ccp;
String countryCode;
boolean countryCodeSelected =false;

    boolean codeSended=false;


private EditText number,code;
private String num;
     Boolean userexist=false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks Callbacks;
    String verifyId;
     LoadingDialog1 loadingDialog1;
     LoadingDialog2 loadingDialog2;

     int a=0;

    private SharedPreferences preferences;
    private  String FirstTime;
    SplashScreen splashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        splashScreen=new SplashScreen(loginActivity.this);
        splashScreen.startAlertDialog();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseApp.initializeApp(this);

        check();


        ccp=findViewById(R.id.ccp);
        sendcode=findViewById(R.id.sencodeButton);
        number=findViewById(R.id.numberTextView);
        code=findViewById(R.id.codeTextView);
        TextView textView=findViewById(R.id.welcomeTextView);

        String mystring=new String("Welcome!");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        textView.setText(content);
        loadingDialog1=new LoadingDialog1(loginActivity.this);
        loadingDialog2=new LoadingDialog2(loginActivity.this);


        Callbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingDialog1.dismissDialog();
                e.printStackTrace();
                Toast.makeText(loginActivity.this, "Something Went Wrong,please check number", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verifyid, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verifyid, forceResendingToken);

                verifyId=verifyid;
                loadingDialog1.dismissDialog();
                loadingDialog2.startAlertDialog();
                a=1;
                code.setVisibility(View.VISIBLE);
                getAnimation();

                codeSended=true;

            }
        };

    }

    private void getAnimation() {
        LinearLayout linearLayout=findViewById(R.id.phoneNumberLayout);
        YoYo.with(Techniques.FlipOutY)
                .duration(1000)
                .repeat(0)
                .playOn(linearLayout);

        code.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FlipInX)
                .duration(1000)
                .repeat(0)
                .playOn(code);

    }

    private void check() {

        preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        FirstTime = preferences.getString("FirstTimeInstall", "");

        Boolean  isFirstRun  = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", false);

        if (isFirstRun) {
            splashScreen.dismissDialog();
            startActivity(new Intent(loginActivity.this, MainActivity.class));
            finish();
        }else if(FirstTime.equals("Yes")){
               userislogin();

            }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("FirstTimeInstall","Yes");
                    editor.apply();
                    userislogin();


                }
            }
        });
    }

    private void userislogin(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        alreadyRegister();
        if(user!=null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!userexist) {
                        gotoRegister();
                    }else{
                        Intent intent=new Intent(loginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putBoolean("isFirstRun", true).commit();
                        startActivity(intent);
                        finish();
                        return;

                    }
                }
            }, 5000);

        }
        else{
            splashScreen.dismissDialog();
        }

    }

    private void alreadyRegister() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String number=user.getPhoneNumber();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");
        Query queries=reference.orderByChild("number").equalTo(number);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    userexist=true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        queries.addListenerForSingleValueEvent(eventListener);





    }
    private void gotoRegister1(){

        Intent intent=new Intent(loginActivity.this,registerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return;
    }

    private void gotoRegister(){

            if(a==1){
                loadingDialog2.dismissDialog();
            }
            gotoRegister1();


    }




    private void getCorrect() {
        num = num.replaceAll("\\s", "");
        num = num.replaceAll("\\(", "");
        num = num.replaceAll("\\)", "");
        num = num.replaceAll("\\-", "");
        countryCode = ccp.getSelectedCountryCodeWithPlus();
        num=countryCode+num;

    }

    public void sendcode(View view) {
        if(!codeSended){

        num=number.getText().toString();
        getCorrect();


           if(verifyId!=null) {


               loadingDialog1.startAlertDialog();
               verifyPhoneNumberWithCode();

               startPhoneNumberVerification();


           }else {
               loadingDialog1.startAlertDialog();
               startPhoneNumberVerification();
           }}else{
            verifyPhoneNumberWithCode();
        }



    }



    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(num,60,
                TimeUnit.SECONDS,
                this,
                Callbacks);
    }

    private void verifyPhoneNumberWithCode() {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verifyId,code.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private Boolean checknumber() {
        if(num.length()!=10){
            return  false;
        }else{
            return true;
        }


    }



    public void goback(View view) {
        startActivity(new Intent(loginActivity.this,loginActivity.class));
    }
}