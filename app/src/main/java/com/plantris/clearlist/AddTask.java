package com.plantris.clearlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddTask extends RecyclerView.Adapter<AddTask.ViewHolder> {

    private ArrayList<TodoItem> todoList;

    public AddTask(ArrayList<TodoItem> todoList) {
        this.todoList = todoList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxDone);
            textView = itemView.findViewById(R.id.textViewTask);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodoItem item = todoList.get(position);
        holder.textView.setText(item.text);
        holder.checkBox.setChecked(item.done);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }
}
