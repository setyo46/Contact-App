package contactapp.example.ContactApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class AddEditContact extends AppCompatActivity {

    private ImageView profileIv;
    private EditText nameEt,phoneEt,emailEt,noteEt;
    private FloatingActionButton fab;

    //String variable;
    private String id,name, phone,email,note,addedTime,updatedTime,image;
    private Boolean isEditMode;

    //action bar
    private ActionBar actionBar;

    //permission constant
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;

    // string array of permission
    private String[] cameraPermission;
    private String[] storagePermission;

    //Image uri var
    private Uri imageUri;

    //database helper
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        //init db
        dbHelper = new DbHelper(this);

        //init permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init actionBar
        actionBar = getSupportActionBar();

        //back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        //init view
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        noteEt = findViewById(R.id.noteEt);
        fab = findViewById(R.id.fab);

        // get intent data
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        if (isEditMode){
            //set toolbar title
            actionBar.setTitle("Update Contact");

            //mendapatkan nilai lain dari intent
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            phone = intent.getStringExtra("PHONE");
            email = intent.getStringExtra("EMAIL");
            note = intent.getStringExtra("NOTE");
            addedTime = intent.getStringExtra("ADDED_TIME");
            updatedTime = intent.getStringExtra("UPDATE_TIME");
            image = intent.getStringExtra("IMAGE");

            //set value in editText field
            nameEt.setText(name);
            phoneEt.setText(phone);
            emailEt.setText(email);
            noteEt.setText(note);

            imageUri = Uri.parse(image);

            if (image.equals("null")){
                profileIv.setImageResource(R.drawable.ic_baseline_person_24);
            }else {
                profileIv.setImageURI(imageUri);
            }

        }else {
            // add mode on
            actionBar.setTitle("Add Contact");
        }

        // add even handler
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();
            }
        });
    }

    private void showImagePickerDialog() {

        //option for dialog
        String options[] = {"Camera","Gallery"};

        // Alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setTitle
        builder.setTitle("Choose An Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // handle item click
                if (which == 0){ //start from 0 index
                    //camera selected
                    if (!checkCameraPermission()){
                        //meminta izin kamera
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }

                }else if (which == 1){
                    //Gallery selected
                    if (!checkStoragePermission()){
                        //meminta izin penyimpanan
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        }).create().show();
    }

    private void pickFromGallery() {
        // Intent untuk mengambil gambar dari galeri
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*"); // only image

        startActivityForResult(galleryIntent,IMAGE_FROM_GALLERY_CODE);
    }

    private void pickFromCamera() {
        // ContentValues untuk info gambar
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION,"IMAGE_DETAIL");

        //save imageUri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent untuk membuka camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

        startActivityForResult(cameraIntent,IMAGE_FROM_CAMERA_CODE);
    }

    private void saveData(){

        //data yang diberikan dalam variabel
        name = nameEt.getText().toString();
        phone = phoneEt.getText().toString();
        email = emailEt.getText().toString();
        note = noteEt.getText().toString();

        //waktu saat ini untuk disimpan sebagai waktu tambahan
        String timeStamp = ""+System.currentTimeMillis();

        // check filed data
        if (!name.isEmpty() || !phone.isEmpty() || !email.isEmpty() || !note.isEmpty()){
            //menyimpan data, jika pengguna hanya memiliki satu data

            //cek edit atau tambahkan untuk menyimpan data di sql
            if (isEditMode){
                //edit mode
                dbHelper.updateContact(
                        ""+id,
                        ""+imageUri,
                        ""+name,
                        ""+phone,
                        ""+email,
                        ""+note,
                        ""+addedTime,
                        ""+timeStamp // waktu yang diperbarui akan waktu baru
                );

                // Untuk memeriksa pembaruan data berhasil, menampilkan pesan toast
                Toast.makeText(getApplicationContext(), "Updated Successfully...."+id, Toast.LENGTH_SHORT).show();

            } else {
                // add mode
                long id = dbHelper.insertContact(
                        ""+imageUri,
                        ""+name,
                        ""+phone,
                        ""+email,
                        ""+note,
                        ""+timeStamp,
                        ""+timeStamp

                );

                // Untuk memeriksa insert data berhasil, menampikan pesan toast
                Toast.makeText(getApplicationContext(), "Successfully...."+id, Toast.LENGTH_SHORT).show();
            }

        }else {
            // menampilkan pesan toast
            Toast.makeText(getApplicationContext(), "Nothing to save....", Toast.LENGTH_SHORT).show();
        }

    }

    //back button click
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //cperiksa izin kamera
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result & result1;
    }

    // permintaan izin kamera
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_PERMISSION_CODE); // handle request permission on override method
    }

    // periksa izin penyimpanan
    private boolean checkStoragePermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result1;
    }

    //permintaan izin penyimpanan
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_PERMISSION_CODE);
    }


    //meminta izin handle
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length >0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted){
                        //kedua izin diberikan
                        pickFromCamera();
                    }else {
                        //izin tidak diberikan
                        Toast.makeText(getApplicationContext(),"Camera & Storage Permission needed..",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_PERMISSION_CODE:
                if (grantResults.length >0){

                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    // jika semua izin diizinkan, kembalikan benar, jika tidak salah
                    if (storageAccepted){
                        //izin diberikan
                        pickFromGallery();
                    }else {
                        //izin tidak diberikan
                        Toast.makeText(getApplicationContext(),"Storage Permission needed..",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_FROM_GALLERY_CODE){
                // mengambil gambar dari kamera
                // crop gambar
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddEditContact.this);

            }else if (requestCode == IMAGE_FROM_CAMERA_CODE){
                // mengambil gambar dari kamera
                // memotong gambar
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddEditContact.this);
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                //gambar yang dipotong diterima
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();
                profileIv.setImageURI(imageUri);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //untuk error handling
                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
