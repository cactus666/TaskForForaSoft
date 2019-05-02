package com.forasoft.taskforforasoft;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class AlbumAdapter extends ArrayAdapter<Parcelable> {
    private LayoutInflater inflater;
    private int layout;
    private List<Parcelable> albumList;

    public AlbumAdapter(Context context, int resource, List<Parcelable> albums) {
        super(context, resource, albums);
        this.albumList = albums;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Album album = (Album) albumList.get(position);

        viewHolder.name_album_from_album_list.setText(album.getAlbumCensoredName());
        viewHolder.name_artist_from_album_list.setText(album.getArtistName());


        Picasso.with(getContext())
                .load(album.getUrlImage())
                .into(viewHolder.album_label);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AlbumActivity.class);
                intent.putExtra("album", album);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }


    private class ViewHolder {
        final ImageView album_label;
        final TextView name_album_from_album_list, name_artist_from_album_list;

        ViewHolder(View view){
            album_label = (ImageView) view.findViewById(R.id.album_label);
            name_album_from_album_list = (TextView) view.findViewById(R.id.name_album_from_album_list);
            name_artist_from_album_list = (TextView) view.findViewById(R.id.name_artist_from_album_list);
        }
    }
}
