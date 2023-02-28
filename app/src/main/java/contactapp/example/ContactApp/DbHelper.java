package contactapp.example.ContactApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

// class untuk database helper
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // membuat table di database
        db.execSQL(Constants.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // tingkatkan tabel jika ada perubahan struktur di db

        // drop table jika ada
        db.execSQL("DROP TABLE IF EXISTS "+Constants.TABLE_NAME);


        onCreate(db);
    }

    // Sisipkan Fungsi untuk memasukkan data ke dalam basis data
    public long insertContact(String image, String name, String phone, String email, String note, String addedTime, String updateTime){

        // dapatkan db yang dapat ditulisi untuk menulis data di db
        SQLiteDatabase db = this.getWritableDatabase();

        // buat objek kelas Nilai Konten untuk menyimpan data
        ContentValues contentValues = new ContentValues();

        // id akan disimpan secara otomatis saat menulis query
        contentValues.put(Constants.C_IMAGE,image);
        contentValues.put(Constants.C_NAME,name);
        contentValues.put(Constants.C_PHONE,phone);
        contentValues.put(Constants.C_EMAIL,email);
        contentValues.put(Constants.C_NOTE,note);
        contentValues.put(Constants.C_ADDED_TIME,addedTime);
        contentValues.put(Constants.C_UPDATE_TIME,updateTime);

        // masukkan data dalam baris, Ini akan mengembalikan id catatan
        long id = db.insert(Constants.TABLE_NAME, null,contentValues);

        // close db
        db.close();


        return id;

    }

    // Perbarui Fungsi untuk memperbarui data di database
    public void updateContact(String id,String image, String name, String phone, String email, String note, String addedTime, String updateTime){

        //  database yang dapat ditulis untuk menulis data db
        SQLiteDatabase db = this.getWritableDatabase();

        // membuat ContentValue class object untuk menyimpan data
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constants.C_IMAGE,image);
        contentValues.put(Constants.C_NAME,name);
        contentValues.put(Constants.C_PHONE,phone);
        contentValues.put(Constants.C_EMAIL,email);
        contentValues.put(Constants.C_NOTE,note);
        contentValues.put(Constants.C_ADDED_TIME,addedTime);
        contentValues.put(Constants.C_UPDATE_TIME,updateTime);

        // uperbarui data dalam baris, Ini akan mengembalikan id
        db.update(Constants.TABLE_NAME,contentValues,Constants.C_ID+" =? ",new String[]{id});

        // close db
        db.close();

    }

    // hapis data by id
    public void deleteContact(String id){
        //get writable database
        SQLiteDatabase db  = getWritableDatabase();

        //delete query
        db.delete(Constants.TABLE_NAME,Constants.C_ID+" =? ",new String[]{id});

        db.close();
    }

    //menghapus semua data
    public void deleteAllContact(){
        //get writable database
        SQLiteDatabase db = getWritableDatabase();

        //query untuk menghapus
        db.execSQL("DELETE FROM "+Constants.TABLE_NAME);
        db.close();
    }

    // get data
    public ArrayList<ModelContact> getAllData(){
        //create arrayList
        ArrayList<ModelContact> arrayList = new ArrayList<>();
        //sql command query
        String selectQuery = "SELECT * FROM "+Constants.TABLE_NAME;

        //get read table db
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // mengulang semua record dan menambahkan ke list
        if (cursor.moveToFirst()){
            do {
                ModelContact modelContact = new ModelContact(

                        ""+cursor.getInt(cursor.getColumnIndexOrThrow(Constants.C_ID)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_UPDATE_TIME))
                );
                arrayList.add(modelContact);

            }while (cursor.moveToNext());
        }

        db.close();
        return arrayList;

    }

    // search data in sql Database
    public ArrayList<ModelContact> getSearchContact(String query){

        // akan mengembalikan list array dari class modelContact
        ArrayList<ModelContact> contactList = new ArrayList<>();


        SQLiteDatabase db = getReadableDatabase();

        //query untuk pencarian
        String queryToSearch = "SELECT * FROM "+Constants.TABLE_NAME+" WHERE "+Constants.C_NAME + " LIKE '%" +query+"%'";

        Cursor cursor = db.rawQuery(queryToSearch,null);

        // mengulang semua record dan menambahkan ke list
        if (cursor.moveToFirst()){
            do {
                ModelContact modelContact = new ModelContact(

                        ""+cursor.getInt(cursor.getColumnIndexOrThrow(Constants.C_ID)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_UPDATE_TIME))
                );
                contactList.add(modelContact);

            }while (cursor.moveToNext());
        }

        db.close();
        return contactList;

    }

}
