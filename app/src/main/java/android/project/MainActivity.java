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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_UP = 0;

    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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




    }
}
