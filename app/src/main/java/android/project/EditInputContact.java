package android.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditInputContact extends AppCompatActivity {

    private Handler handler;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        extras = getIntent().getExtras();

        handler = new Handler(getApplicationContext());


        final EditText getContactName = findViewById(R.id.getContactName);
        getContactName.setText(extras.getString("name"));
        final EditText getContactNumber = findViewById(R.id.getContactNumber);
        getContactNumber.setText(extras.getString("number"));

        Button updateButton=findViewById(R.id.createContactButton);
        updateButton.setText("Update Contact Info");
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactInformation contactInformation = new ContactInformation();
                contactInformation.setIdentifier(extras.getInt("id"));
                contactInformation.setName(getContactName.getText().toString());
                contactInformation.setNumber(getContactNumber.getText().toString());

                boolean update = handler.edit(contactInformation);
                if(update){
                    Intent myIntent = new Intent(EditInputContact.this,Contact_ListView.class);
                    startActivity(myIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Try Again",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
