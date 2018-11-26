package android.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AddContact extends AppCompatActivity{

    private Handler handler;
    private ImageButton uploadImage;
    private String picturePath="";
    private String p="";
    private String name;
    private String number;
    private String image;
    private String geo;
    String image2;
    private final String TAG = "testing location->";
    SharedPreferences locat;
    SharedPreferences.Editor editor;
    String destination;
    boolean locationPermission;
    private LocationManager locationManager;
    Location location = null;
    double lon, lat;
    Double latitude, longitude;


    public static final int MIN_D = 10;
    public static final int MIN_T = 5000;


    private MapView gmap;
    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        gmap = findViewById(R.id.mapView);

        gmap.onCreate(savedInstanceState);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locat = getSharedPreferences("Location", MODE_PRIVATE);
        editor = locat.edit();

        String horizontal = locat.getString("Longitude", "");
        String vertical = locat.getString("Latitude", "");
        try {
            lon = Double.parseDouble(horizontal);
            lat = Double.parseDouble(vertical);
        }catch (NumberFormatException err){

        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);

        } catch (IOException err) {
            err.printStackTrace();
        }

        if (addressList != null && addressList.size() > 0) {
            destination = addressList.get(0).getLocality();
        }

        gmap.getMapAsync(new OnMapReadyCallback() {
                             @Override
                             public void onMapReady(GoogleMap googleMap) {
                                 gMap = googleMap;
                                 getLocation();
                                 gplay();

                             }
                         });

        handler = new Handler(getApplicationContext());


        //checking to make sure user gave permission to use phones location
        //required even though permission is checked on main page
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    1);
           locationPermission = true;


        }
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider,0,0,locationListener);


        //handles upload image button click
        uploadImage = findViewById(R.id.uploadContactImage);
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

                EditText getContactName = (EditText) findViewById(R.id.getContactName);
                name = getContactName.getText().toString();
                EditText getContactNumber = (EditText) findViewById(R.id.getContactNumber);
                number = getContactNumber.getText().toString();
                image = picturePath;
                image2 = p;
                geo = destination;

                if (name.equals("") || number.equals("")) {
                    android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(AddContact.this);
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("One or more Values not Entered");
                    alertDialog.setNegativeButton("Ok", null);
                    alertDialog.setCancelable(true);
                    alertDialog.create().show();
                } else {

                    ContactInformation contactInformation = new ContactInformation();
                    contactInformation.setName(name);
                    contactInformation.setNumber(number);
                    contactInformation.setImage(image);
                    contactInformation.setImage2(image2);
                    contactInformation.setLocation(geo);

                    Boolean added = handler.addContact(contactInformation);
                    if (added) {
                        addToPhoneContacts();
                        Intent myIntent = new Intent(AddContact.this, Contact_ListView.class);
                        startActivity(myIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn't Create Contact", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    //adds the created contact into the phone's contact book
    private void addToPhoneContacts(){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert display name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, number)
                .withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // Adding insert operation to operations list
        // to insert Babelaas note in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Note.NOTE, "Contact added with Babelaas application")
                .build());
        try{
            // Executing all the insert operations as a single database transaction
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            //Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
        }catch (RemoteException e) {
            e.printStackTrace();
        }catch (OperationApplicationException e) {
            e.printStackTrace();
        }
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
                uploadImage.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, uploadImage.getWidth(), uploadImage.getHeight(), false));

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



    //setup for up pulling current gps location
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //constantly pulls and updates the users location and sets it to currentLocation
            if(location != null){
                pin(location);
                locationManager.removeUpdates(locationListener);
            }else{

            }

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

    private void gplay(){
        //checking to make sure user gave permission to use phones location
        //required even though permission is checked on main page
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    1);
            locationPermission = true;


        }
        int gplay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(gplay != ConnectionResult.SUCCESS){
            GooglePlayServicesUtil.getErrorDialog(gplay, this, -1).show();
            finish();
        }else{
            if(gMap != null){
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
                gMap.getUiSettings().setAllGesturesEnabled(true);
            }
        }
    }

    private void getLocation() {


        //checking to make sure user gave permission to use phones location
        //required even though permission is checked on main page
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            locationPermission = true;

        }

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (network) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_T, MIN_D, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        if (gps) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_T, MIN_D, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            pin(location);

        }

        try{
            SharedPreferences locat = getApplication().getSharedPreferences("Location",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = locat.edit();
            editor.putString("Longitude", longitude + "");
            editor.putString("Latitude", latitude + "");
            editor.commit();
        }catch (Exception err){
            err.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Location Retrieved", Toast.LENGTH_LONG
        ).show();

    }

    private void pin(Location location){
        if(gMap != null){
            gMap.clear();
            LatLng drop = new LatLng(location.getLatitude(),location.getLongitude());
            gMap.addMarker(new MarkerOptions().position(drop));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(drop,15));
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

    @Override
    protected void onResume(){
        super.onResume();
        gmap.onResume();
        getLocation();
    }

    @Override
    protected void onPause(){
        super.onPause();
        gmap.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        gmap.onDestroy();
    }

}




