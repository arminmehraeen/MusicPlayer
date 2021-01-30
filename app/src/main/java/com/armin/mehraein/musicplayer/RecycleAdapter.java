package com.armin.mehraein.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    ArrayList<Music> arrayList = new ArrayList<>();
    Context context ;

    public RecycleAdapter(ArrayList<Music> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view,parent,false);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Music music = arrayList.get(position);
        holder.music_artist.setText(music.getArtist());
        holder.muisc_name.setText(music.getName());
        Picasso.with(context).load(music.getPicture()).into(holder.muisc_picture);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Player.class);
                intent.putExtra("id",music.getId());
                intent.putExtra("name",music.getName());
                intent.putExtra("artist",music.getArtist());
                intent.putExtra("picture",music.getPicture());
                intent.putExtra("link",music.getLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView muisc_picture ;
        public TextView muisc_name ;
        public TextView music_artist ;
        public LinearLayout linearLayout ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            muisc_name = itemView.findViewById(R.id.music_name);
            muisc_picture = itemView.findViewById(R.id.music_picture);
            music_artist = itemView.findViewById(R.id.music_artist);
            linearLayout = itemView.findViewById(R.id.linear);

        }
    }
}
