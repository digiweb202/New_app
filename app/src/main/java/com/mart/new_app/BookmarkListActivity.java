package com.mart.new_app;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class BookmarkListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);

        ListView listView = findViewById(R.id.listView);

        // Retrieve bookmarked URLs from SharedPreferences
        SharedPreferences bookmarksPreferences = getSharedPreferences("Bookmarks", MODE_PRIVATE);
        Map<String, ?> bookmarksMap = bookmarksPreferences.getAll();

        // Create a list to store bookmark URLs
        ArrayList<String> bookmarksList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : bookmarksMap.entrySet()) {
            String url = entry.getValue().toString();
            bookmarksList.add(url);
        }

        // Create an adapter to populate the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookmarksList);
        listView.setAdapter(adapter);

        // Set long click listener to remove item from the list
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the item from the list
                String removedItem = bookmarksList.remove(position);
                // Update the adapter
                adapter.notifyDataSetChanged();
                // Show a toast message indicating the removed item
                Toast.makeText(BookmarkListActivity.this, "Removed: " + removedItem, Toast.LENGTH_SHORT).show();
                return true; // indicate that the long click has been consumed
            }
        });
    }
}
