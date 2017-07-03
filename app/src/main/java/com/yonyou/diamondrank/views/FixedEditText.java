package com.yonyou.diamondrank.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.yonyou.diamondrank.R;


/**
 * Created by libo on 17/1/23.
 */
public class FixedEditText extends EditText {
    private String fixedText;
    private View.OnClickListener mListener;
    private Paint paint;

    public FixedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }
    public FixedEditText(Context context,AttributeSet set,int defStyle){
        super(context,set,defStyle);
        initView(context,set);
    }

    private void initView(Context context, AttributeSet attrs){
        if (attrs != null){
            paint = new Paint();
            paint.setTextSize(getTextSize());
            paint.setColor(getResources().getColor(R.color.label_tips_color));
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedEditText);
            fixedText = a.getString(R.styleable.FixedEditText_fixedText);
        }else {
            fixedText = "我也不知道";
        }
    }
    public void setFixedText(String text) {
        fixedText = text;
        int left = (int) getPaint().measureText(fixedText)+ getPaddingLeft();
        setPadding(left, getPaddingTop(), getPaddingBottom(), getPaddingRight());
        invalidate();
    }

    public void setDrawableClk(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(fixedText)) {
            canvas.drawText(fixedText, 0, (getMeasuredHeight() - getTextSize()) / 2 + getTextSize() -3, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListener != null && getCompoundDrawables()[2] != null) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int i = getMeasuredWidth() - getCompoundDrawables()[2].getIntrinsicWidth();
                    if (event.getX() > i) {
                        mListener.onClick(this);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
                default:
                    break;
            }

        }
        return super.onTouchEvent(event);

    }
}
