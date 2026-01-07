package com.example.taskmanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementsystem.R;
import com.example.taskmanagementsystem.model.TaskList;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvTitle;
        public TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnLongClickListener(this); //register long click action listener
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    } // close ViewHolder class

    //////////////////////////////////////////////////////////////////////
    // adapter class definitions

    private List<TaskList> taskListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;

    public ListAdapter(Context context, List<TaskList> listData) {
        taskListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout
        View view = inflater.inflate(R.layout.task_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        TaskList m = taskListData.get(position);
        holder.tvTitle.setText(m.getTitle());
        holder.tvDescription.setText(m.getDescription());
    }

    @Override
    public int getItemCount() {
        return taskListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public TaskList getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && taskListData !=null && currentPos<taskListData.size()) {
            return taskListData.get(currentPos);
        }
        return null;
    }
}