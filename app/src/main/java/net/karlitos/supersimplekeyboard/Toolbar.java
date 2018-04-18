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
        mSearchBox.setText(mSearchBox.getText()+str);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mSearchBox = findViewById(R.id.search_box);
    }
}