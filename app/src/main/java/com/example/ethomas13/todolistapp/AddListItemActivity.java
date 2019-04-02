package com.example.ethomas13.todolistapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddListItemActivity extends AppCompatActivity implements View.OnClickListener
{
    DBManager dbManager;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);

        Button addListItemBtn = (Button)findViewById(R.id.btn_add_item);
        addListItemBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.create_list)
        {
            EditText listNameEditText = (EditText)findViewById(R.id.et_new_list_item_name);
            String listItemName = listNameEditText.getText().toString();
            addListItemToLocalDB(listItemName);
            listNameEditText.setText("");
            Toast.makeText(getApplicationContext(), "New list item: " + listItemName + " added.", Toast.LENGTH_LONG).show();
        }
    }

    private void addListItemToLocalDB(String listItemName)
    {
        ContentValues values = new ContentValues();
        values.put(DBManager.C_LIST_DESCRIPTION, listItemName);

        database = dbManager.getWritableDatabase();
        database.insert(DBManager.TABLE_NAME_LIST, null, values);
    }
}
