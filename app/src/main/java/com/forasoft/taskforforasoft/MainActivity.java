package com.forasoft.taskforforasoft;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.forasoft.taskforforasoft.fragments.ErrorSearchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText search_field;
    private int nextStateBar = 0;
    private InputMethodManager inputManager;
    private RequestToItunesAPI requestToItunesAPI;
    private android.support.v4.app.FragmentTransaction fragment_transaction;

    String[] items;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    ListView listView;

    private FragmentListForAlbums fragment_list_for_albums;
    private ErrorSearchFragment error_search_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);



//        listView=(ListView)findViewById(R.id.listView);
        requestToItunesAPI = new RequestToItunesAPI();
    }

//    public void searchItem(String textToSearch){
//        for(String item:items){
//            String textToSearch1 = textToSearch.toLowerCase();
//
//            if(!item.toLowerCase().contains(textToSearch1)){
//                listItems.remove(item);
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    public void initList(){
//        items=new String[]{"Java","JavaScript","C#","PHP", "Нищая страна", "Python", "C", "SQL", "Ruby", "Objective-C", "JavA","JAVvScript","C1#","PHP", "СC++", "Py23thon", "C", "SQDFL", "Rub", "ObjectDFDive-C"};
//        listItems=new ArrayList<>(Arrays.asList(items));
//        adapter=new ArrayAdapter<String>(this, R.layout.item_for_list_with_albums, R.id.txtitem, listItems);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Log.d("value", adapter.getItem(position));
//                Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
//                intent.putExtra("album_name", adapter.getItem(position));
//                startActivity(intent);
//            }
//        });
//
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // получаем объект MenuInflater и вызываем метод inflate, который заполняет объект menu согласно файлу R.menu.menu_start_or_search
        getMenuInflater().inflate(R.menu.menu_start, menu);

        switch(nextStateBar){
            case 0:
                // устанавливаем титульник и делаем видимым группу group_start из файла R.menu.menu_start_or_search
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                menu.setGroupVisible(R.id.group_start, true);
//                menu.setGroupVisible(R.id.group_search, false);
                break;
            case 1:
                // убираем титульник и делаем не видимой группу group_start из menu.menu_start
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                menu.setGroupVisible(R.id.group_start, false);
//                menu.setGroupVisible(R.id.group_search, false);

                // получаем инфлейтор и используем его чтобы заполнить View объект содержимым файла R.layout.search_menu
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View newBar = inflater.inflate(R.layout.search_menu, null);

                // выставляем слушатель на стрелку назад
                newBar.findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(inputManager != null) {
                            // закрытие клавиатуры до того, как уберем View(newBar) из ActionBar'a!
                            inputManager.hideSoftInputFromWindow(search_field.getWindowToken(), 0);
                        }
                        // убираем созданный ранее ActionBar с (стрелкой и EditText)
                        getSupportActionBar().setCustomView(null);

                        // выставляем следующее состояние, теперь оно будет 0 - вернемся к стартовому состоянию
                        nextStateBar = 0;

                        // убираем лист из альбомов или же предупреждение о том, что альбомов нет
                        fragment_transaction = getSupportFragmentManager().beginTransaction();
                        if(error_search_fragment != null) fragment_transaction.remove(error_search_fragment);
                        if(fragment_list_for_albums != null) fragment_transaction.remove(fragment_list_for_albums);
                        fragment_transaction.commit();

                        // метод перерисовки меню/ActionBar, вызывается метод onCreateOptionsMenu
                        invalidateOptionsMenu();
                    }
                });

                // находим EditText из newBar'a
                search_field = (EditText)newBar.findViewById(R.id.search_field);
                // убираем видимую черточку снизу у EditText
                search_field.setBackgroundResource(android.R.color.transparent);

                search_field.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (!event.isShiftPressed()) {
                                // получаем строку без бробелов(в начале и в конце) из поля ввода(search_field)
                                String album_name = search_field.getText().toString().trim();
                                Log.d("album", album_name);
                                // проверяем полученную строку на наличие символов, и если они есть ищем альбомы
                                if(album_name.length() != 0){
                                    requestToItunesAPI.universalRequest("album", album_name, new CallBackForGettingAlbumsAndCreateList(), null);
                                }
                                // возвращаем true если нет действия
                                return true;
                            }
                        }
                        // возвращаем false если нет действия
                        return false;
                    }
                });

                // получаем ImageView send и устанавливаем для него слушатель
                ((ImageView) newBar.findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // получаем строку без бробелов(в начале и в конце) из поля ввода(search_field)
                        String album_name = search_field.getText().toString().trim();
                        Log.d("album", album_name);
                        // проверяем полученную строку на наличие символов, и если они есть ищем альбомы
                        if(album_name.length() != 0){
                            requestToItunesAPI.universalRequest("album", album_name, new CallBackForGettingAlbumsAndCreateList(), null);
                        }
                    }
                });

                // дает фокус EditText'у
                search_field.requestFocus();
