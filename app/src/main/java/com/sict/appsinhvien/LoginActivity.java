package com.sict.appsinhvien;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private Button btnSigout;
    private int SIGN_GOOLE_CODE = 1;

    private EditText emailAuth, passwordAuth;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = (SignInButton) findViewById(R.id.buttonSignIn);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton();
            }
        });
        anhXa();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emailAuth.getText().toString().equals("") && !passwordAuth.getText().toString().equals("")){
                    dangNhap();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void dangNhap() {
        mAuth.signInWithEmailAndPassword(emailAuth.getText().toString(), passwordAuth.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("Thất bại", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Kiểm tra lại email",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void anhXa(){
        emailAuth = (EditText)findViewById(R.id.editTextEmail);
        passwordAuth = (EditText)findViewById(R.id.editTextPassword);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
    }
    private void signInButton() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, SIGN_GOOLE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_GOOLE_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //xác thực đăng nhập firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Tài khoản google:" + account.getId());
                Toast.makeText(this, "Chức năng hiện đang bảo trì", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                Log.w(TAG, "Đăng nhập thất bại", e);
            }
//            checkLogin(task);
        }
    }



    private void checkLogin(Task<GoogleSignInAccount> completeTask) {
        try {
            GoogleSignInAccount acc = completeTask.getResult(ApiException.class);
            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            GoogleSignInAccount user = getDataUser();
            String name = user.getDisplayName();
            String img = user.getPhotoUrl().toString();
//            intent.putExtra("userName", name);
//            intent.putExtra("userImage", img);
            startActivity(intent);
            Log.d("Login", user.getDisplayName());
//            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "Kiểm tra lại tài khoản", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    getDataUser();
                } else {
                    Toast.makeText(LoginActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private GoogleSignInAccount getDataUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            String userName = account.getDisplayName();
            String userGivenName = account.getGivenName();
            String userFamilyName = account.getFamilyName();
            String userEmail = account.getEmail();
            Uri userPhoto = account.getPhotoUrl();
            Log.d("Link ảnh :" ,userPhoto.toString());
            Toast.makeText(this, userPhoto.toString(), Toast.LENGTH_SHORT).show();
        }
        return account;
    }
}
