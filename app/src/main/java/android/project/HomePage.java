package android.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;



public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private Toolbar myTool;
    private CardView cardView,cardView2,cardView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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
        public boolean onCreateOptionsMenu (Menu menu){
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

}






