package android.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import java.util.List;

public class Contact_ListView extends AppCompatActivity {

    private Handler handler;
    private List<ContactInformation> contacts;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__list_view);

        final Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        AppBarLayout layout = findViewById(R.id.app_bar);
        layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean display = false;
            int scroll = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scroll == -1){
                    scroll = appBarLayout.getTotalScrollRange();
                }
                if(scroll + verticalOffset == 0){
                    display = true;
                }else if (display){
                    display = false;
                }
            }
        });

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

        //Handles single click on ListView items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(Contact_ListView.this,EditContact.class);
                myIntent.putExtra("id", contacts.get(position).getIdentifier());
                myIntent.putExtra("name", contacts.get(position).getName());
                myIntent.putExtra("number", contacts.get(position).getNumber());
                myIntent.putExtra("image", contacts.get(position).getImage());
                myIntent.putExtra("image2",contacts.get(position).getImage2());
                myIntent.putExtra("geo", contacts.get(position).getLocation());
                startActivity(myIntent);

            }
        });

        //Handles long click on ListView items
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Contact_ListView.this);
                builder.setTitle("Contact options");
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Contact_ListView.this,EditInputContact.class);
                        myIntent.putExtra("id", contacts.get(position).getIdentifier());
                        myIntent.putExtra("name", contacts.get(position).getName());
                        myIntent.putExtra("number", contacts.get(position).getNumber());
                        myIntent.putExtra("image", contacts.get(position).getImage());
                        myIntent.putExtra("image2",contacts.get(position).getImage2());
                        myIntent.putExtra("geo", contacts.get(position).getLocation());
                        startActivity(myIntent);
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(handler.delete(contacts.get(position).getIdentifier())){
                            loadList();
                        }
                    }
                });
                builder.setNeutralButton("Cancel", null);
                builder.show();
                return true;
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
        getMenuInflater().inflate(R.menu.menu_contactlist,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem items) {
        int iden = items.getItemId();

        if(iden == R.id.action_home){
                Intent myIntent = new Intent(Contact_ListView.this, HomePage.class);
                startActivity(myIntent);
                finish();

        }
        return super.onOptionsItemSelected(items);
    }

    //handles user pressing back button
    @Override
    public void onBackPressed(){
        startActivity(new Intent(Contact_ListView.this, HomePage.class));
        finish();
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

