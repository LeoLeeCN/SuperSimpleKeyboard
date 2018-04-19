package net.karlitos.supersimplekeyboard;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Toolbar extends LinearLayout {

    EditText mSearchBox;

    public Toolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean searchBoxFocused(){
        return this.hasFocus();
    }

    public void appendText(String str){
        mSearchBox.getText().append(str);
    }

    public void deleteLast(){
        try {
            mSearchBox.getText().delete(mSearchBox.getText().length() - 1, mSearchBox.getText().length());
        } catch (Exception e){

        }
    }

    public String getText(){
        return mSearchBox.getText().toString();
    }
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mSearchBox = findViewById(R.id.search_box);
    }
}
