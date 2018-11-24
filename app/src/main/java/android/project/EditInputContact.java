package android.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditInputContact extends AppCompatActivity {

    private Handler handler;
    private String picturePath="";
    ImageButton uploadContactImage;

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
        uploadContactImage = findViewById(R.id.uploadContactImage);
        uploadContactImage.setImageBitmap(BitmapFactory.decodeFile(extras.getString("image")));

        uploadContactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button updateButton=findViewById(R.id.createContactButton);
        updateButton.setText("Update Contact Info");
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getContactName.length() == 0|| getContactName.length()== 0){

                    android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(EditInputContact.this);
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("One or more Values not Entered");
                    alertDialog.setNegativeButton("Ok", null);
                    alertDialog.setCancelable(true);
                    alertDialog.create().show();
                } else {

                    ContactInformation contactInformation = new ContactInformation();
                    contactInformation.setIdentifier(extras.getInt("id"));
                    contactInformation.setName(getContactName.getText().toString());
                    contactInformation.setNumber(getContactNumber.getText().toString());
                    contactInformation.setImage(picturePath);

                    boolean update = handler.edit(contactInformation);
                    if (update) {
                        Intent myIntent = new Intent(EditInputContact.this, Contact_ListView.class);
                        startActivity(myIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }



    //creates pop up box with photo upload options
    protected void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder addPhoto = new AlertDialog.Builder(EditInputContact.this);
        addPhoto.setTitle("Add Photo:");
        addPhoto.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[which].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        addPhoto.show();
    }

    //functionality for taking/uploading a new photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { //if wanting to take a new picture
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                uploadContactImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, uploadContactImage.getWidth(), uploadContactImage.getHeight(), false));


            } else if (requestCode == 2) { //if wanting to upload image from phone storage
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                uploadContactImage.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, uploadContactImage.getWidth(), uploadContactImage.getHeight(), false));

            }
        }
    }
}
