package android.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_UP = 0;

    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //handles login and verification
        Button loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLoginInfo()){
                    startActivity(new Intent(MainActivity.this, HomePage.class));

                    final ProgressDialog loading = new ProgressDialog(MainActivity.this,
                            R.style.progressTheme);
                    loading.setIndeterminate(true);
                    loading.setMessage("Validating...");
                    loading.show();
                }else{
                    TextView invalidDisplay = findViewById(R.id.invalidLoginDisplay);
                    invalidDisplay.setText("Invalid Login");
                }


            }
        });

    }


    //temporarily checks login with hardcoded values until database is connected
    public boolean checkLoginInfo(){
        EditText inputUserName = findViewById(R.id.userName);
        EditText inputPassword = findViewById(R.id.userPassword);
        String userName = inputUserName.getText().toString();
        String passWord = inputPassword.getText().toString();
        if(userName.matches("username") && passWord.matches("password")){
            return true;
        }else{
            return false;
        }


    }


}
