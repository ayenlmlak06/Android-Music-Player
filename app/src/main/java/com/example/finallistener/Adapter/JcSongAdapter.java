package com.example.finallistener.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finallistener.Model.GetSong;
import com.example.finallistener.Model.Utility;
import com.example.finallistener.R;

import java.util.List;

public class JcSongAdapter extends RecyclerView.Adapter<JcSongAdapter.SongAdapterViewHolder> {
    private int selectedPosition;
    Context context;
    List<GetSong> arraylistSongs;
    private RecyclerItemClickListener listener;

    public JcSongAdapter(Context context, List<GetSong> arraylistSongs, RecyclerItemClickListener listener) {
        this.context = context;
        this.arraylistSongs = arraylistSongs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.songs_row,parent,false);
        return new SongAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapterViewHolder holder, int position) {
        GetSong getSong = arraylistSongs.get(position);
        if(getSong != null){
            if(selectedPosition == position){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.light_green));
                holder.iv_PlaySong.setVisibility(View.VISIBLE);

            }else{
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent));
                holder.iv_PlaySong.setVisibility(View.VISIBLE);
            }
        }

        holder.tv_Title.setText(getSong.getSongTitle());
        holder.tv_Artist.setText(getSong.getArtist());
        String duration = Utility.convertDuration(Long.parseLong(getSong.getSongDuration()));
        holder.tv_Duration.setText(duration);

        holder.bind(getSong,listener);
    }

    @Override
    public int getItemCount() {
        return arraylistSongs.size();
    }

    public class SongAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_Title, tv_Artist,tv_Duration;
        ImageView iv_PlaySong;
        public SongAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Title = itemView.findViewById(R.id.tv_TitleSong);
            tv_Artist = itemView.findViewById(R.id.tv_ArtistSong);
            tv_Duration = itemView.findViewById(R.id.tv_DurationSong);
            iv_PlaySong = itemView.findViewById(R.id.iv_PlaySong);

        }

        public void bind(GetSong getSong, RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(getSong, getAdapterPosition());
                }
            });
        }
    }
    public interface RecyclerItemClickListener {
        void onClickListener (GetSong song, int postion);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
