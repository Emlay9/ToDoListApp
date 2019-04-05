package com.example.ethomas13.todolistapp;

import android.app.Activity;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity
{

    List<String> archivedLists = new ArrayList<>();
    List<String> archivedItems = new ArrayList<>();
    List<String> archivedCompleted1or0 = new ArrayList<>();
    List<String> archivedDates = new ArrayList<>();
    ListView listView;

    String USERNAME_TEST = "rabbit";
    String PASSWORD_TEST = "pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        if(Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getArchive();
        listView = (ListView)findViewById(R.id.lv_achive);
        listView.setAdapter(new MyListItemListAdapter(this, R.layout.custom_row_archive, archivedLists, archivedItems, archivedDates, archivedCompleted1or0));
    }


    public void getArchive() {
        BufferedReader in = null;
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://www.youcode.ca/Lab02Get.jsp?ALIAS=" + USERNAME_TEST + "&PASSWORD=" + PASSWORD_TEST));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String field  = "";
            while ((field = in.readLine()) != null)
            {
                archivedDates.add(field);
                if((field = in.readLine()) != null)
                    archivedLists.add(field);
                if((field = in.readLine()) != null)
                    archivedItems.add(field);
                if((field = in.readLine()) != null)
                    archivedCompleted1or0.add(field);
            }
            in.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private class MyListItemListAdapter extends ArrayAdapter<String>
    {
        private int layout;
        private List<String> lists;
        private List<String> items;
        private List<String> dates;
        private List<String> completed;

        public MyListItemListAdapter(Activity context, @LayoutRes int resource, List<String> lists, List<String> items, List<String> dates, List<String> completed)
        {
            super(context, resource, lists);
            this.lists = lists;
            this.items = items;
            this.dates = dates;
            this.completed = completed;
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

                viewHolder.listName = (TextView)convertView.findViewById(R.id.tv_archive_list_name);
                viewHolder.item = (TextView)convertView.findViewById(R.id.tv_archive_content);
                viewHolder.date = (TextView)convertView.findViewById(R.id.tv_archive_created_date);
                viewHolder.completed = (TextView)convertView.findViewById(R.id.tv_archive_completed);
                convertView.setTag(viewHolder);
            }
            mainViewHolder = (Views)convertView.getTag();

            mainViewHolder.listName.setText(lists.get(position));
            mainViewHolder.item.setText(items.get(position));
            mainViewHolder.date.setText(dates.get(position));
            mainViewHolder.completed.setText(completed.get(position));
            return convertView;
        }
    }

    private class Views {
        TextView listName;
        TextView item;
        TextView date;
        TextView completed;
    }
}
