package com.example.finallistener.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finallistener.Model.Album;
import com.example.finallistener.PlaysongActivity;
import com.example.finallistener.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
private Context mContext;
private List<Album> albums;

    public ItemAdapter(Context mContext, List<Album> albums) {
        this.mContext = mContext;
        this.albums = albums;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.card_view_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Album album = albums.get(position);
        holder.tv_Title.setText(album.getNames());
        Glide.with(mContext).load(album.getUrls()).into(holder.iv_Cover);

        holder.cv_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlaysongActivity.class);
                intent.putExtra("songsCategory", album.getSongsCategory());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv_Item;
        TextView tv_Title;
        ImageView iv_Cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_Item = itemView.findViewById(R.id.cv_Item);
            tv_Title = itemView.findViewById(R.id.tv_Title);
            iv_Cover = itemView.findViewById(R.id.iv_Cover);
        }
    }
}
