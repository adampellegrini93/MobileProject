package android.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


public class AddContact extends AppCompatActivity{

    private Handler handler;
    private ImageButton uploadImage;
    private String picturePath="";
    private String name;
    private String number;
    private String image;
    private final String TAG = "testing location->";
    private Location currentLocation;

    private MapView gmap;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        gmap = findViewById(R.id.mapView);

        gmap.onCreate(savedInstanceState);
        gmap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

            }
        });


        handler = new Handler(getApplicationContext());

        //setup for up pulling current gps location
        currentLocation = null;
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //constantly pulls and updates the users location and sets it to currentLocation
                currentLocation = location;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //checking to make sure user gave permission to use phones location
        //required even though permission is checked on main page
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    1);
        }
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider,0,0,locationListener);


        //handles upload image button click
        uploadImage = (ImageButton) findViewById(R.id.uploadContactImage);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        //handles create contact button click
        Button createContactButton = (Button) findViewById(R.id.createContactButton);
        createContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //testing pulling and printing current location
                locationManager.removeUpdates(locationListener);
                if(currentLocation != null){
                    Double latitude = currentLocation.getLatitude();
                    Double longitude = currentLocation.getLongitude();
                    Log.i(TAG,"latitude: "+latitude.toString()+" Longitude: "+longitude.toString());
                }

                EditText getContactName = (EditText) findViewById(R.id.getContactName);
                name = getContactName.getText().toString();
                EditText getContactNumber = (EditText) findViewById(R.id.getContactNumber);
                number = getContactNumber.getText().toString();
                image = picturePath;

                ContactInformation contactInformation = new ContactInformation();
                contactInformation.setName(name);
                contactInformation.setNumber(number);
                contactInformation.setImage(image);

                Boolean added = handler.addContact(contactInformation);
                if (added) {
                    Intent myIntent = new Intent(AddContact.this, Contact_ListView.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't Create Contact", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //creates pop up box with photo upload options
    protected void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder addPhoto = new AlertDialog.Builder(AddContact.this);
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
                uploadImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, uploadImage.getWidth(), uploadImage.getHeight(), false));


            } else if (requestCode == 2) { //if wanting to upload image from phone storage
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                uploadImage.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, uploadImage.getWidth(), uploadImage.getHeight(), false));

            }
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
                Intent myIntent = new Intent(AddContact.this, HomePage.class);
                startActivity(myIntent);
                break;
        }
        return super.onOptionsItemSelected(items);
    }

    //Handles back button functionality
    @Override
    public void onBackPressed() {
    startActivity(new Intent(AddContact.this, HomePage.class));
    finish();
    }
}




