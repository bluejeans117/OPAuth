package com.oxygen.opauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.io.SuFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static android.text.TextUtils.isEmpty;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "MainActivity";

    View view;

    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth fb;
    public static FirebaseUser mUser;

    private EditText mEmail, mName, mID;
    private ProgressBar mProgressBar;

    public String number = "";

    static {
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = getWindow().getDecorView();

        mEmail = findViewById(R.id.email);
        mName = findViewById(R.id.name);
        mID=  findViewById(R.id.tg);
        mProgressBar = findViewById(R.id.progressBar);

        File file = SuFile.open("/sys/devices/soc0/serial_number");
        try {
            Scanner scanner = new Scanner(file);
            number = scanner.nextLine();
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "You so veri ghey", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "UwU", Toast.LENGTH_SHORT).show();
        }

        setupFirebaseAuth();
        signIn();
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void signIn(){
        //check if the fields are filled out
        Log.d(TAG, "onClick: attempting to authenticate.");
        hideKeyboard(MainActivity.this);
        showDialog();

        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            mUser = FirebaseAuth.getInstance().getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
        hideDialog();
    }

    private void sendDetails() {
        if(!isEmpty(mEmail.getText().toString())
                && !isEmpty(mName.getText().toString())
                && !isEmpty(mID.getText().toString())) {
            DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
            String email = mEmail.getText().toString();
            String name = mName.getText().toString();
            String id = mID.getText().toString();

            mDb.child("users").child(name).child("email").setValue(email);
            mDb.child("users").child(name).child("tg_id").setValue(id);
            mDb.child("users").child(name).child("number").setValue(number);

            Toast.makeText(MainActivity.this, "Your details have been submitted. Thank you!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(MainActivity.this, "You didn't fill in your email.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                fb = firebaseAuth;
                mUser = user;

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.email_sign_in_button) {
            sendDetails();
        }
    }
}
