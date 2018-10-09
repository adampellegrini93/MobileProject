package android.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private static final int SIGN_UP = 0;
    private FirebaseAuth auth;
    private ProgressDialog loading;
    private EditText inputUserName, inputPassword;


    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets firebase database authentication instance
        auth = FirebaseAuth.getInstance();

        //pulling text fields
        inputUserName = findViewById(R.id.userName);
        inputPassword = findViewById(R.id.userPassword);

        //prevents keyboard from automatically opening up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //handles sign-up features
        signUp = (TextView)findViewById(R.id.signUp);
        final String createAccount = signUp.getText().toString();
        SpannableString txt = new SpannableString(createAccount);
        ClickableSpan clickTxt = new ClickableSpan() {
            @Override
            public void onClick(View view) {

            }
        };

        txt.setSpan(clickTxt,31,37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setText(txt);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(getApplicationContext(),createAccount.class);
                startActivityForResult(signUpIntent, SIGN_UP);
            }
        });

        //handles login button
        Button loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();;
            }
        });

        inputPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    loginUser();
                    return true;
                }
                return false;
            }
        });
    }

    //handles login and  user verification
    public void loginUser(){
        //if user leaves login field empty
        if(inputUserName.getText().toString().matches("") || inputPassword.getText().toString().matches("")){
            Toast.makeText(getApplicationContext(),"blank login field",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loading = new ProgressDialog(MainActivity.this,
                    R.style.progressTheme);
            loading.setIndeterminate(true);
            loading.setMessage("Validating...");
            loading.show();
            String userName = inputUserName.getText().toString();
            String passWord = inputPassword.getText().toString();
            auth.signInWithEmailAndPassword(userName, passWord)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loading.cancel();
                            if (!task.isSuccessful()) {
                                //displays invalid login notification
                                AlertDialog.Builder invalidLogin = new AlertDialog.Builder(MainActivity.this);
                                invalidLogin.setTitle("Error:");
                                invalidLogin.setMessage("Invalid Username/Password");
                                invalidLogin.setPositiveButton("Ok", null);
                                invalidLogin.setCancelable(true);
                                invalidLogin.create().show();
                            } else {
                                startActivity(new Intent(MainActivity.this, HomePage.class));
                                finish();
                            }
                        }
                    });
        }
    }

    //Asks user if they want to exit the app if they push back button on main page
    @Override
    public void onBackPressed(){
        AlertDialog.Builder closingApp = new AlertDialog.Builder(MainActivity.this);
        closingApp.setTitle("Closing Application");
        closingApp.setMessage("Are you sure you want to exit?");
        closingApp.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //closes application when yes is clicked
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        closingApp.setNegativeButton("No",null);
        closingApp.setCancelable(true);
        closingApp.create().show();
    }


}
