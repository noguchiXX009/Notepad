package com.example.notepad;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class CustomEditText extends AppCompatEditText {

    private int lineNum = 55;
    private int padding = 0;
    private static final int defaultCount = 40;
    private int changeOfLineCount = 20;
    private int heightSet = 1750;

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public CustomEditText(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init();
    }
    public CustomEditText(Context context){
        super(context);
        init();
    }

    private void init(){
        this.setHeight(heightSet);
    };

    @Override
    protected void onDraw(Canvas canvas) {
        this.setBackgroundDrawable(null);

        Layout layout = this.getLayout();
        padding = this.getPaddingBottom();
        Paint mPaint = this.getPaint();

        if(layout.getLineCount() >= defaultCount) {
            lineManager(layout);
        }

        for(int i = 0; i < lineNum; i++){
            canvas.drawLine(0, padding, layout.getWidth(), padding, mPaint);
            padding += this.getPaddingTop() + this.getPaddingBottom() + layout.getTopPadding() + this.getLineSpacingMultiplier();
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTextContextMenuItem(int id){

        switch(id){
            case android.R.id.paste :
            case android.R.id.pasteAsPlainText :
                return super.onTextContextMenuItem(id);
        }
        return super.onTextContextMenuItem(id);
    }

    private void lineExpansion(Layout layout){
        lineNum++;
        heightSet += this.getPaddingTop() + this.getPaddingBottom() + layout.getTopPadding() + this.getLineSpacingMultiplier();
        this.setHeight(heightSet);
    }

    private void lineShrink(Layout layout){
        lineNum--;
        heightSet -= this.getPaddingTop() + this.getPaddingBottom() + layout.getTopPadding() + this.getLineSpacingMultiplier();
        this.setHeight(heightSet);
    }

    private void lineManager(Layout layout){
        if(layout.getLineCount() > changeOfLineCount){
            lineExpansion(layout);
            changeOfLineCount++;
            lineManager(layout);
        } else if (layout.getLineCount() < changeOfLineCount){
            lineShrink(layout);
            changeOfLineCount--;
            lineManager(layout);
        }
    }
}
