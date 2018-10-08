package android.project;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.LoginFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class createAccount extends AppCompatActivity {

    private static final int LOGIN = 0;
    private ImageButton uploadImage;
    TextView login;
    private Button createAccountButton;
    private EditText getName, getEmail, getPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
                selectImage();
            }
        });

        //for when users create a new account
        progressDialog = new ProgressDialog(this);
        getName = findViewById(R.id.getName);
        getEmail = findViewById(R.id.getEmail);
        getPassword = findViewById(R.id.getPassword);
        createAccountButton = findViewById(R.id.createAccountButton);
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

            UserInformation userInformation = new UserInformation(inputName,inputEmail,inputPassword);
            databaseReference.child("users").child(inputName).setValue(userInformation)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.hide();
                                Toast.makeText(getApplicationContext(),"account created",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

/*
            auth.createUserWithEmailAndPassword(inputEmail,inputPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.hide();
                                UserInformation userInformation = new UserInformation(inputName,inputEmail,inputPassword);
                                FirebaseUser user = auth.getCurrentUser();
                                databaseReference.child("users").child(user.getUid()).setValue(userInformation);
                                startActivity(new Intent(createAccount.this, HomePage.class));
                                finish();
                            }
                        }
                    });
    */
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
                    activeTakePhoto();
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

    //checks if permission to use phones camera and storage is already allowed
    private void activeTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
        } else {
            takePicture();
        }
    }

    //launches functionality for taking/uploading a new photo
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    //asks user if app can access the phones camera and storage if not allowed
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 110) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        }}

    //functionality for taking/uploading a new photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 1){ //if wanting to take a new picture
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                uploadImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,uploadImage.getWidth(),uploadImage.getHeight(),false));

            } else if (requestCode == 2) { //if wanting to upload image from phone storage
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                uploadImage.setImageBitmap(Bitmap.createScaledBitmap(thumbnail,uploadImage.getWidth(),uploadImage.getHeight(),false));
            }
        }
    }

}
