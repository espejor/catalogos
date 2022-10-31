package com.example.catalogos.google_drive_access.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.catalogos.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;

public class GoogleLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 400;

    TextView nameView;
    SignInButton signInButton;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInAccount account;

    private Button signOutButton;
    private Button backupButton;
    private Button restoreButton;
    private ImageView mAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
//        String code = getString(R.string.server_client_id);
        googleSignInOptions = new GoogleSignInOptions
                .Builder (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail ()
                .requestProfile ()
                .requestScopes (new Scope (DriveScopes.DRIVE_APPDATA))
                .build ();

        setContentView (R.layout.activity_google_login);

        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the dimensions of the sign-in button.
        mAvatar = findViewById(R.id.avatar);
        signInButton = findViewById(R.id.sign_in_button);
        signOutButton = findViewById(R.id.sign_out_button);
        backupButton = findViewById(R.id.backup_button);
        restoreButton = findViewById(R.id.restore_button);
        nameView = (TextView) findViewById(R.id.name);

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view){
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        requestSignIn ();
                        break;
                }

            }
        });
        signOutButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view){
                if (googleSignInOptions != null) {
                    GoogleSignInClient client =GoogleSignIn.getClient (GoogleLoginActivity.this,googleSignInOptions);
                    client.signOut ();
                    account = null;
                    updateUI ();
                }
            }
        });

        backupButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view){
                try {
                    new BackupAppData (GoogleLoginActivity.this,account).uploadAppData ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        });

        restoreButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view){
                new BackupAppData (GoogleLoginActivity.this,account).downloadAppData();
            }
        });
    }


    @Override
    protected void onStart(){
        super.onStart ();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI();
    }

    private void updateUI(){
        if(account != null){
            // Está logeado
            nameView.setText ("Hola " + account.getDisplayName ());
            Glide.with(this).load(account.getPhotoUrl ())
                .circleCrop().into(mAvatar);
            signInButton.setVisibility (View.INVISIBLE);
            signOutButton.setVisibility (View.VISIBLE);
            backupButton.setVisibility (View.VISIBLE);
            restoreButton.setVisibility (View.VISIBLE);
        }else{
            // No Está logeado
            Glide.with(this).load(R.drawable.ic_account_circle).into(mAvatar);
            signInButton.setVisibility (View.VISIBLE);
            signOutButton.setVisibility (View.INVISIBLE);
            backupButton.setVisibility (View.INVISIBLE);
            restoreButton.setVisibility (View.INVISIBLE);
            nameView.setText ("");
        }
    }

    private void requestSignIn(){
        GoogleSignInClient client = GoogleSignIn.getClient (this,googleSignInOptions);
        startActivityForResult (client.getSignInIntent (),RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data){
        super.onActivityResult (requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error de auntenticación", "signInResult:failed code=" + e.getStatusCode());
            account = null;
            updateUI();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}