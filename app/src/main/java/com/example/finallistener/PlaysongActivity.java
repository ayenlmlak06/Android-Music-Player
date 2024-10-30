package com.example.finallistener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.finallistener.Adapter.JcSongAdapter;
import com.example.finallistener.Model.GetSong;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlaysongActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Boolean checkin = false;
    List<GetSong> mupload;
    JcSongAdapter adapter;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    JcPlayerView jcPlayerView;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private  int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        recyclerView = findViewById(R.id.rv_Playsong);
        progressBar = findViewById(R.id.pb_Playsong);
        jcPlayerView = findViewById(R.id.jc_Player);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mupload = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        adapter = new JcSongAdapter(getApplicationContext(), mupload, new JcSongAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(GetSong song, int postion) {

                changeSelectedSong(postion);

                jcPlayerView.playAudio(jcAudios.get(postion));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mupload.clear();
                for(DataSnapshot dss: snapshot.getChildren()){
                    GetSong getSong = dss.getValue((GetSong.class));
                    getSong.setMkey(dss.getKey());
                    final String s= getIntent().getExtras().getString("songsCategory");
                    if(s.equals(getSong.getSongsCategory())){
                        mupload.add(getSong);
                        checkin = true;
                        jcAudios.add(JcAudio.createFromURL(getSong.getSongTitle(),getSong.getSongLink()));
                    }
                }
                adapter.setSelectedPosition(0);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if(checkin){
                    jcPlayerView.initPlaylist(jcAudios, null);
                }else{
                    Toast.makeText(PlaysongActivity.this, "There is no anysong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void changeSelectedSong(int index){
        adapter.notifyItemChanged(adapter.getSelectedPosition());
        currentIndex = index;
        adapter.setSelectedPosition(currentIndex);
        adapter.notifyItemChanged(currentIndex);
    }
}