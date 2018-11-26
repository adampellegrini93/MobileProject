package android.project;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class EditInputContact extends AppCompatActivity {

    private Handler handler;
    private String picturePath="";
    private String p="";
    ImageButton uploadContactImage;
    private final int REQ = 100;
    private final int NUM = 101;
    EditText getContactName;
    EditText getContactNumber;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        extras = getIntent().getExtras();

        handler = new Handler(getApplicationContext());

        final ImageView nameMic = findViewById(R.id.nameMic);
        nameMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech();
            }
        });

        final ImageView numberMic = findViewById(R.id.numberMic);
        numberMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechNumber();
            }
        });



        getContactName = findViewById(R.id.getContactName);
        getContactName.setText(extras.getString("name"));
        getContactNumber = findViewById(R.id.getContactNumber);
        getContactNumber.setText(extras.getString("number"));
        uploadContactImage = findViewById(R.id.uploadContactImage);
        uploadContactImage.setImageBitmap(BitmapFactory.decodeFile(extras.getString("image")));
        uploadContactImage.setImageBitmap(BitmapFactory.decodeFile(extras.getString("image2")));

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
                    contactInformation.setImage2(p);

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

                Uri uri = getUri(getApplicationContext(),bitmap);

                File file = new File(getPath(uri));

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

        switch(requestCode){
            case REQ:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> arrayList = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String voiceInput = arrayList.get(0);
                    getContactName.setText(voiceInput);
                }
                break;
            }
            case NUM:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> arrayList = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String voiceInput = arrayList.get(0);
                    getContactNumber.setText(voiceInput);
                }
                break;
            }
        }
    }

    public Uri getUri(Context context, Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        String p = MediaStore.Images.Media.insertImage(context.getContentResolver(),image,"Title",null);
        return Uri.parse(p);
    }

    public String getPath(Uri uri) {
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                p = cursor.getString(index);
                cursor.close();
            }
        }
        return p;
    }

    //handles speech to text for both name and number
    private void speech(){
        Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        try{
            startActivityForResult(intent1, REQ);
        }catch (ActivityNotFoundException err){

        }

    }
    private void speechNumber(){
        Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        try{
            startActivityForResult(intent2, NUM);
        }catch (ActivityNotFoundException err){

        }

    }

}
