package com.example.todaybuddy;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class Noteviewmodel extends AndroidViewModel {
    public LiveData<List<notes>> notelist;
    Notesrepositor notesrepositor;

    public Noteviewmodel(@NonNull Application application) {
        super(application);
        notesrepositor = new Notesrepositor(application);
        notelist = notesrepositor.fetchAll();

    }

    public void insert(notes notes) {
        notesrepositor.insert(notes);
    }

    public void update(notes notes) {
        notesrepositor.update(notes);
    }

    public void delete(notes notes) {
        notesrepositor.deleteNote(notes);
    }

    public LiveData<List<notes>> fetchAll() {
        return notelist;
    }

}
