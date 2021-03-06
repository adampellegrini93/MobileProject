package android.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();
        //if user never logged out go straight to homepage
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(SplashScreen.this, HomePage.class));
            finish();
        }
        else{
            //displays splash screen for 2.5 seconds upon app opening
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent splash = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(splash);
                    finish();
                }
            },SPLASH_TIME_OUT);
        }
    }


    @Override
    public void onBackPressed(){
        //prevents user from pushing back button during splash screen
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();

    }

    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
