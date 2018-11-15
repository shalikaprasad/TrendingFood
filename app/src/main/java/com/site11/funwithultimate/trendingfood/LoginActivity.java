package com.site11.funwithultimate.trendingfood;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.site11.funwithultimate.trendingfood.Home.Consumers.Consumer_Home;
import com.site11.funwithultimate.trendingfood.Home.Farmers.Farmers_Home;
import com.site11.funwithultimate.trendingfood.Home.Retailers.Retails_Home;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_SIGN_IN = 9999;
    GoogleApiClient googleClient;
    SignInButton signInButton;
    FirebaseAuth firebaseAuth;

    //Create Dialog
    AlertDialog waitingDialog;
    RelativeLayout rellay1, rellay2;
    Spinner category;
    int category_no = 0;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PERMISSION_SIGN_IN)
        {
            waitingDialog.dismiss();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){

                GoogleSignInAccount account = result.getSignInAccount();
                String idToken = account.getIdToken();

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
                firebaseAuthWithGoogle(credential);


            }else {
                waitingDialog.dismiss();

                Log.e("EDMT_ERROR","Login failed");
                Log.e("EDMT_ERROR",result.getStatus().getStatusMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {

            firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(LoginActivity.this,Farmers_Home.class);
                            intent.putExtra("email",authResult.getUser().getEmail());
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);
        category = (Spinner) findViewById(R.id.category);
        signInButton = (SignInButton) findViewById(R.id.google_sign) ;
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash


        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(LoginActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(provinceAdapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    category_no = 0;
                } else if (i == 2) {
                    category_no = 1;
                } else if (i == 3) {
                    category_no = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //GoogleSign In
        configureGoogleSignIn();

        firebaseAuth = FirebaseAuth.getInstance();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        waitingDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .build();


    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleClient);
        startActivityForResult(intent,PERMISSION_SIGN_IN);
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        googleClient.connect();
    }

    public void signupbtn(View view) {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    public void forgotbtn(View view) {
        Intent i = new Intent(LoginActivity.this, ForgotActivity.class);
        startActivity(i);
    }

    public void loginbtn(View view) {
        if (category_no == 0) {
            Intent i = new Intent(LoginActivity.this, Farmers_Home.class);
            startActivity(i);
        } else if (category_no == 1) {
            Intent i = new Intent(LoginActivity.this, Retails_Home.class);
            startActivity(i);
        } else if (category_no == 2) {
            Intent i = new Intent(LoginActivity.this, Consumer_Home.class);
            startActivity(i);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "" + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

    }
}