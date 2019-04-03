package com.example.ethomas13.todolistapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;

public class AddListItemActivity extends AppCompatActivity implements View.OnClickListener
{
    DBManager dbManager;
    SQLiteDatabase database;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);

        dbManager = new DBManager(this);

        Button addListItemBtn = (Button)findViewById(R.id.btn_add_item);
        addListItemBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btn_add_item)
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
        String notCompleted = "0";
        String listIndex = getIntent().getStringExtra("listID");

        String currentDateTime = DateFormat.getDateInstance().format(new Date());
        ContentValues values = new ContentValues();
        values.put(DBManager.C_ITEM_DESCRIPTION, listItemName);
        values.put(DBManager.C_ITEM_DATE, currentDateTime);
        values.put(DBManager.C_ITEM_COMPLETED, notCompleted);
        values.put(DBManager.C_ITEM_LIST_ID, listIndex);

        database = dbManager.getWritableDatabase();
        database.insert(DBManager.TABLE_NAME_ITEM, null, values);
        database.close();
    }
}
