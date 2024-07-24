package com.example.todaybuddy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.todaybuddy.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final boolean isScrolling = false;
    ActivityMainBinding binding;
    FirebaseAuth auth;
    private Noteviewmodel noteviewmodel;
    private List<notes> itemlsit = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        config();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //User Name
        String userName = getIntent().getStringExtra("username");
        if (userName != null) {
            Log.d("MainActivity", "Username received: " + userName);

            binding.apptitle.setText("Hi" + userName);
        } else {
            binding.apptitle.setText("My Notes");
        }


        noteviewmodel = new ViewModelProvider(this, ViewModelProvider
                .AndroidViewModelFactory.getInstance((Application) this.getApplicationContext()))
                .get(Noteviewmodel.class);

        binding.searchView.clearFocus();
        //UserName;
        // Searching logic
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterlist(newText);
                return false;
            }
        });

        // Fab scrolling effects
        binding.RV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    binding.Fabtext.setVisibility(View.GONE);
                } else if (scrollX == scrollY) {
                    binding.Fabtext.setVisibility(View.VISIBLE);
                } else {
                    binding.Fabtext.setVisibility(View.VISIBLE);
                }
            }
        });

        // To get focus on search view when it's clicked
        binding.searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    binding.searchView.setIconified(false); // Open the SearchView when clicked
                    return true;
                }
                return false;
            }
        });

        // Insert button
        binding.floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Datainsert.class);
            intent.putExtra("type", "ADD");
            startActivityForResult(intent, 1);
        });

        // Staggered grid layout
        binding.RV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.RV.setHasFixedSize(true);
        RVadapter adapter = new RVadapter(noteviewmodel);
        binding.RV.setAdapter(adapter);
        itemlsit = new ArrayList<>();
        noteviewmodel.fetchAll().observe(this, new Observer<List<notes>>() {
            @Override
            public void onChanged(List<notes> notes) {
                adapter.submitList(notes);
                binding.RV.scrollToPosition(0);
                itemlsit = notes;
            }
        });

        binding.fablinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Datainsert.class);
                intent.putExtra("type", "ADD");
                startActivityForResult(intent, 1);
            }
        });

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.sin_out) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, Login_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        // Swipe features
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition(); // gets position of adapter
                notes notes = adapter.getnote(position); // gets position of note
                if (direction == ItemTouchHelper.LEFT) {
                    snackbar(notes);
                } else {
                    snackbar(notes);
                }
            }
        }).attachToRecyclerView(binding.RV);
    }

    private void config() {
        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementsUseOverlay(false);
    }

    private void snackbar(notes notes) {
        noteviewmodel.delete(notes);
        Snackbar snackbar = Snackbar.make(binding.getRoot(), notes.getTitle() + " deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteviewmodel.insert(notes);
            }
        });
        snackbar.show();
    }

    // searching
    private void filterlist(String newText) {
        List<notes> filterlist = new ArrayList<>();
        String query = newText.toLowerCase(); // Convert query to lowercase
        for (notes note : itemlsit) {
            String title = note.getTitle().toLowerCase(); // Convert title to lowercase
            String display = note.getDisplayText().toLowerCase();

            if (title.contains(query) || display.contains(query)) {
                filterlist.add(note);
            }
        }
        RVadapter adapter = (RVadapter) binding.RV.getAdapter(); // Get the existing adapter
        adapter.submitList(filterlist); // Update the adapter with filtered data
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String display = data.getStringExtra("display");
            String date = data.getStringExtra("date");
            notes notes = new notes(title, display, date);
            noteviewmodel.insert(notes);
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("notetext");
            String display = data.getStringExtra("notedisplay");
            String date = data.getStringExtra("date");
            notes notes = new notes(title, display, date);
            notes.setId(data.getIntExtra("noteid", 0));
            noteviewmodel.update(notes);
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_signout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sin_out) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, Login_Activity.class));
            finish();
            return true; // Indicate that the menu item selection has been handled
        } else {
            return super.onOptionsItemSelected(item); // Propagate the return value of the superclass
        }
    }


}