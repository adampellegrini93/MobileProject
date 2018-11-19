package android.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class Contact_ListView extends AppCompatActivity {

    private Handler handler;
    private List<ContactInformation> contacts;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__list_view);

        handler = new Handler(getApplicationContext());

        Button add_new_contact = findViewById(R.id.add_new_contact);
        add_new_contact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Contact_ListView.this, AddContact.class);
                startActivity(myIntent);
                finish();
            }
        });

        listView = findViewById(R.id.list_of_contacts);

        loadList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(Contact_ListView.this,EditContact.class);
                myIntent.putExtra("id", contacts.get(position).getIdentifier());
                myIntent.putExtra("name", contacts.get(position).getName());
                myIntent.putExtra("number", contacts.get(position).getNumber());
                myIntent.putExtra("image", contacts.get(position).getImage());
                startActivity(myIntent);

            }
        });

    }

    private void loadList(){
        contacts = handler.read();

        Adapter adapter = new Adapter(this,contacts);

        listView.setAdapter(adapter);

        for(ContactInformation c: contacts){
            String r = "ID=" + c.getIdentifier() + "| Name=" + c.getName() + " |" +c.getNumber();

            Log.d("Record", r);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 0, "HomePage");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem items) {
        int iden = items.getItemId();

        switch (iden) {
            case 1:
                Intent myIntent = new Intent(Contact_ListView.this, HomePage.class);
                startActivity(myIntent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(items);
    }

    //handles user pressing back button
    @Override
    public void onBackPressed(){
        startActivity(new Intent(Contact_ListView.this, HomePage.class));
        finish();
    }
}

