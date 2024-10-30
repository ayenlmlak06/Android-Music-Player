package com.example.finallistener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.finallistener.Model.AlbumUploads;
import com.example.finallistener.Model.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateAlbumActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_AlbumImg;
    private Button btn_UploadAlbum;
    private EditText et_NameAlbum;
    private ImageView iv_AlbumCover;
    String songsCategory;
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePaths;
    StorageReference storageReference;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        btn_AlbumImg = findViewById(R.id.btn_AlbumImg);
        btn_UploadAlbum = findViewById(R.id.btn_UploadAlbum);
        et_NameAlbum = findViewById(R.id.et_NameAlbum);
        iv_AlbumCover = findViewById(R.id.iv_AlbumCover);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        Spinner sp_CategoryAlbum = findViewById(R.id.sp_CategoryAlbum);

        btn_AlbumImg.setOnClickListener(this);
        btn_UploadAlbum.setOnClickListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("Love Song");
        categories.add("Sad Song");
        categories.add("USUK Song");
        categories.add("Ballad Song");
        categories.add("EDM Song");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_CategoryAlbum.setAdapter(dataAdapter);

        sp_CategoryAlbum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                songsCategory = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(CreateAlbumActivity.this, "Selected :" +songsCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == btn_AlbumImg){
            showFileImg();
        }
        else if(view == btn_UploadAlbum)
            uploadFile();
    }

    private void uploadFile() {
        if(filePaths != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("uploading...");
            progressDialog.show();
            final StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePaths));

            sRef.putFile(filePaths).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String urls = uri.toString();
                            AlbumUploads albumUploads = new AlbumUploads(et_NameAlbum.getText().toString().trim(), urls, songsCategory);
                            String uploadID = mDatabase.push().getKey();
                            mDatabase.child(uploadID).setValue(albumUploads);
                            progressDialog.dismiss();
                            Toast.makeText(CreateAlbumActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateAlbumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setMessage("uploaded" + ((int)progress)+"%....");
                }
            });
        }
    }

    private void showFileImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePaths = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePaths);
                iv_AlbumCover.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getMimeTypeFromExtension(cr.getType(uri));
    }
}