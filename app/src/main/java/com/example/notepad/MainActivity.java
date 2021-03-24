package com.example.notepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AddToSql sql;
    private SQLiteDatabase db;

    protected MyAdapter adapter;

    private static MainActivity instance = null;

    TextView plusTextView;
    String s;
    EditText testEdit;

    public boolean beginnerFrag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("MyNotepad");
        toolbar.setTitleTextColor(Color.BLACK);

        sql = new AddToSql(this);
        db = sql.getWritableDatabase();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        adapter = new MyAdapter(this, getAllItems());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(
                new MyAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClicklistener(View view, int position, int id, String clicketTitle, String clickedText, String clicketDate) {
                        Intent intent = new Intent(getApplicationContext(), EditingMemoActivity.class);
                        intent.putExtra("UPDATE_FRAG", true).putExtra("ID", id).putExtra("TITLE", clicketTitle)
                                .putExtra("TEXT",clickedText).putExtra("DATE", clicketDate);
                        startActivity(intent);
                    }
                }
        );
        adapter.setLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClicklistener(View view, int position, final int id, String clicketTitle, String clickedText, String clicketDate) {
                deleteDialog(id);
            }
        });

        plusTextView = findViewById(R.id.plusTextView);
        plusTextView.setVisibility(View.INVISIBLE);

        if(adapter.getItemCount() == 0){
            beginnerFrag = true;
            plusTextView.setVisibility(View.VISIBLE);
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(10000);
            animation.setInterpolator(new CycleInterpolator(5));
            plusTextView.setVisibility(View.VISIBLE);
            plusTextView.startAnimation(animation);
            plusTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item != null) {
            switch (item.getItemId()) {
                case R.id.add :
                    Intent intent = new Intent(getApplicationContext(), EditingMemoActivity.class);
                    intent.putExtra("UPDATE_FRAG", false).putExtra("BEGINNER", beginnerFrag);
                    startActivity(intent);
                    return true;

                case R.id.initiatation :
                    deleteDialog();
                    return true;

                case R.id.credit :
                    return true;

                case R.id.usage :
                    Intent usageIntent = new Intent(this, UsageActivity.class);
                    startActivity(usageIntent);
                    return true;

                case R.id.updater :
                    adapter.swapCursor(getAllItems());
                    return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor getAllItems(){
        try {
            return db.query(MemoContract.MemoEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    MemoContract.MemoEntry.TIME_STUMP + " DESC"
            );
        } catch (Exception e){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("注意");
            alertDialog.setMessage(e.getMessage());
            alertDialog.create();
            alertDialog.show();
            return null;
        }
    }

    public void deleteAll(){
        db.delete(
                MemoContract.MemoEntry.TABLE_NAME,
                null,
                null
        );
        adapter.swapCursor(getAllItems());
    }

    public void deleteSelected(int id){
        db.delete(
                MemoContract.MemoEntry.TABLE_NAME,
                MemoContract.MemoEntry._ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    private void deleteDialog(final int id){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("注意");
        alertDialog.setMessage("選択されたメモを\n削除しますか");
        alertDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteSelected(id);
                    adapter.swapCursor(getAllItems());
                } catch (Exception e){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("注意");
                    alertDialog.setMessage(e.getMessage());
                    alertDialog.create();
                    alertDialog.show();
                }
            }
        });
        alertDialog.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void deleteDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("注意");
        alertDialog.setMessage("記事をすべて\n削除しますか");
        alertDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteAll();
                    adapter.swapCursor(getAllItems());
                } catch (Exception e){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("注意");
                    alertDialog.setMessage(e.getMessage());
                    alertDialog.create();
                    alertDialog.show();
                }
            }
        });
        alertDialog.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    public static MainActivity getInstance(){
        return instance;
    }
}
