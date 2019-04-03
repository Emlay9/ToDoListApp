package com.example.ethomas13.todolistapp;

import android.app.Activity;
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

import static java.security.AccessController.getContext;

public class ListItemsActivity extends AppCompatActivity
{
    DBManager dbManager;
    SQLiteDatabase database;
    ArrayList<String> listData = new ArrayList<>();
    ListView listView;
    String listID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);
        putListTitle();
        populateList();
    }

    @Override
    protected void onStart() {
        populateList();
        listView = (ListView)findViewById(R.id.lv_list_items);
        listView.setAdapter(new MyListItemListAdapter(this, R.layout.custom_row_items, listData));
        super.onStart();
    }

    private void putListTitle() {
        int listIndex = getIntent().getIntExtra("listIndex", 0);
        dbManager = new DBManager(this);
        String[] whereClause = new String[] {Integer.toString(listIndex)};
        database =  dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * from List", null);
        cursor.moveToPosition(listIndex);
        //the column index of the title
        int index = cursor.getColumnIndex(DBManager.C_LIST_DESCRIPTION);
        listID = Integer.toString(listIndex + 1);
        // (should be the list name at the cursors current position)
        String listName = cursor.getString(index);
        TextView listTitle = (TextView)findViewById(R.id.tv_listNameTitle);
        listTitle.setText(listName);
        cursor.close();
    }

    private void populateList() {
        listData.clear();
        listView = (ListView)findViewById(R.id.lv_list_items);
        dbManager = new DBManager(this);
        database =  dbManager.getReadableDatabase();
        //the first id is always 1 in the table
//        Cursor listContents = database.rawQuery("Select * from Item", null);
        Cursor listContents = database.rawQuery("Select * from Item WHERE " + DBManager.C_ITEM_LIST_ID + " = " + listID, null);
        if(listContents.getCount() == 0)
        {
            //database is empty
        }
        else
        {
            while(listContents.moveToNext())
            {
                listData.add(listContents.getString(listContents.getColumnIndex(DBManager.C_ITEM_DESCRIPTION)));
            }
        }
        listContents.close();
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
                Intent intent = new Intent(this, AddListItemActivity.class);
                intent.putExtra("listID", listID);
                startActivity(intent);
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

    private class MyListItemListAdapter extends ArrayAdapter<String>
    {
        private int layout;
        private String[] itemDescription;
        private String[] date;
        private List<String> mObjects;
        public MyListItemListAdapter(Activity context, @LayoutRes int resource, @NonNull List<String> objects)
        {
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
                viewHolder.deleteButton = (ImageButton)convertView.findViewById(R.id.delete_button);
                viewHolder.archiveButton = (ImageButton)convertView.findViewById(R.id.archive_button);
                viewHolder.listItemDescription = (TextView)convertView.findViewById(R.id.tv_itemName);
                viewHolder.listItemDate = (TextView)convertView.findViewById(R.id.tv_itemDate);
                convertView.setTag(viewHolder);
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        switch(v.getId())
                        {
                            case R.id.add_item_to_list_button:
                            {
//                                Toast.makeText(getContext(), "Button clicked for list Item " + position, Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(getContext(), ListItemsActivity.class);
//                                intent.putExtra("listIndex", position);
//                                startActivity(intent);
                            }
                            case R.id.more_list_options_button:
                            {

                            }
                        }
                    }
                });
            }
            mainViewHolder = (Views)convertView.getTag();
            mainViewHolder.listItemDescription.setText(getItem(position));

            return convertView;
        }
    }

    public class Views {
        ImageButton deleteButton;
        ImageButton archiveButton;
        TextView listItemDescription;
        TextView listItemDate;
    }


}
