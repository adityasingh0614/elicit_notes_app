package com.example.todaybuddy;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todaybuddy.databinding.ListitemRvBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RVadapter extends ListAdapter<notes, RVadapter.Viewholder> {

    private static final DiffUtil.ItemCallback<notes> CALLBACK = new DiffUtil.ItemCallback<notes>() {

        @Override
        public boolean areItemsTheSame(@NonNull notes oldItem, @NonNull notes newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull notes oldItem, @NonNull notes newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDisplayText().equals(newItem.getDisplayText());
        }
    };
    private final Noteviewmodel noteviewmodel;
    List<notes> allnotes;
    List<notes> notes;

    public RVadapter(Noteviewmodel noteviewmodel) {
        super(CALLBACK);
        allnotes = new ArrayList<notes>();
        this.noteviewmodel = noteviewmodel;
    }

    public void search(List<notes> filterdnotes) {
        this.notes = filterdnotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_rv, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        notes note = getItem(position);
        holder.binding.titlerv.setText(note.getTitle());
        holder.binding.displayrv.setText(note.getDisplayText());
        //  String specificDateString = "16-09-2024";


        currentDate();
        Date currentDate = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = formatter.format(currentDate);

// Set the formatted date to the TextView
        holder.binding.notedate.setText(formattedDate);
        int colorcode = getrandomcolor();
        holder.binding.card.setCardBackgroundColor(holder.itemView.getResources().getColor(colorcode, null));

        String transitionName = "transition" + position;
        holder.binding.card.setTransitionName(transitionName);

        //Simple update
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard((Activity) view.getContext());
                Intent intent = new Intent(view.getContext(), Datainsert.class);
                intent.putExtra("type", "update");
                intent.putExtra("noteid", note.getId());
                intent.putExtra("notetext", note.getTitle());
                intent.putExtra("notedisplay", note.getDisplayText());
                intent.putExtra("date", formattedDate);
                ((Activity) view.getContext()).startActivityForResult(intent, 2);

            }
        });
        //Delete
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // pop up menu to deete note
                showpopup(view, note);

                return true;
            }
        });
    }

    private void currentDate() {
    }

    private void showpopup(View view, notes note) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.DELETE) {
                    Toast.makeText(view.getContext(), "deeted", Toast.LENGTH_SHORT).show();
                    noteviewmodel.delete(note);
                    Snackbar snackbar = Snackbar.make(view, note.getTitle() + "Delete", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            noteviewmodel.insert(note);

                        }
                    });
                    snackbar.show();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();

    }


    private int getrandomcolor() {
        List<Integer> random = new ArrayList<>();
        random.add(R.color.c1);
        random.add(R.color.c2);
        random.add(R.color.c3);
        random.add(R.color.c4);
        random.add(R.color.c5);
        random.add(R.color.c6);
        random.add(R.color.c7);
        random.add(R.color.c8);
        random.add(R.color.c9);
        random.add(R.color.c10);
        random.add(R.color.c11);
        random.add(R.color.c12);
        Random Random = new Random();
        //random pick colors
        int num = Random.nextInt(random.size());
        return random.get(num);
    }

    public notes getnote(int position) {

        return getItem(position);
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        ListitemRvBinding binding;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            binding = ListitemRvBinding.bind(itemView);
        }
    }


}
