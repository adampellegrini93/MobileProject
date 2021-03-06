package android.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
                myIntent.putExtra("date",contacts.get(position).getDate());
                startActivity(myIntent);

            }
        });

        //Handles long click on ListView items
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                final String[] options = {"Call", "Edit", "Delete", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Contact_ListView.this);
                builder.setTitle("Contact options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].matches("Call")){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+contacts.get(position).getNumber()));
                            startActivity(intent);
                        }
                        else if(options[which].matches("Edit")){
                            Intent myIntent = new Intent(Contact_ListView.this,EditInputContact.class);
                            myIntent.putExtra("id", contacts.get(position).getIdentifier());
                            myIntent.putExtra("name", contacts.get(position).getName());
                            myIntent.putExtra("number", contacts.get(position).getNumber());
                            myIntent.putExtra("image", contacts.get(position).getImage());
                            myIntent.putExtra("image2",contacts.get(position).getImage2());
                            myIntent.putExtra("geo", contacts.get(position).getLocation());
                            startActivity(myIntent);
                        }
                        else if(options[which].matches("Delete")){
                            if(handler.delete(contacts.get(position).getIdentifier())){
                                deleteContact(getApplicationContext(),contacts.get(position).getNumber(),contacts.get(position).getName());
                                loadList();
                            }
                        }
                        else if(options[which].matches("Cancel")){

                        }
                    }
                });
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

    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
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

