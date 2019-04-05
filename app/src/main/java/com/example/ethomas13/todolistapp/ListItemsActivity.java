package com.example.ethomas13.todolistapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ListItemsActivity extends AppCompatActivity
{
    DBManager dbManager;
    SQLiteDatabase database;
    ArrayList<String> listData = new ArrayList<>();
    List<String> itemDescriptions = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    ListView listView;
    String listID;
    String listTitle;

//    String LIST_TITLE_TEST = "Groceries";
//    String ITEM_DESCRIPTION_TEST = "Apple";
//    String COMPLETED_FLAG_TEST = "0";
    String USERNAME_TEST = "rabbit";
    String PASSWORD_TEST = "pass";
//    String CREATED_DATE = "June 4 2019";

    Drawable notDoneDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        if(Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        notDoneDrawable = getResources().getDrawable(R.drawable.ic_action_not_done);

        putListTitle();
        populateList();
    }

    @Override
    protected void onStart() {
        populateList();
        listView = (ListView)findViewById(R.id.lv_list_items);
        listView.setAdapter(new MyListItemListAdapter(this, R.layout.custom_row_items, itemDescriptions, dates));

        super.onStart();
    }

    private String getCompletedStatus(String itemId) {
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

    private String getItemID(int position) {
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

    private void setCompletedStatus(String newStatus, String itemId) {

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
        database =  dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * from List", null);
        cursor.moveToPosition(listIndex);
        //the column index of the title
        int index = cursor.getColumnIndex(DBManager.C_LIST_DESCRIPTION);
        listID = Integer.toString(listIndex + 1);
        // (should be the list name at the cursors current position)
        listTitle = cursor.getString(index);
        TextView tvListTitle = (TextView)findViewById(R.id.tv_listNameTitle);
        tvListTitle.setText(listTitle);
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

    private void updateItemDescription(String itemId, String updatedItemDescriptionText) {
        dbManager = new DBManager(this);

        ContentValues values = new ContentValues();
        values.put(DBManager.C_ITEM_DESCRIPTION, updatedItemDescriptionText);
        String whereClause = DBManager.C_ITEM_ID + "=?";
        String whereArgs[] = {itemId};

        database =  dbManager.getWritableDatabase();
        database.update(DBManager.TABLE_NAME_ITEM, values, whereClause, whereArgs);
        database.close();
    }

    private void deleteItem(String itemId) {

        dbManager = new DBManager(this);

        String whereClause = DBManager.C_ITEM_ID + "=?";
        String whereArgs[] = {itemId};

        database =  dbManager.getWritableDatabase();
        database.delete(DBManager.TABLE_NAME_ITEM, whereClause, whereArgs);
        database.close();
    }



    private void archiveItem(String listTitle, String itemDescription, String completedFlag, String username, String password, String date)
    {
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://www.youcode.ca/Lab02Post.jsp");
            List <NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("LIST_TITLE", listTitle));
            postParameters.add(new BasicNameValuePair("CONTENT", itemDescription));
            postParameters.add(new BasicNameValuePair("COMPLETED_FLAG", completedFlag));
            postParameters.add(new BasicNameValuePair("ALIAS", username));
            postParameters.add(new BasicNameValuePair("PASSWORD", password));
            postParameters.add(new BasicNameValuePair("CREATED_DATE", date));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
            post.setEntity(formEntity);
            client.execute(post);
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
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
        private List<String> itemDescription;
        private List<String> date;

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
                viewHolder.confirmButton = (ImageButton)convertView.findViewById(R.id.confirm_edit_button);
                viewHolder.completedButton = (ImageButton)convertView.findViewById(R.id.complete_button);
                viewHolder.listItemDescription = (TextView)convertView.findViewById(R.id.tv_itemName);
                viewHolder.listItemDate = (TextView)convertView.findViewById(R.id.tv_itemDate);
                viewHolder.editItemDescription = (EditText)convertView.findViewById(R.id.et_itemName);
                convertView.setTag(viewHolder);

                final String itemId = getItemID(position);
                final String completedStatus = getCompletedStatus(itemId);
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
                            archiveItem(listTitle, itemDescription.get(position), completedStatus, USERNAME_TEST, PASSWORD_TEST, date.get(position));
                            deleteItem(itemId);
                            itemDescription.remove(position);
                            recreate();
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
                            deleteItem(itemId);
                            itemDescription.remove(position);
                            recreate();
                        }
                    }
                });

                final View finalConvertView = convertView;
                viewHolder.editButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if( v.getId() == R.id.edit_button)
                        {
                            viewHolder.editButton.setVisibility(View.GONE);
                            viewHolder.listItemDescription.setVisibility(View.INVISIBLE);
                            viewHolder.editItemDescription.setVisibility(View.VISIBLE);
                            viewHolder.editItemDescription.setText(itemDescription.get(position));

                            viewHolder.confirmButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

                viewHolder.confirmButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if( v.getId() == R.id.confirm_edit_button)
                        {
                            String updatedText = viewHolder.editItemDescription.getText().toString();
                            updateItemDescription(itemId, updatedText);
                            viewHolder.editItemDescription.setVisibility(View.INVISIBLE);
                            viewHolder.editButton.setVisibility(View.VISIBLE);
                            viewHolder.confirmButton.setVisibility(View.GONE);
                            viewHolder.listItemDescription.setVisibility(View.VISIBLE);
                            itemDescription.set(position, updatedText);
//                            viewHolder.listItemDescription.setText(updatedText);
//                            viewHolder.listItemDescription = (TextView)finalConvertView.findViewById(R.id.tv_itemName);
                        }
                    }
                });

                viewHolder.completedButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String itemId = getItemID(position);
                        String completedStatus = getCompletedStatus(itemId);
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

    private class Views {
        ImageButton deleteButton;
        ImageButton archiveButton;
        ImageButton editButton;
        ImageButton confirmButton;
        ImageButton completedButton;
        TextView listItemDescription;
        TextView listItemDate;
        EditText editItemDescription;
    }


}