//                search_field.setCursorVisible(true);
//                search_field.setFocusable(true);
//                search_field.setFocusableInTouchMode(true);

                if(inputManager != null) {
                    // открывает клавиатуру
                    inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }

                // получаем SupportActionBar для монипуляции с ActionBar и выводим в него тогль newBar
                ActionBar supportActionBar = getSupportActionBar();
                supportActionBar.setDisplayHomeAsUpEnabled(false);
                supportActionBar.setDisplayShowHomeEnabled (false);
                supportActionBar.setDisplayShowCustomEnabled(true);
                supportActionBar.setDisplayShowTitleEnabled(false);
                supportActionBar.setCustomView(newBar);
//                initList();


//                search_field.addTextChangedListener(new TextWatcher() {
//
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                        Log.i("info_beforeTextChanged", s+"__"+start+"__"+count+"__"+after);
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        Log.i("info_onTextChanged", s+"__"+start+"__"+count+"__"+before);
//                        if(s.toString().equals("")){
//                            // reset listview
//                            initList();
//                        } else {
//                            // perform search
//                            searchItem(s.toString());
//                        }
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        Log.i("info_afterTextChanged", s+"");
//                    }
//
//                });
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.search_label:
                nextStateBar = 1;
                // метод перерисовки меню/ActionBar
                invalidateOptionsMenu();
                break;
            case R.id.filter_search:
                break;
        }
        return true;
    }


    public class CallBackForGettingAlbumsAndCreateList implements Callback {
        @Override
        public void callForTrack(List<Map<String, Object>> result_list) {
            // ничего здесь писать не нужно
        }

        // в методе callForAlbum осуществляется инициализация интерфейса прокручивающегося листа с альбомами
        @Override
        public void callForAlbum(final List<Parcelable> result_list) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment_transaction = getSupportFragmentManager().beginTransaction();
                    if(result_list == null){
                        error_search_fragment = new ErrorSearchFragment();
                        fragment_transaction.replace(R.id.fragmentList, error_search_fragment);
                    }else {
                        Bundle args = new Bundle();
                        args.putParcelableArrayList("result_list", (ArrayList<? extends Parcelable>) result_list);
                        fragment_list_for_albums = new FragmentListForAlbums();
                        fragment_list_for_albums.setArguments(args);
                        // если использовать вместо android.support.v4.app.Fragment android.app.Fragment, то это приводит к странному поведению программы, см. ниже
                        // заменяем текущий список новым, должен быть метод replays, но здесь он работает как add, а add как replays
                        // если список заменить на TextView(в Album_list.xml), то все работает как надо...
                        // (баг пофиксил заменой android.app.Fragment на android.support.v4.app.Fragment для совместимости)
                        fragment_transaction.replace(R.id.fragmentList, fragment_list_for_albums);
                    }
                    fragment_transaction.commit();
                }
            });
        }
    }
}
