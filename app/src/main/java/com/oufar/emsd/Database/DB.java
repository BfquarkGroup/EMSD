package com.oufar.emsd.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME = "Client.db";
    private static final String DB_TABLE = "Client_Table";

    // Columns
    private static final String ID = "ID";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String NAME = "NAME";
    private static final String PHONE = "PHONE";
    private static final String ADDRESS = "ADDRESS";
    private static final String IMAGE_URL = "IMAGE_URL";
    private static final String LAT = "LAT";
    private static final String LNG = "LNG";
    private static final String STORE_ID = "STORE_ID";
    private static final String STORE_NAME = "STORE_NAME";

    // Command
    private static final String CREATE_TABLE = "CREATE TABLE "+ DB_TABLE+" ("+
            ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+  // 0
            CLIENT_ID+ " TEXT, "+  // 1
            NAME+ " TEXT, "+  // 2
            PHONE+ " TEXT, "+  // 3
            ADDRESS+ " TEXT, "+  // 4
            IMAGE_URL+ " TEXT, "+  // 5
            LAT+ " TEXT, "+  // 6
            LNG+ " TEXT, "+  // 7
            STORE_ID+ " TEXT, "+  // 8
            STORE_NAME+ " TEXT "+")";  // 9

    public DB(Context context) {
        super(context, DB_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE);

        onCreate(db);
    }

    // A method to insert data
    public boolean insertData(String clientId, String name, String phone,
                              String address, String imageURL,
                              String lat, String lng,
                              String storeId, String storeName){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CLIENT_ID, clientId);
        contentValues.put(NAME, name);
        contentValues.put(PHONE, phone);
        contentValues.put(ADDRESS, address);
        contentValues.put(IMAGE_URL, imageURL);
        contentValues.put(LAT, lat);
        contentValues.put(LNG, lng);
        contentValues.put(STORE_ID, storeId);
        contentValues.put(STORE_NAME, storeName);

        long result = db.insert(DB_TABLE, null, contentValues);

        if (result == -1){

            return false;
        }else {

            return true;
        }

        // return result != -1; // if result = -1 data doesn't inserted
    }

    // A method to delete data
    public void deleteData(String id, String clientId, String name, String phone,
                           String address, String imageURL,
                           String lat, String lng,
                           String storeId, String storeName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE,ID + " = ?",new String[]{id});
        db.delete(DB_TABLE,CLIENT_ID + " = ?",new String[]{clientId});
        db.delete(DB_TABLE,NAME + " = ?",new String[]{name});
        db.delete(DB_TABLE,PHONE + " = ?",new String[]{phone});
        db.delete(DB_TABLE,ADDRESS + " = ?",new String[]{address});
        db.delete(DB_TABLE,IMAGE_URL + " = ?",new String[]{imageURL});
        db.delete(DB_TABLE,LAT + " = ?",new String[]{lat});
        db.delete(DB_TABLE,LNG + " = ?",new String[]{lng});
        db.delete(DB_TABLE,STORE_ID + " = ?",new String[]{storeId});
        db.delete(DB_TABLE,STORE_NAME + " = ?",new String[]{storeName});
        db.close();
    }

    // A method to view data
    public Cursor viewData(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "Select * from "+ DB_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        return cursor;
    }
}
