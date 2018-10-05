package android.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private Toolbar myTool;
    private CardView cardView,cardView2,cardView3;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference ref;
    String uid;
    List<String> itemList;

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

        /*
        //testing reading database data
        final TextView display = findViewById(R.id.textViewTest);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        itemList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                String user_name = dataSnapshot.child("users").child(uid).child("name").getValue(String.class);
                display.setText(user_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */

        cardView=findViewById(R.id.addContact);
        cardView2=findViewById(R.id.contactList);
        cardView3=findViewById(R.id.editProfile);

        cardView.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);


        myTool = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(myTool);
    }


    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()){
            case R.id.addContact: intent = new Intent(this,AddContact.class);
            startActivity(intent);
                break;
            case R.id.contactList: intent = new Intent(this,ContactList.class);
            startActivity(intent);
                break;

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

        if (id == R.id.action_settings) {
            startActivity(new Intent(HomePage.this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder signOut = new AlertDialog.Builder(HomePage.this);
        signOut.setTitle("Warning");
        signOut.setMessage("Do you want to sign out?");
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
    }

}






