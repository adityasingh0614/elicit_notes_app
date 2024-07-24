package com.example.todaybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todaybuddy.databinding.ActivityDatainsertBinding;
import com.yahiaangelo.markdownedittext.MarkdownEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Datainsert extends AppCompatActivity {
    ActivityDatainsertBinding binding;
    boolean isUpdate = false;
    int noteid = -1;
    Noteviewmodel noteviewmodel;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        config();
//        onConfigEnterTransition(); // Set up enter transition
        super.onCreate(savedInstanceState);
        binding = ActivityDatainsertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteviewmodel = new ViewModelProvider(this).get(Noteviewmodel.class);

        setupEditMode();
        setupStylesBar();
        setupBackButton();
    }

//    private void config() {
//        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
//        getWindow().setSharedElementsUseOverlay(false);
//    }

//    private void onConfigEnterTransition() {
//        findViewById(android.R.id.content).setTransitionName("go");
//        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
//
//        MaterialContainerTransform transform = new MaterialContainerTransform();
//        transform.addTarget(android.R.id.content);
//        transform.setDuration(280L); // Enter transition duration
//
//        getWindow().setSharedElementEnterTransition(transform);
//        getWindow().setSharedElementReturnTransition(transform);
//
//    }

//    private void onConfigExitTransition() {
//        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
//        getWindow().setSharedElementsUseOverlay(false);
//
//        MaterialContainerTransform transform = new MaterialContainerTransform();
//        transform.addTarget(android.R.id.content);
//        transform.setDuration(250L); // Exit transition duration
//
//    }

    private void setupEditMode() {
        String type = getIntent().getStringExtra("type");
        if ("update".equals(type)) {
            binding.title.setText("Update Note");
            id = getIntent().getIntExtra("noteid", 0);
            String oldtitle = getIntent().getStringExtra("notetext");
            String olddisplay = getIntent().getStringExtra("notedisplay");
            binding.Title.setText(oldtitle);
            binding.Display.setText(olddisplay);
        }
    }

    private void setupStylesBar() {
        MarkdownEditText markdownEditText = binding.Display;

    }

    private void setupBackButton() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(Datainsert.this);
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        KeyboardUtils.hideKeyboard(Datainsert.this);
        String title = binding.Title.getText().toString().trim();
        String display = Objects.requireNonNull(binding.Display.getText()).toString().trim();
        String date = getCurrentDate();
        boolean isUpdate = Objects.requireNonNull(getIntent().getStringExtra("type")).equals("update");
        if (isUpdate) {
            String oldtitle = getIntent().getStringExtra("notetext");
            String olddisplay = getIntent().getStringExtra("notedisplay");

            if (oldtitle != null && olddisplay != null && (!oldtitle.equals(title) || !olddisplay.equals(display))) {
                Intent i = new Intent();
                i.putExtra("notetext", binding.Title.getText().toString());
                i.putExtra("notedisplay", binding.Display.getText().toString());
                i.putExtra("noteid", id);
                i.putExtra("date", date); // Pass the current date
                setResult(RESULT_OK, i);
            } else {
                setResult(RESULT_CANCELED);
                startActivity(new Intent(Datainsert.this, MainActivity.class));
            }

        } else {
            if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(display)) {
                Intent i = new Intent();
                i.putExtra("title", binding.Title.getText().toString());
                i.putExtra("display", binding.Display.getText().toString());
                i.putExtra("date", date); // Pass the current date
                setResult(RESULT_OK, i);
            } else {
                setResult(RESULT_CANCELED);
                startActivity(new Intent(Datainsert.this, MainActivity.class));
            }
        }

        super.onBackPressed();
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return formatter.format(new Date()); // Get the current date
    }


}
