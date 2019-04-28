package com.forasoft.taskforforasoft.fragments_from_menu;


import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.forasoft.taskforforasoft.R;

public class Fragment_for_start extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        // с помощью этого метода включается режим вывода элементов фрагмента в ActionBar
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    // создается View
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_start, null);
//    }

    // создается меню
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_start, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}