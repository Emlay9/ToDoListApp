package com.example.ethomas13.todolistapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements SharedPreferences.OnSharedPreferenceChangeListener
{

    ListView listView;
    DBManager dbManager;
    SQLiteDatabase database;
    SharedPreferences prefs;
    View mainView;

    ArrayList<String> listNames = new ArrayList<>();
    ArrayList<String> listIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        mainView = findViewById(R.id.layout_main_activity);
        String bgColor = prefs.getString("bg_color_options", "#eeeeee");
        mainView.setBackgroundColor(Color.parseColor(bgColor));
        changeTitleColor(bgColor);

        String fontSizeChoice = prefs.getString("font_size_options", "default");
        changeFontSize(fontSizeChoice);
    }

    public void changeFontSize(String sizeChoice)
    {
        TextView title = (TextView)findViewById(R.id.tv_listNameTitle);

        if(sizeChoice.equals("large"))
        {
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        }
        else
        {
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        }
    }

    public void changeTitleColor(String bgColor) {
        int lightText = Color.parseColor("#eeeeee");
        int darkText = Color.parseColor("#1c2833");
        TextView title = (TextView)findViewById(R.id.tv_listNameTitle);
        if(bgColor.equals("#eeeeee"))
        {
            title.setTextColor(darkText);
        }
        else
        {
            title.setTextColor(lightText);
        }
    }

    @Override
    protected void onStart() {
        populateList();
        listView = (ListView) findViewById(R.id.lv_lists);
        listView.setAdapter(new MyListAdapter(this, R.layout.custom_row_lists, listNames, listIds));


        super.onStart();
    }

    private void populateList() {
        listNames.clear();
        listView = (ListView)findViewById(R.id.lv_lists);
        dbManager = new DBManager(this);
        database =  dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * from List", null);
        //ArrayList<String> listsList = new ArrayList<>();
        if(cursor.getCount() == 0)
        {
            //database is empty
        }
        else
        {
            while(cursor.moveToNext())
            {
                // the parameter for getString() is column index (0 based)
                listIds.add(cursor.getString(0));
                listNames.add(cursor.getString(1));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_action_add_list:
            {
                Intent intent = new Intent(this, AddListActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.menu_item_prefs:
            {
                Intent intent = new Intent(this,PrefsActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.menu_item_archive:
            {
                Intent intent = new Intent(this, ArchiveActivity.class);
                this.startActivity(intent);
                break;
            }
        }
        return true;
    }

    private String getListID(int position){
        dbManager = new DBManager(this);
        database =  dbManager.getReadableDatabase();
        Cursor c = database.rawQuery("Select * from List", null);
        c.moveToPosition(position);
//        int columnIndex = c.getColumnIndex(DBManager.C_ITEM_ID);
        int listId = c.getInt(c.getColumnIndex(DBManager.C_LIST_ID));
        String listIdString = Integer.toString(listId);
        c.close();
        database.close();
        return listIdString;
    }

    private void deleteList(String listId) {

        //Delete the child table(Item) rows before deleting the list
        // "delete Item where C_ITEM_LIST_ID = "listID"
        dbManager = new DBManager(this);

        String whereClauseItem = DBManager.C_ITEM_LIST_ID + "=?";
        String whereArgs[] = {listId};

        String whereClauseList = DBManager.C_LIST_ID + "=?";

        database =  dbManager.getWritableDatabase();
        database.delete(DBManager.TABLE_NAME_ITEM, whereClauseItem, whereArgs);
        int rowsAffected = database.delete(DBManager.TABLE_NAME_LIST, whereClauseList, whereArgs);
        database.close();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String bgColor = prefs.getString("bg_color_options", "#000000");
        String fontSizeChoice = prefs.getString("font_size_options", "default");

        mainView.setBackgroundColor(Color.parseColor(bgColor));
        changeTitleColor(bgColor);
        changeFontSize(fontSizeChoice);
    }

    private class MyListAdapter extends ArrayAdapter<String> {
        private int layout;
        private List<String> listNames;
        private List<String> listIds;

        public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> listNames, List<String> listIds) {
            super(context, resource, listNames);
            this.listNames = listNames;
            this.listIds = listIds;
            layout = resource;
        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            Views mainViewHolder = null;
            if(convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                Views viewHolder = new Views();
                viewHolder.addButton = (ImageButton)convertView.findViewById(R.id.add_item_to_list_button);
                viewHolder.deleteListButton = (ImageButton)convertView.findViewById(R.id.delete_list_button);
                viewHolder.listTitle = (TextView)convertView.findViewById(R.id.tv_listName);

                String fontSizeChoice = prefs.getString("font_size_options", "default");
                if(fontSizeChoice.equals("large")) {
                    viewHolder.listTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                }
                else
                {
                    viewHolder.listTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                }

                final String listID = getListID(position);

                convertView.setTag(viewHolder);
                viewHolder.addButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(v.getId() == R.id.add_item_to_list_button)
                        {
//                            Toast.makeText(MainActivity.this, "Button clicked for list Item " + position, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, ListItemsActivity.class);
                            intent.putExtra("listIndex", position);
                            startActivity(intent);
                        }
                    }
                });

                viewHolder.deleteListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if(v.getId() == R.id.delete_list_button)
                        {
                            deleteList(listID);
                            listNames.remove(position);
                            recreate();
                        }
                    }
                });
            }
                mainViewHolder = (Views)convertView.getTag();
                mainViewHolder.listTitle.setText(getItem(position));

            return convertView;
        }
    }

    public class Views {
        ImageButton addButton;
        ImageButton deleteListButton;
        TextView listTitle;
    }
}
