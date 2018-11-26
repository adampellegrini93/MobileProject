package android.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class createAccount extends AppCompatActivity {

    private static final int LOGIN = 0;
    private ImageButton uploadImage;
    TextView login;
    private Button createAccountButton;
    private EditText getName, getEmail, getPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private StorageReference imageStorage;
    private DatabaseReference databaseReference;
    private Uri filepath;
    private Uri uri;
    private File pictureFile;
    private Bitmap bitmap;
    private boolean uploadedPhoto;
    String picturePath ="";
    String p="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        uploadedPhoto = false;

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        imageStorage = FirebaseStorage.getInstance().getReference();

        //allows user to click "sign in" text and bring back to main page
        login = (TextView)findViewById(R.id.backtoLogin);

        final String backLogin = login.getText().toString();
        SpannableString txt2 = new SpannableString(backLogin);
        ClickableSpan clickTxt2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {

            }
        };
        txt2.setSpan(clickTxt2,19,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(txt2);
        login.setMovementMethod(LinkMovementMethod.getInstance());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loginIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(loginIntent, LOGIN);
            }
        });

        //allows user to upload photo from phone gallery
        uploadImage = (ImageButton)findViewById(R.id.userUploadImage);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the user has not uploaded a photo yet
                if(uploadedPhoto == false){
                    selectImage();
                }
                //if the user already has a photo uploaded
                else{
                    photoOptions();
                }
            }
        });

        //for when users create a new account
        progressDialog = new ProgressDialog(this);
        getName = findViewById(R.id.getName);
        getEmail = findViewById(R.id.getEmail);
        getPassword = findViewById(R.id.getPassword);
        createAccountButton = findViewById(R.id.createAccountButton);

        getPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    createAccount();
                    return true;
                }
                return false;
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    //handles creating and registering the new account with the database
    private void createAccount(){
        final String inputName = getName.getText().toString();
        final String inputEmail = getEmail.getText().toString();
        final String inputPassword = getPassword.getText().toString();
        if(inputName.equals("") || inputEmail.equals("") || inputPassword.equals("")){
            Toast.makeText(getApplicationContext(),"blank field",Toast.LENGTH_LONG).show();
        }
        else{
            progressDialog.setMessage("Registering please wait...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(inputEmail,inputPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.hide();
                                FirebaseUser user = auth.getCurrentUser();
                                UserInformation userInformation = new UserInformation(inputName,inputEmail,inputPassword);
                                databaseReference.child("users").child(user.getUid()).setValue(userInformation);
                                if(uploadedPhoto){ //if user uploaded a photo
                                    if(filepath != null){
                                        StorageReference photoLocation = imageStorage.child("Images/Profile Photos/" + user.getUid());
                                        photoLocation.putFile(filepath);
                                    }
                                    else if(uri != null) {
                                        StorageReference photoLocation = imageStorage.child("Images/Profile Photos/" + user.getUid());
                                        photoLocation.putFile(uri);

                                    }
                                }
                                startActivity(new Intent(createAccount.this, HomePage.class));
                                finish();
                            }
                        }
                    });
        }
    }

    //creates pop up box with photo upload options
    protected void selectImage(){
        final CharSequence[] options = {"Take Photo","Choose from Gallery","Cancel"};
        AlertDialog.Builder addPhoto = new AlertDialog.Builder(createAccount.this);
        addPhoto.setTitle("Add Photo:");
        addPhoto.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Take Photo")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                    intent.setType("image/*");
                }
                else if(options[which].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,2);
                }
                else if(options[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        addPhoto.show();
    }

    //gives user options if they already uploaded a photo and select it again
    private void photoOptions(){
        final CharSequence[] options = {"View","Change","Cancel"};
        AlertDialog.Builder photo = new AlertDialog.Builder(createAccount.this);
        photo.setTitle("Photo Options");
        photo.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("View")){
                    displayImage();
                }
                else if(options[which].equals("Change")) {
                    selectImage();
                }
                else if(options[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        photo.show();
    }

    //for displaying image in center of screen
    private void displayImage(){

    }

    //functionality for taking/uploading a new photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //if wanting to take a new picture
            if(requestCode == 1){
                bitmap = (Bitmap) data.getExtras().get("data");
                uri = getUri(getApplicationContext(),bitmap);
                uploadImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,uploadImage.getWidth(),uploadImage.getHeight(),false));

                File file = new File(getPath(uri));

            }
            //if wanting to upload image from phone storage
            else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
                bitmap = (BitmapFactory.decodeFile(picturePath));
                uploadImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,uploadImage.getWidth(),uploadImage.getHeight(),false));
                filepath = data.getData();
            }
            uploadedPhoto = true;
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
}
