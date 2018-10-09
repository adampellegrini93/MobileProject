package android.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private Toolbar myTool;
    private CardView cardView,cardView2,cardView3;
    private FirebaseAuth auth;
    private FirebaseUser user;

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

        cardView=findViewById(R.id.addContactButton);
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
            signOut.setMessage("Areyou sure you want to sign out?");
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
        signOut.setMessage("Areyou sure you want to sign out?");
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

}






