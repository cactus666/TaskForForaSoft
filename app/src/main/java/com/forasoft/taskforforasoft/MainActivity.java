package com.forasoft.taskforforasoft;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private EditText search_field;
    private int nextStateBar = 0;
    private InputMethodManager inputManager;

    String[] items;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        listView=(ListView)findViewById(R.id.listView);
        new RequestToItunesAPI().universalRequest("album", "Нищая страна");
//        new RequestToItunesAPI().universalRequest("musicTrack", "белые хлопья");
    }

    public void searchItem(String textToSearch){
        for(String item:items){
            String textToSearch1 = textToSearch.toLowerCase();

            if(!item.toLowerCase().contains(textToSearch1)){
                listItems.remove(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void initList(){
        items=new String[]{"Java","JavaScript","C#","PHP", "С++", "Python", "C", "SQL", "Ruby", "Objective-C", "JavA","JAVvScript","C1#","PHP", "СC++", "Py23thon", "C", "SQDFL", "Rub", "ObjectDFDive-C"};
        listItems=new ArrayList<>(Arrays.asList(items));
        adapter=new ArrayAdapter<String>(this, R.layout.item_for_list_with_albums, R.id.txtitem, listItems);
        listView.setAdapter(adapter);
    }

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
                LayoutInflater inflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View newBar = inflator.inflate(R.layout.search_menu, null);

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
                        // метод перерисовки меню/ActionBar, вызывается метод onCreateOptionsMenu
                        invalidateOptionsMenu();
                    }
                });

                // находим EditText из newBar'a
                search_field = (EditText)newBar.findViewById(R.id.search_field);
                // убираем видимую черточку снизу у EditText
                search_field.setBackgroundResource(android.R.color.transparent);


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
                initList();


                search_field.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.i("info_beforeTextChanged", s+"__"+start+"__"+count+"__"+after);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.i("info_onTextChanged", s+"__"+start+"__"+count+"__"+before);
                        if(s.toString().equals("")){
                            // reset listview
                            initList();
                        } else {
                            // perform search
                            searchItem(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.i("info_afterTextChanged", s+"");
                    }

                });
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
}
