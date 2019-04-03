package com.example.ethomas13.todolistapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ethomas13 on 3/12/2019.
 */

public class AddListActivity extends AppCompatActivity implements View.OnClickListener
{
    DBManager dbManager;
    SQLiteDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        dbManager = new DBManager(this);

        Button createListButton = (Button)findViewById(R.id.create_list);
        createListButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.create_list)
        {
            EditText listNameEditText = (EditText)findViewById(R.id.new_list_name);
            String listName = listNameEditText.getText().toString();
            addListToLocalDB(listName);
            listNameEditText.setText("");
            Toast.makeText(getApplicationContext(), "New list: " + listName + " added.", Toast.LENGTH_LONG).show();
        }
    }

    private void addListToLocalDB(String listName)
    {
        ContentValues values = new ContentValues();
        values.put(DBManager.C_LIST_DESCRIPTION, listName);

        database = dbManager.getWritableDatabase();
        database.insert(DBManager.TABLE_NAME_LIST, null, values);

        // deletes all tables
//        database.delete(DBManager.TABLE_NAME_ITEM, null, null);
//        database.delete(DBManager.TABLE_NAME_LIST, null, null);

        database.close();
    }
}
