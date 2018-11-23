package android.project;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Handler extends SQLiteOpenHelper {

    private static final String D_BASE = "ListContacts";

    private static final int DATABASE_V= 3;

    private static final String CONTACTS = "contacts";

    private static final String C_ID = "id";
    private static final String C_N = "name";
    private static final String C_Nu = "number";
    private static final String C_I ="image";
    private static final String C_L = "geo";

    private String[] columns ={C_ID,C_N,C_Nu,C_I,C_L};

    public Handler(Context context){
        super(context,D_BASE,null,DATABASE_V);
    }

    @Override
    public void onCreate(SQLiteDatabase d_base){
        String C_TABLE = "CREATE TABLE " + CONTACTS + "("
                + C_ID + " INTEGER PRIMARY KEY,"
                + C_N + " TEXT,"
                + C_Nu + " TEXT,"
                + C_I + " TEXT,"
                + C_L + " TEXT "
                + ")";
        d_base.execSQL(C_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase d_base, int oldVersion, int newVersion){
        d_base.execSQL("DROP TABLE IF EXISTS " + CONTACTS);
        onCreate(d_base);

    }

    public boolean addContact(ContactInformation contact){
        SQLiteDatabase d_base =this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_N, contact.getName());
        values.put(C_Nu, contact.getNumber());
        values.put(C_I,contact.getImage());
        values.put(C_L,contact.getLocation());

        long data = d_base.insert(CONTACTS,null,values);
        d_base.close();

        if(data != 0){
            return true;
        }else{
            return false;
        }
    }

    public List<ContactInformation>read(){
        SQLiteDatabase d_base = this.getWritableDatabase();

        List<ContactInformation> contacts = new ArrayList<>();

        Cursor crs = d_base.query(CONTACTS, columns, null ,null,null,null,null);

        crs.moveToFirst();

        while(!crs.isAfterLast()){
            ContactInformation contact = new ContactInformation();
            contact.setIdentifier(Integer.parseInt(crs.getString(0)));
            contact.setName(crs.getString(1));
            contact.setNumber(crs.getString(2));
            contact.setImage(crs.getString(3));
            contact.setLocation(crs.getString(4));
            contacts.add(contact);
            crs.moveToNext();
        }

        crs.close();
        return contacts;
    }

    public boolean edit(ContactInformation contact){
        SQLiteDatabase d_base = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_N, contact.getName());
        values.put(C_Nu, contact.getNumber());
        values.put(C_I,contact.getImage());
        values.put(C_L,contact.getLocation());

        int data = d_base.update(CONTACTS,values,C_ID +" = ? ",new String[]{String.valueOf(contact.getIdentifier())});

        d_base.close();

        if(data != 0 ){
            return true;
        }else{
            return false;
        }
    }

    public boolean delete(int id){
        SQLiteDatabase d_base = this.getWritableDatabase();

        int data = d_base.delete(CONTACTS, C_ID + " = ? ", new String[]{String.valueOf(id)});

        d_base.close();

        if (data != 0){
            return true;
        }else{
            return false;
        }
    }


}
