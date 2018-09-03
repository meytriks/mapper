package com.dushan.dev.mapper.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.R;

import java.util.Objects;

public class MarkerActivity extends AppCompatActivity {

    private TextView markerNameText, markerAuthorText, markerDescriptionText, markerAddress, markerCategoryText;
    private CollapsingToolbarLayout markerImage;
    private ImageView markerToolbarImage;
    private FloatingActionButton markerAddFavoriteButton;
    private Button markerGetDirectionsButton;

    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Bundle extras = getIntent().getExtras();
        marker = new Marker(extras.getString("name"), extras.getString("address"), extras.getString("category"), extras.getString("author"), extras.getString("description"), extras.getString("imageURL"),
                extras.getDouble("latitude"), extras.getDouble("longitude"));
        connectViews();
        updateViews();
    }

    private void connectViews() {
        markerNameText = findViewById(R.id.markerNameText);
        markerAuthorText = findViewById(R.id.markerAuthorText);
        markerDescriptionText = findViewById(R.id.markerDescriptionText);
        markerAddress = findViewById(R.id.markerAddressText);
        markerCategoryText = findViewById(R.id.markerCategoryText);
        markerAddFavoriteButton = findViewById(R.id.markerAddFavoriteButton);
        markerImage = findViewById(R.id.markerToolbar);
        markerGetDirectionsButton= findViewById(R.id.markerGetDirectionsButton);
        markerToolbarImage = findViewById(R.id.markerToolbarImage);
        setupListeners();
    }

    private void updateViews() {
        markerNameText.setText(marker.getName());
        markerAuthorText.setText(marker.getAuthor());
        markerDescriptionText.setText(marker.getDescription());
        markerAddress.setText(marker.getAddress());
        markerCategoryText.setText(marker.getCategory());
        Glide.with(this).load(marker.getImageURL()).into(markerToolbarImage);
    }

    private void setupListeners(){
        markerAddFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        markerGetDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Getting directions..", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
