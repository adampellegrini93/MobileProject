package android.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.android.rides.RideRequestButton;

public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private Toolbar myTool;
    private CardView cardView,cardView2,cardView3;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private final String TAG = "Value recovered is ";
    private TextView testDisplay;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Shake shake;
    RideRequestButton rideRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //checking if user is logged in
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            startActivity(new Intent(HomePage.this, MainActivity.class));
            finish();
        }

        //pulling the logged in user
        user = auth.getCurrentUser();

        //pulling database name that was stored and placing it in the welcome TextView
        testDisplay = findViewById(R.id.testTextView);
        if (user != null) {
            myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("name");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    Log.d(TAG,  value);
                    testDisplay.setText("Welcome " + value + "!!!");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        //creates ability for user to shake device to display a small ride request button (uber)
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shake = new Shake();
        shake.setListener(new Shake.OnShakeListener() {
            @Override
            public void Motion(int tally) {

                    Toast.makeText(getApplicationContext(), "Shake Detected", Toast.LENGTH_LONG
                    ).show();
                SessionConfiguration configuration = new SessionConfiguration.Builder()
                        .setClientId("VloB4uodKMZy9GsR7Pbuw2znE2wnkugp")
                        .setServerToken("7GksvSrca1X2h0sFTdETgBg5r7FwpKp6RZie8FHr")
                        .setRedirectUri("https://www.babelass.com/redirect_uri")
                        .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                        .build();

                UberSdk.initialize(configuration);
                Context context = getApplicationContext();
                rideRequestButton = new RideRequestButton(context);
                RelativeLayout relativeLayout = findViewById(R.id.homepage);
                relativeLayout.addView(rideRequestButton);
                rideRequestButton.setVisibility(View.VISIBLE);
            }
        });


        cardView=findViewById(R.id.addContactButton);
        cardView2=findViewById(R.id.contactList);
        cardView3=findViewById(R.id.editProfile);

        cardView.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);

        myTool = findViewById(R.id.mytoolbar);
        setSupportActionBar(myTool);
    }


    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()){
            case R.id.contactList: intent = new Intent(this,Contact_ListView.class);
            startActivity(intent);
                break;

            case R.id.addContactButton: intent = new Intent(this, AddContact.class);
            startActivity(intent);
                default:break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //option for user logging put with drop down menu
        if (id == R.id.action_settings) {
            //startActivity(new Intent(HomePage.this, MainActivity.class));
            AlertDialog.Builder signOut = new AlertDialog.Builder(HomePage.this);
            signOut.setTitle("Warning");
            signOut.setMessage("Are you sure you want to sign out?");
            signOut.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this is where we will handle sign out functionality
                    //right now it will just return user to main screen and close current page
                    startActivity(new Intent(HomePage.this, MainActivity.class));
                    finish();
                }
            });
            signOut.setNegativeButton("No",null);
            signOut.setCancelable(true);
            signOut.create().show();
            auth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder signOut = new AlertDialog.Builder(HomePage.this);
        signOut.setTitle("Warning");
        signOut.setMessage("Are you sure you want to sign out?");
        signOut.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //this is where we will handle sign out functionality
                //right now it will just return user to main screen and close current page
                startActivity(new Intent(HomePage.this, MainActivity.class));
                finish();
            }
        });
        signOut.setNegativeButton("No",null);
        signOut.setCancelable(true);
        signOut.create().show();
        auth.signOut();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(shake);

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






