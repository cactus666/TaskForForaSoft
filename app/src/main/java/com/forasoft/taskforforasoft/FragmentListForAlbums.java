package com.forasoft.taskforforasoft;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;


public class FragmentListForAlbums extends Fragment {

    private ListView list_with_albums;
    private ArrayList<Parcelable> result_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("status", "onCreateView");
        return inflater.inflate(R.layout.fragment_album_list, null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment state here
        outState.putParcelableArrayList("result_list", result_list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment state here
            result_list = savedInstanceState.getParcelableArrayList("result_list");
        }else {
            result_list = getArguments().getParcelableArrayList("result_list");
        }
        list_with_albums = (ListView) getActivity().findViewById(R.id.list_for_albums);

        AlbumAdapter adapter = new AlbumAdapter(getActivity(), R.layout.item_for_list_with_albums, result_list);
        list_with_albums.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        // обновляем стек из последних альбомов для предложки
//        Log.d("status", "onPause_"+getArguments().getString("arg"));
    }

}
