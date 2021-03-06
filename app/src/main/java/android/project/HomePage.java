package android.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.android.rides.RideRequestButton;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private ImageButton profilePhoto;
    private Toolbar myTool;
    private CardView cardView,cardView2;
    private MaterialCalendarView calendar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference storageRef;
    private DatabaseReference myRef;
    private final String TAG = "Value recovered is ";
    private TextView testDisplay;
    private TextView welcomeText;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Shake shake;
    private Toast t;
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

        welcomeText = findViewById(R.id.welcomeText);
        myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("name");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG,  value);
                welcomeText.setText("Welcome " + value + "!!!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //pulling saved profile photo and displaying it on homepage
        profilePhoto = (ImageButton) findViewById(R.id.homepagePhoto);
        profilePhoto.setVisibility(View.INVISIBLE);
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoLocation = storageRef.child("Images/Profile Photos/" + user.getUid() + "");
        try {
            final File localFile = File.createTempFile("images", "jpg");
            photoLocation.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Bitmap myBitmap = Bitmap.createScaledBitmap(bitmap, profilePhoto.getWidth(), profilePhoto.getHeight(), true);
                    profilePhoto.setImageBitmap(myBitmap);
                    profilePhoto.setVisibility(View.VISIBLE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //handles shaking the phone
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


        cardView.setOnClickListener(this);
        cardView2.setOnClickListener(this);


        myTool = findViewById(R.id.mytoolbar);
        setSupportActionBar(myTool);

        //handles the calendar
        calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendar.addDecorator(new CurrentDateDecorator(this));
        final NewContactDateDecorator decorator = new NewContactDateDecorator(this);
        calendar.addDecorator(decorator);
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                if(decorator.isDateImportant(calendarDay)){
                    String Date = calendarDay.toString().substring(12,calendarDay.toString().length()-1);
                    int month = Integer.parseInt(Date.substring(5,7)) + 1;
                    String displayDate = Date.substring(0,5) + month + Date.substring(7,Date.length());
                    if(t != null){
                        t.cancel();
                    }
                    t = Toast.makeText(getBaseContext(), "Met "+ decorator.getNames(calendarDay)+" on: " + displayDate, Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        //pulling amount of contacts added in current month and displays in TextView
        testDisplay = findViewById(R.id.testTextView);
        int contactCount = decorator.contactsAdded(CalendarDay.today());
        switch(contactCount){
            case 0:
                testDisplay.setText("You haven't added any new contacts this month yet.");
                break;
            case 1:
                testDisplay.setText("You've added " + contactCount + " new contact this month!!");
                break;
            default:
                testDisplay.setText("You've added " + contactCount + " new contacts this month!!");
                break;
        }
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
                    auth.signOut();
                    startActivity(new Intent(HomePage.this, MainActivity.class));
                    finish();
                }
            });
            signOut.setNegativeButton("No",null);
            signOut.setCancelable(true);
            signOut.create().show();
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
                auth.signOut();
                startActivity(new Intent(HomePage.this, MainActivity.class));
                finish();
            }
        });
        signOut.setNegativeButton("No",null);
        signOut.setCancelable(true);
        signOut.create().show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(shake, accelerometer, SensorManager.SENSOR_DELAY_UI);

    }
    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(shake);

    }

}






