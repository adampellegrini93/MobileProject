package android.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.LoginFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class createAccount extends AppCompatActivity {

    private static final int LOGIN = 0;

    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        login = (TextView)findViewById(R.id.backtoLogin);

        final String backLogin = login.getText().toString();
        SpannableString txt2 = new SpannableString(backLogin);
        ClickableSpan clickTxt2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {

            }
        };
        txt2.setSpan(clickTxt2,19,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(txt2);
        login.setMovementMethod(LinkMovementMethod.getInstance());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loginIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(loginIntent, LOGIN);
            }
        });
    }

}
