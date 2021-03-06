package com.yonyou.diamondrank.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.yonyou.diamondrank.R;


/**
 * Created by libo on 16/7/21.
 */
public class SelectButton extends Button {

    private int drawableWidth;
    private int iconPadding;
    private DrawablePosition position;

    Rect bounds;

    private  enum  DrawablePosition{

        NONE,
        LEFT_AND_RIGHT,
        LEFT,
        RIGHT

    }



    public SelectButton(Context context) {

        this(context,null,0);
    }

    public SelectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        bounds = new Rect();
    }

    public SelectButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bounds = new Rect();

    }

    protected void applyAttributes(AttributeSet attrs){

        if (null == bounds){

            bounds = new Rect();
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconButton);
        int paddingId = typedArray.getDimensionPixelOffset(R.styleable.IconButton_iconPadding,0);
        setIconPadding(paddingId);
        typedArray.recycle();
    }


    public void setIconPadding(int padding){
        iconPadding = padding;
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


        Paint textPaint = getPaint();
        String text = getText().toString();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        int textWidth = bounds.width();
        int factor = (position == DrawablePosition.LEFT) ? 2 : 1;
        int contentWidth = drawableWidth + iconPadding * factor + textWidth;
        int horizontalPadding = (int) ((getWidth()  ) - (contentWidth + getPaddingLeft() + 50 ));

        setCompoundDrawablePadding(horizontalPadding - iconPadding);

        switch (position) {
            case LEFT:
                setPadding(horizontalPadding, getPaddingTop(), 0, getPaddingBottom());
                break;

            case RIGHT:
                setPadding(0, getPaddingTop(), 20, getPaddingBottom());
                break;

            case LEFT_AND_RIGHT:
                setPadding(horizontalPadding, getPaddingTop(), horizontalPadding, getPaddingBottom());
                break;

            default:
                setPadding(0, getPaddingTop(), 0, getPaddingBottom());
        }
    }


    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);

        if (left != null && right != null) {
            drawableWidth = left.getIntrinsicWidth() + right.getIntrinsicWidth();
            position = DrawablePosition.LEFT_AND_RIGHT;
        } else if (left != null) {
            drawableWidth = left.getIntrinsicWidth();
            position = DrawablePosition.LEFT;
        } else if (right != null) {
            drawableWidth = right.getIntrinsicWidth();
            position = DrawablePosition.RIGHT;
        } else {
            position = DrawablePosition.NONE;
        }


        requestLayout();
    }


}
