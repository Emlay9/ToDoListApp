package com.example.ethomas13.todolistapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ListItemsActivity extends AppCompatActivity
{
    DBManager dbManager;
    SQLiteDatabase listsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);
        putListTitle();
    }

    private void putListTitle() {
        int listIndex = getIntent().getIntExtra("listIndex", 0);
        dbManager = new DBManager(this);
        String[] whereClause = new String[] {Integer.toString(listIndex)};
        listsDatabase =  dbManager.getReadableDatabase();
        Cursor cursor = listsDatabase.rawQuery("Select * from List", null);
        cursor.moveToPosition(listIndex);
        //the column index of the title
        int index = cursor.getColumnIndex(DBManager.C_LIST_DESCRIPTION);
        // (should be the list name at the cursors current position)
        String listName = cursor.getString(index);
        TextView listTitle = (TextView)findViewById(R.id.tv_listNameTitle);
        listTitle.setText(listName);
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


}
