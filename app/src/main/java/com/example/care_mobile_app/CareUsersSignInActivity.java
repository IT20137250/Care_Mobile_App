package com.example.care_mobile_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CareUsersSignInActivity extends AppCompatActivity {

    TextView createnewcuser;

    //initialize variables
    EditText inputEmail,inputPaasword;
    Button btncuserlogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;


    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_users_sign_in);


        createnewcuser = findViewById(R.id.createnewcuser);

        inputEmail=findViewById(R.id.inputEmail);
        inputPaasword=findViewById(R.id.inputPaasword);
        btncuserlogin=findViewById(R.id.btncuserlogin);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        createnewcuser.setOnClickListener(view -> startActivity(new Intent(CareUsersSignInActivity.this,CareUsersSignUpActivity.class)));

        btncuserlogin.setOnClickListener(view -> perforLogin());


    }

    private void perforLogin() {

        String email = inputEmail.getText().toString();
        String password = inputPaasword.getText().toString();


        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter Correct Email");
        } else if (password.isEmpty() || password.length() < 6) {
            inputPaasword.setError("Enter Proper Password");
        } else {
            progressDialog.setMessage("Please Wait  While Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    String uid = task.getResult().getUser().getUid();

                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("CDuser").child(uid).child("udType").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int udType = snapshot.getValue(Integer.class);
                            if(udType == 0){
                                Intent intent =  new Intent(CareUsersSignInActivity.this,CareUserDocViewlRecyclerActivity.class);
                                startActivity(intent);
                            }
                            if(udType == 1){
                                Intent intent =  new Intent(CareUsersSignInActivity.this,CareAdminDocViewlRecyclerActivity.class);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                      //sendUserToNextActivity();
                    Toast.makeText(CareUsersSignInActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CareUsersSignInActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        }

    }
