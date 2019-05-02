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
    private String search_field_after;
    private int nextStateBar = 0;
    private InputMethodManager inputManager;
    private RequestToItunesAPI requestToItunesAPI;
    private android.support.v4.app.FragmentTransaction fragment_transaction;
    private FragmentListForAlbums fragment_list_for_albums;
    private ErrorSearchFragment error_search_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        if (savedInstanceState != null) {
            nextStateBar = savedInstanceState.getInt("nextStateBar");
            search_field_after = savedInstanceState.getString("text_from_search_field");

            //Restore the fragment instance
            fragment_list_for_albums = (FragmentListForAlbums) getSupportFragmentManager().getFragment(savedInstanceState, "fragment_list_for_albums");
            error_search_fragment = (ErrorSearchFragment) getSupportFragmentManager().getFragment(savedInstanceState, "error_search_fragment");

            fragment_transaction = getSupportFragmentManager().beginTransaction();
            if(error_search_fragment != null){
                fragment_transaction.replace(R.id.fragmentList, error_search_fragment);
            }else if(fragment_list_for_albums != null){
                fragment_transaction.replace(R.id.fragmentList, fragment_list_for_albums);
            }
            fragment_transaction.commit();

        }


//        listView=(ListView)findViewById(R.id.listView);
        requestToItunesAPI = new RequestToItunesAPI();
    }

    // сохранение данных перед Pause.
    // например при повороте или смене языка происходит пересоздание активности и нужно сохранить данные
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("nextStateBar", nextStateBar);

        // сохраняем те объекты, что не null
        if(search_field != null)
            outState.putString("text_from_search_field", search_field.getText().toString());

        //Save the fragments instance
        // по условию, что прописано в callForAlbum только один фрагмент может быть не null или ниодного(если мы находимся в состоянии 1, где еще не появились фрагменты связанные с листом)
        if(fragment_list_for_albums != null)
            getSupportFragmentManager().putFragment(outState, "fragment_list_for_albums", fragment_list_for_albums);
        else if(error_search_fragment != null)
            getSupportFragmentManager().putFragment(outState, "error_search_fragment", error_search_fragment);
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
                break;
            case 1:
                // убираем титульник и делаем не видимой группу group_start из menu.menu_start
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                menu.setGroupVisible(R.id.group_start, false);

                // получаем инфлейтор и используем его чтобы заполнить View объект содержимым файла R.layout.search_menu
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View newBar = inflater.inflate(R.layout.search_menu, null);

                // выставляем слушатель на стрелку назад
                newBar.findViewById(R.id.back_arrow).setOnClickListener(on_click_listener_for_back);

                // находим EditText из newBar'a
                search_field = (EditText)newBar.findViewById(R.id.search_field);
                settingSearchField(search_field);


                // получаем ImageView clear и устанавливаем для него слушатель, в котором при нажатии отчищаем поле ввода
                ((ImageView) newBar.findViewById(R.id.clear)).setOnClickListener(on_click_listener_for_clear);

                // открывает клавиатуру, если это возможно(inputManager != null)
                if(inputManager != null)  inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                // получаем SupportActionBar для монипуляции с ActionBar и выводим в него newBar
                ActionBar supportActionBar = getSupportActionBar();
                supportActionBar.setDisplayHomeAsUpEnabled(false);
                supportActionBar.setDisplayShowHomeEnabled (false);
                supportActionBar.setDisplayShowCustomEnabled(true);
                supportActionBar.setDisplayShowTitleEnabled(false);
                supportActionBar.setCustomView(newBar);
                break;
        }
        return true;
    }

    void settingSearchField(EditText search_field){
        // убираем видимую черточку снизу у EditText
        search_field.setBackgroundResource(android.R.color.transparent);
        // устанавливаем значение, если оно не null, что значит, что активити пересоздали
        search_field.setText(search_field_after);
        search_field.setOnEditorActionListener(on_editor_action_listener_for_search);
        // дает фокус EditText'у
        search_field.requestFocus();
    }

    View.OnClickListener on_click_listener_for_clear = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // отчищаем поле ввода
            search_field.setText("");
        }
    };

    EditText.OnEditorActionListener on_editor_action_listener_for_search = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                // получаем строку без пробелов(в начале и в конце) из поля ввода(search_field)
                String album_name = search_field.getText().toString().trim();
                Log.d("album", album_name);
                // проверяем полученную строку на наличие символов, и если они есть ищем альбомы
                if(album_name.length() != 0){
                    requestToItunesAPI.universalRequest("album", album_name, new CallBackForGettingAlbumsAndCreateList(), null);
                }

                if(inputManager != null) {
                    // закрытие клавиатуры, что бы не мешала просмотру результатов
                    inputManager.hideSoftInputFromWindow(search_field.getWindowToken(), 0);
                }

                // возвращаем true если нет действия
                return true;
            }
            // возвращаем false если нет действия
            return false;
        }
    };

    View.OnClickListener on_click_listener_for_back = new View.OnClickListener() {
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
            // мы должны знать, что его уже нет, и нет смысла сохранять его поле, ведь это может привести к ошибке
            search_field = null;

            // убираем лист из альбомов или же предупреждение о том, что альбомов нет
            fragment_transaction = getSupportFragmentManager().beginTransaction();
            if(error_search_fragment != null){
                fragment_transaction.remove(error_search_fragment);
                // для того, чтобы если мы вернемся к состоянию 1 и попытаемся повернуть экран, то fragment неужастся сохранить, т.к. он уе удален в строку выше
                error_search_fragment = null;
            }
            if(fragment_list_for_albums != null) {
                fragment_transaction.remove(fragment_list_for_albums);
                // для того, чтобы если мы вернемся к состоянию 1 и попытаемся повернуть экран, то fragment неужастся сохранить, т.к. он уе удален в строку выше
                fragment_list_for_albums = null;
            }
            fragment_transaction.commit();

            // метод перерисовки меню/ActionBar, вызывается метод onCreateOptionsMenu
            invalidateOptionsMenu();
        }
    };

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
                        // для того чтобы при восстановлении явно проверить какой fragment нужно установить
                        fragment_list_for_albums = null;
                        fragment_transaction.replace(R.id.fragmentList, error_search_fragment);
                    }else {
                        Bundle args = new Bundle();
                        args.putParcelableArrayList("result_list", (ArrayList<? extends Parcelable>) result_list);
                        fragment_list_for_albums = new FragmentListForAlbums();
                        fragment_list_for_albums.setArguments(args);
                        // для того чтобы при восстановлении явно проверить какой fragment нужно установить
                        error_search_fragment = null;
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