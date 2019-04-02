package com.example.ethomas13.todolistapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    ListView listView;
    DBManager dbManager;
    SQLiteDatabase listsDatabase;
    ImageButton addButton;
    ImageButton settingsButton;
    ArrayList<String> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        populateList();
        listView = (ListView) findViewById(R.id.lv_lists);
        listView.setAdapter(new MyListAdapter(this, R.layout.custom_row_lists, listData));
        super.onStart();
    }

    private void populateList() {
        listData.clear();
        listView = (ListView)findViewById(R.id.lv_lists);
        dbManager = new DBManager(this);
        listsDatabase =  dbManager.getReadableDatabase();
        Cursor listContents = listsDatabase.rawQuery("Select * from List", null);
        //ArrayList<String> listsList = new ArrayList<>();
        if(listContents.getCount() == 0)
        {
            //database is empty
        }
        else
        {
            while(listContents.moveToNext())
            {
                listData.add(listContents.getString(1));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
//                Intent intent = new Intent(this,ChatterListActivity.class);
//                this.startActivity(intent);
                break;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.add_item_to_list_button:
            {

            }
        }
    }

    private class MyListAdapter extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
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
                viewHolder.settingsButton = (ImageButton)convertView.findViewById(R.id.more_list_options_button);
                viewHolder.listTitle = (TextView)convertView.findViewById(R.id.tv_listName);

                convertView.setTag(viewHolder);
                viewHolder.addButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        switch(v.getId())
                        {
                            case R.id.add_item_to_list_button:
                            {
//                                Toast.makeText(getContext(), "Button clicked for list Item " + position, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(), ListItemsActivity.class);
                                intent.putExtra("listIndex", position);
                                startActivity(intent);
                            }
                            case R.id.more_list_options_button:
                            {

                            }
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
        ImageButton settingsButton;
        TextView listTitle;
    }
}
