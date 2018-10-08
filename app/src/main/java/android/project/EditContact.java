package android.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EditContact extends AppCompatActivity {

    Bundle extras;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);


        extras = getIntent().getExtras();

        handler = new Handler(getApplicationContext());

        TextView contactNameView2 = findViewById(R.id.contactNameView2);
        contactNameView2.setText(extras.getString("name"));

        TextView contactNumberView2 = findViewById(R.id.contactNumberView2);
        contactNumberView2.setText(extras.getString("number"));

        Button contactBack = findViewById(R.id.contactBack);
        contactBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent myIntent = new Intent(EditContact.this,Contact_ListView.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(1, 1, 0, "Edit Contact");
        menu.add(1,2,1,"Delete Contact");
        menu.add(1,3,2,"HomePage");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem items){
        int iden = items.getItemId();

        switch (iden){
            case 1:
                edit();
                break;
            case 2:
                if(handler.delete(extras.getInt("id"))){
                    Intent myIntent = new Intent(EditContact.this,Contact_ListView.class);
                    startActivity(myIntent);
                }
                break;
            case 3:
                Intent myIntent = new Intent(EditContact.this,HomePage.class);
                startActivity(myIntent);
                break;
        }

        return super.onOptionsItemSelected(items);

    }

    public void edit(){
        Intent myIntent = new Intent(EditContact.this,EditInputContact.class);
        myIntent.putExtras(extras);
        startActivity(myIntent);
    }
}
