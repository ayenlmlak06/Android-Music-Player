package com.example.finallistener;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finallistener.Model.SongUploads;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView tv_SongSelected;
    ProgressBar pb_Progress;
    Uri audioUri;
    Spinner sp_Category;
    StorageReference mStorageref;
    StorageTask mUploads;
    DatabaseReference referenceSong;
    String songsCategory;
    String title1, artist1, albums_art1 = "", durations1;
    MediaMetadataRetriever metadataRetriever;
    byte [] img;
    TextView tv_TitleUp,tv_AlbumsUp, tv_TypeUP,  tv_DurationUp, tv_ArtistUP;
    ImageView imv_imgSongUp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        tv_SongSelected = findViewById(R.id.tv_SongSelected);
        pb_Progress = findViewById(R.id.pb_Progress);
        tv_TitleUp = findViewById(R.id.tv_TitleUp);
        tv_AlbumsUp = findViewById(R.id.tv_AlbumsUp);
        tv_TypeUP = findViewById(R.id.tv_TypeUp);
        tv_DurationUp = findViewById(R.id.tv_DurationUp);
        tv_ArtistUP = findViewById(R.id.tv_ArtistUp);
        imv_imgSongUp = findViewById(R.id.imv_imgSongUp);

        metadataRetriever = new MediaMetadataRetriever();
        referenceSong = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageref = FirebaseStorage.getInstance().getReference().child("songs");

        sp_Category = findViewById(R.id.sp_Category);
        sp_Category.setOnItemSelectedListener(this);

        List <String> categories = new ArrayList<>();
        categories.add("Love Song");
        categories.add("Sad Song");
        categories.add("USUK Song");
        categories.add("Ballad Song");
        categories.add("EDM Song");

        ArrayAdapter <String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Category.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        songsCategory = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected: "+songsCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void openAudioFiles (View v){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == 101 && resultCode == RESULT_OK && data.getData() != null){
        audioUri = data.getData();
        String fileName = getFileName(audioUri);
        tv_SongSelected.setText(fileName);

        metadataRetriever.setDataSource(this,audioUri);

        img= metadataRetriever.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0,img.length);
        imv_imgSongUp.setImageBitmap(bitmap);
        tv_TitleUp.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        tv_AlbumsUp.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        tv_ArtistUP.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        tv_TypeUP.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        tv_DurationUp.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        artist1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        title1= metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        durations1= metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }

    }


    @SuppressLint("Range")
    private String getFileName(Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if(cursor !=null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }

        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut!=-1){
                result=result.substring(cut+1);
            }
        }
        return result;
    }
    public void uploadFileToFirebase(View v){
        if(tv_SongSelected.equals("Select your song")){
            Toast.makeText(this, "please select a song", Toast.LENGTH_SHORT).show();
        }
        else{
            if(mUploads != null && mUploads.isInProgress()){
                Toast.makeText(this, "Song upload in already progress", Toast.LENGTH_SHORT).show();
            }
            else{
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        if(audioUri != null){
            Toast.makeText(this, "uploading please wait", Toast.LENGTH_SHORT).show();
            pb_Progress.setVisibility(View.VISIBLE);
            final StorageReference storageReference = mStorageref.child(System.currentTimeMillis()+"."+getfileExtension(audioUri));
            mUploads = storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            SongUploads songUploads = new SongUploads(songsCategory, title1, artist1, albums_art1, durations1, uri.toString());
                            String uploadId = referenceSong.push().getKey();
                            referenceSong.child(uploadId).setValue(songUploads);

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    pb_Progress.setProgress((int) progress);
                }
            });
        }
        else {
            Toast.makeText(this, "No file selected to upload", Toast.LENGTH_SHORT).show();
        }

    }
    private String getfileExtension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
    public void openCreateAlbum(View v){
        Intent in = new Intent(UploadActivity.this, CreateAlbumActivity.class);
        startActivity(in);
    }
}
