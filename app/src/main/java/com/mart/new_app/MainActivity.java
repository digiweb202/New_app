
package com.mart.new_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText urlEditText;
    private SharedPreferences bookmarksPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.setWebViewClient(new WebViewClient());
        // Set up WebView
        setupWebView();

        // Set up navigation controls
        setupNavigationControls();

        // Set up bookmark button
        Button showBookmarksButton = findViewById(R.id.bookmarkButton);
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
                if (isExternalLink(url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    return false; // Load link internally
                }
            }
        });
    }

    private void setupNavigationControls() {
        Button backButton = findViewById(R.id.backButton);
        Button forwardButton = findViewById(R.id.forwardButton);
        Button refreshButton = findViewById(R.id.refreshButton);

        backButton.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });

        forwardButton.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });

        refreshButton.setOnClickListener(v -> {
            webView.reload();
        });

        urlEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String url = urlEditText.getText().toString();
                loadUrl(url);
                return true;
            }
            return false;
        });
    }

    private void setupBookmarkButton() {
        Button bookmarkButton = findViewById(R.id.bookmarkButton);
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

    private boolean isExternalLink(String url) {
        // Add logic to determine if the link should be opened externally
        // For example, check if it's a different domain than the current page
        return false;
    }
}
