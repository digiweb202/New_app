
package com.mart.new_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText urlEditText;
    private SharedPreferences bookmarksPreferences;
    private Button bookmarklist;
    Switch switchButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assuming you have a Switch with the ID "switch1"
        switchButton = findViewById(R.id.switch1);

        // Set an OnCheckedChangeListener to monitor the state changes of the Switch
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Check if the Switch is checked (enabled) or unchecked (disabled)
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "Extenal Web Browser Open Link", Toast.LENGTH_SHORT).show();
                } else {
                    // Switch is unchecked (disabled)
                    Toast.makeText(MainActivity.this,"Internal Web browser Open Link",Toast.LENGTH_SHORT).show();

                }
            }
        });

        bookmarklist = findViewById(R.id.bookmarkButtonlist);

        bookmarklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BookmarkListActivity.class);
                startActivity(intent);
            }
        });
        // Initialize SharedPreferences for bookmarks
        bookmarksPreferences = getSharedPreferences("Bookmarks", MODE_PRIVATE);

        // Find views by ID
        webView = findViewById(R.id.webView);
        urlEditText = findViewById(R.id.urlEditText);

        // Load a default URL
//        loadUrl("https://www.tutorialspoint.com/index.htm");
        // loading https://www.geeksforgeeks.org url in the WebView.
        webView.loadUrl("https://www.google.com");

        // this will enable the javascript.
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(false);
        webView.getSettings().setJavaScriptEnabled(false);

//        webView.getSettings().setUserAgentString("Your Custom User Agent String");
        webView.getSettings().setAllowFileAccess(false);



        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.setWebViewClient(new WebViewClient());
        // Set up WebView
        setupWebView();

        // Set up navigation controls
        setupNavigationControls();

        // Set up bookmark button
        FloatingActionButton  showBookmarksButton = findViewById(R.id.bookmarkButton);
        showBookmarksButton.setOnClickListener(v -> showBookmarks());

        setupBookmarkButton();

    }

    private void loadUrl(String url) {
        webView.loadUrl(url);
        Toast.makeText(this, "Loading URL ...", Toast.LENGTH_SHORT).show();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false); // Disable JavaScript if not needed
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Show loading indicator or progress bar
                showLoadingIndicator();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Hide loading indicator or progress bar
                hideLoadingIndicator();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Handle the error, e.g., show an error message to the user
                showErrorDialog();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {



                // Decide whether to open link internally or externally
                if (switchButton.isChecked()) {
                    // If it's an external link, open it in another browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    // Load the link internally in the WebView
                    view.loadUrl(url);
                    return false; // Load link internally
                }
            }
        });
    }

    private void setupNavigationControls() {
        FloatingActionButton backButton = findViewById(R.id.backButton);
        FloatingActionButton forwardButton = findViewById(R.id.forwardButton);
        FloatingActionButton refreshButton = findViewById(R.id.refreshButton);

        backButton.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
                Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
            }
        });

        forwardButton.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
                Toast.makeText(this,"Forward",Toast.LENGTH_SHORT).show();
            }
        });

        refreshButton.setOnClickListener(v -> {
            webView.reload();
            Toast.makeText(this, "Refresh page", Toast.LENGTH_SHORT).show();
        });

        // Listen for Enter key press on the EditText
        urlEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // If Enter key is pressed, load the URL
                String url = urlEditText.getText().toString().trim(); // Trim to remove leading/trailing whitespace
                if(isValidUrl(url)) {

                    if (switchButton.isChecked()) {
                        // If it's an external link, open it in another browser
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);

                    }else{
                        loadUrl(url);
                    }

                } else {
                    // URL format is invalid, show error message or handle it accordingly
                    Toast.makeText(getApplicationContext(), "Invalid URL format", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    // Function to check if the URL format is valid
    private boolean isValidUrl(String url) {
        // Regular expression to match URL format
        String urlRegex = "^(https?|ftp)://[a-zA-Z0-9+&@#/%?=~_|!:,.;-]*[-a-zA-Z0-9+&@#/%=~_|]$";
        return url.matches(urlRegex);
    }
    private void setupBookmarkButton() {
        FloatingActionButton bookmarkButton = findViewById(R.id.bookmarkButton);
        bookmarkButton.setOnClickListener(v -> {
            String currentUrl = webView.getUrl();
            if (currentUrl != null) {
                // Save bookmarked URL to SharedPreferences
                SharedPreferences.Editor editor = bookmarksPreferences.edit();
                editor.putString(currentUrl, currentUrl);
                editor.apply();
                Toast.makeText(MainActivity.this, "Bookmark added", Toast.LENGTH_SHORT).show();

                // Show updated bookmark list
                showBookmarks();
            }
        });
    }

    private void showBookmarks() {
        // Retrieve bookmarked URLs from SharedPreferences
        Map<String, ?> bookmarksMap = bookmarksPreferences.getAll();
        if (bookmarksMap.isEmpty()) {
            Toast.makeText(MainActivity.this, "No bookmarks saved", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder bookmarksList = new StringBuilder("Bookmarks:\n");
            for (Map.Entry<String, ?> entry : bookmarksMap.entrySet()) {
                String url = entry.getValue().toString(); // Get the bookmarked URL
                bookmarksList.append(url).append("\n");
            }
            Toast.makeText(MainActivity.this, bookmarksList.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void showLoadingIndicator() {
        // Show loading indicator (e.g., progress bar)
    }

    private void hideLoadingIndicator() {
        // Hide loading indicator (e.g., progress bar)
    }

    private void showErrorDialog() {
        // Show error dialog to the user
        Toast.makeText(MainActivity.this, "Failed to load the page", Toast.LENGTH_SHORT).show();
    }

//    private boolean isExternalLink(String url) {
//        // Add logic to determine if the link should be opened externally
//        // For example, check if it's a different domain than the current page
//        return false;
//    }
private boolean isExternalLink(String url) {
    // If the switch is enabled, consider all links as external
    if (switchButton.isChecked()) {
        return true;
    }

    // Get the domain of the current page
    String currentDomain = getDomain(webView.getUrl());

    // Get the domain of the clicked link
    String clickedDomain = getDomain(url);

    // Compare the domains
    return !currentDomain.equals(clickedDomain);
}


    private String getDomain(String url) {
        try {
            // Parse the URL
            URI uri = new URI(url);

            // Get the host (domain) from the URL
            String domain = uri.getHost();

            // Remove the 'www.' prefix if present
            if (domain != null && domain.startsWith("www.")) {
                domain = domain.substring(4);
            }

            return domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


}
