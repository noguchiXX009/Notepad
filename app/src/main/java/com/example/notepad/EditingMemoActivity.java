package com.example.notepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.CycleInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EditingMemoActivity extends AppCompatActivity {

    private AddToSql sql;
    private SQLiteDatabase db;

    private EditText text;
    private EditText title;
    private TextView saveTextview;
    private boolean updateIs;
    private int updateID;

    private MainActivity mainActivity = null;

    private boolean saveIs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_memo);

        mainActivity = MainActivity.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        toolbar.setTitle("MyNotepad");
        toolbar.setTitleTextColor(Color.BLACK);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateIs = getIntent().getBooleanExtra("UPDATE_FRAG", false);
        updateID = getIntent().getIntExtra("ID", 0);

        sql = new AddToSql(this);
        db = sql.getWritableDatabase();

        title = findViewById(R.id.titleEdit);
        text = findViewById(R.id.customEditText);
        title.setText(getIntent().getStringExtra("TITLE"));
        text.setText(getIntent().getStringExtra("TEXT"));

    }

    private void saveDate(){
        Date date = new Date();
        TimeZone timeZoneJP = TimeZone.getTimeZone("Asia/Tokyo");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setTimeZone(timeZoneJP);
        String timeStump = fmt.format(date);

        ContentValues values = new ContentValues();
        values.put(MemoContract.MemoEntry.COLUMN_TITLE, title.getText().toString());
        values.put(MemoContract.MemoEntry.COLUMN_TEXT, text.getText().toString());
        values.put(MemoContract.MemoEntry.TIME_STUMP, timeStump);

        try {
            db.insert(MemoContract.MemoEntry.TABLE_NAME, null, values);
            Toast.makeText(EditingMemoActivity.this, "保存しました", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditingMemoActivity.this);
            alertDialog.setTitle("注意");
            alertDialog.setMessage(e.getMessage());
            alertDialog.create();
            alertDialog.show();
        }
    }

    private void upDate(){
        Date date = new Date();
        TimeZone timeZoneJP = TimeZone.getTimeZone("Asia/Tokyo");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setTimeZone(timeZoneJP);
        String timeStump = fmt.format(date);

        ContentValues values = new ContentValues();
        values.put(MemoContract.MemoEntry.COLUMN_TITLE, title.getText().toString());
        values.put(MemoContract.MemoEntry.COLUMN_TEXT, text.getText().toString());
        values.put(MemoContract.MemoEntry.TIME_STUMP, timeStump);
        try {
            db.update(MemoContract.MemoEntry.TABLE_NAME, values, MemoContract.MemoEntry._ID + " = " + String.valueOf(updateID), null);
            Toast.makeText(EditingMemoActivity.this, "保存しました", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditingMemoActivity.this);
            alertDialog.setTitle("注意");
            alertDialog.setMessage(e.getMessage());
            alertDialog.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item != null) {
            switch (item.getItemId()) {
                case R.id.save :
                    if(updateIs){
                        upDate();
                        mainActivity.adapter.swapCursor(getAllItems());
                    } else {
                        saveDate();
                        updateIs = !updateIs;
                        mainActivity.adapter.swapCursor(getAllItems());
                        updateID = mainActivity.adapter.getNewColumnId();
                    }
                    saveIs = true;
                    return true;

                case R.id.editRule :
                    return true;

                case R.id.editusage :
                    Intent intent = new Intent(this, UsageActivity.class);
                    startActivity(intent);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed(){
        if(saveIs) {
            saveIs = false;
            super.onBackPressed();
        } else {
            saveAnotation();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK :
                    if(saveIs) {
                        saveIs = false;
                        return super.dispatchKeyEvent(event);
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditingMemoActivity.this);
                        alertDialog.setTitle("注意");
                        alertDialog.setMessage("セーブが完了していません");
                        alertDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialog.create();
                        alertDialog.show();
                        return true;
                    }
            }
        }
        return super.dispatchKeyEvent(event);
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditingMemoActivity.this);
            alertDialog.setTitle("注意");
            alertDialog.setMessage(e.getMessage());
            alertDialog.create();
            alertDialog.show();
            return null;
        }
    }

    private void saveAnotation(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditingMemoActivity.this);
        alertDialog.setTitle("注意");
        alertDialog.setMessage("セーブが完了していません\n終了しますか");
        alertDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveIs = true;
                onBackPressed();
            }
        });
        alertDialog.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveIs = false;
            }
        });
        alertDialog.create();
        alertDialog.show();
    }
}