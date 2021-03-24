package com.example.notepad;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public MyAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        TextView dateTextView;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleOfMemo);
            dateTextView = itemView.findViewById(R.id.dateOfMemo);
            linearLayout = itemView.findViewById(R.id.linearLayout_te);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        final String title = mCursor.getString(mCursor.getColumnIndex(MemoContract.MemoEntry.COLUMN_TITLE));
        final String date = mCursor.getString(mCursor.getColumnIndex(MemoContract.MemoEntry.TIME_STUMP));
        final int id = mCursor.getInt(mCursor.getColumnIndex(MemoContract.MemoEntry._ID));
        final String text = mCursor.getString(mCursor.getColumnIndex(MemoContract.MemoEntry.COLUMN_TEXT));

        holder.titleTextView.setText(title);
        holder.dateTextView.setText(date);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicklistener(v, position, id, title, text, date);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onItemLongClicklistener(v, position, id, title, text, date);
                return true;
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }

    interface OnItemClickListener{
        void onItemClicklistener(View view, int position, int id, String clicketTitle, String clickedText, String clicketDate);
    }

    interface OnItemLongClickListener{
        void onItemLongClicklistener(View view, int position, int id, String clicketTitle, String clickedText, String clicketDate);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;
        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

    public int getNewColumnId(){
        mCursor.moveToFirst();
        return mCursor.getInt(mCursor.getColumnIndex(MemoContract.MemoEntry._ID));
    }
}
