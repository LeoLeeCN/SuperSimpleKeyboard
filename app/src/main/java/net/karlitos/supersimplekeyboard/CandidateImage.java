package net.karlitos.supersimplekeyboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CandidateImage extends LinearLayout {
    ImageView candidateImage;
    public CandidateImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImage(Bitmap bmp){
        candidateImage.setImageBitmap(bmp);
    }

    public void setOnImageClickListener(OnClickListener listener){
        candidateImage.setOnClickListener(listener);
    }
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        candidateImage = findViewById(R.id.candidate_image);
    }
}
