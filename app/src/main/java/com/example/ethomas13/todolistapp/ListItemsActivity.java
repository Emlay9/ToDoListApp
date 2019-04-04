package com.example.ethomas13.todolistapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
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

public class ListItemsActivity extends AppCompatActivity implements View.OnClickListener
{
    DBManager dbManager;
    SQLiteDatabase database;
    ArrayList<String> listData = new ArrayList<>();
    boolean toggle = true;
    List<String> itemDescriptions = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    ListView listView;
    String listID;

//    ImageButton toggleComplete = (ImageButton)findViewById(R.id.complete_button);
    Drawable notDoneDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        notDoneDrawable = getResources().getDrawable(R.drawable.ic_action_not_done);

        putListTitle();
        populateList();
    }

    @Override
    protected void onStart()
    {
        populateList();
        listView = (ListView)findViewById(R.id.lv_list_items);
        listView.setAdapter(new MyListItemListAdapter(this, R.layout.custom_row_items, itemDescriptions, dates));

        super.onStart();
    }

    private String getCompletedStatus(int position, String itemId)
    {
        dbManager = new DBManager(this);
        database =  dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * from Item WHERE " + DBManager.C_ITEM_ID + " = " + itemId, null);
        //move to the first (and only position since there should only be one row with a particular itemId)
        cursor.moveToFirst();
        //get the status from the column item completed
        //getString() returns the value from the requested column (at the cursors current position)
        String status = cursor.getString(cursor.getColumnIndex(DBManager.C_ITEM_COMPLETED));
        cursor.close();
        return status;
    }

    private String getItemID(int position)
    {
        dbManager = new DBManager(this);
        database =  dbManager.getReadableDatabase();
        Cursor c = database.rawQuery("Select * from Item WHERE " + DBManager.C_ITEM_LIST_ID + " = " + listID, null);
        c.moveToPosition(position);
//        int columnIndex = c.getColumnIndex(DBManager.C_ITEM_ID);
        int itemID = c.getInt(c.getColumnIndex(DBManager.C_ITEM_ID));
        String itemIDString = Integer.toString(itemID);
        database.close();
        return itemIDString;
    }

    private void setCompletedStatus(String newStatus, String itemId)
    {
        dbManager = new DBManager(this);


        ContentValues values = new ContentValues();
        values.put(DBManager.C_ITEM_COMPLETED, newStatus);
        String whereClause = DBManager.C_ITEM_ID + "=?";
        String whereArgs[] = {itemId};

        database =  dbManager.getWritableDatabase();
        database.update(DBManager.TABLE_NAME_ITEM, values, whereClause, whereArgs);
        database.close();
    }

    private void putListTitle() {
        int listIndex = getIntent().getIntExtra("listIndex", 0);
        dbManager = new DBManager(this);
//        String[] whereClause = new String[] {Integer.toString(listIndex)};
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
        itemDescriptions.clear();
        dates.clear();
        listView = (ListView)findViewById(R.id.lv_list_items);
        dbManager = new DBManager(this);
        database =  dbManager.getReadableDatabase();
//        the first id is always 1 in the table
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
                //original (no date)
//                listData.add(listContents.getString(listContents.getColumnIndex(DBManager.C_ITEM_DESCRIPTION)));
                itemDescriptions.add(listContents.getString(listContents.getColumnIndex(DBManager.C_ITEM_DESCRIPTION)));
                dates.add(listContents.getString(listContents.getColumnIndex(DBManager.C_ITEM_DATE)));
            }
        }
        listContents.close();
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

    @Override
    public void onClick(View v) {

    }

    private class MyListItemListAdapter extends ArrayAdapter<String>
    {
        private int layout;
        private List<String> itemDescription;
        private List<String> date;
        // original version that worked with one list item (description no date)
//        public MyListItemListAdapter(Activity context, @LayoutRes int resource, @NonNull List<String> objects)
//        {
//            super(context, resource, objects);
//            mObjects = objects;
//            layout = resource;
//        }

        public MyListItemListAdapter(Activity context, @LayoutRes int resource, List<String> itemDescription, List<String> date)
        {
            super(context, resource, itemDescription);
            this.itemDescription = itemDescription;
            this.date = date;
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
                final Views viewHolder = new Views();
                //options buttons
                viewHolder.deleteButton = (ImageButton)convertView.findViewById(R.id.delete_button);
                viewHolder.archiveButton = (ImageButton)convertView.findViewById(R.id.archive_button);
                viewHolder.editButton = (ImageButton)convertView.findViewById(R.id.edit_button);
                viewHolder.completedButton = (ImageButton)convertView.findViewById(R.id.complete_button);
                viewHolder.listItemDescription = (TextView)convertView.findViewById(R.id.tv_itemName);
                viewHolder.listItemDate = (TextView)convertView.findViewById(R.id.tv_itemDate);
                convertView.setTag(viewHolder);

                String itemId = getItemID(position);
                String completedStatus = getCompletedStatus(position, itemId);
                if(completedStatus.equals("1"))
                {
                    viewHolder.completedButton.setImageResource(R.drawable.ic_action_complete);
                }

                viewHolder.archiveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if( v.getId() == R.id.archive_button)
                        {

                        }
                    }
                });

                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if( v.getId() == R.id.delete_button)
                        {

                        }
                    }
                });

                viewHolder.editButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if( v.getId() == R.id.edit_button)
                        {

                        }
                    }
                });

                viewHolder.completedButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String itemId = getItemID(position);
                        String completedStatus = getCompletedStatus(position, itemId);
                        if(completedStatus.equals("0"))
                        {

                            setCompletedStatus("1", itemId);
                            viewHolder.completedButton.setImageResource(R.drawable.ic_action_complete);
                        }
                        else
                        {
                            setCompletedStatus("0", itemId);
                            viewHolder.completedButton.setImageResource(R.drawable.ic_action_not_done);
                        }
                    }//end on click button
                });


            }
            mainViewHolder = (Views)convertView.getTag();
//          mainViewHolder.listItemDescription.setText(getItem(position));

            mainViewHolder.listItemDescription.setText(itemDescription.get(position));
            mainViewHolder.listItemDate.setText(date.get(position));
            return convertView;
        }
    }

    public class Views {
        ImageButton deleteButton;
        ImageButton archiveButton;
        ImageButton editButton;
        ImageButton completedButton;
        TextView listItemDescription;
        TextView listItemDate;
    }


}
