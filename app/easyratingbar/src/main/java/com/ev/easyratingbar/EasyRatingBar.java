package com.ev.easyratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.math.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * description: EasyRatingBar
 * author: EV
 * created on: 2022/8/4 17:30
 */
public class EasyRatingBar extends View implements View.OnTouchListener {

    public interface OnRatingSeekListener {
        void ratingSeek(float newRate);
    }

    /**
     * 图片质量参数
     */
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    /**
     * 图片
     */
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private OnRatingSeekListener mOnRatingSeekListener;

    private PorterDuffXfermode mDstinMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private PorterDuffXfermode mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private boolean mSlidable;
    private Drawable mRatingDrawable;
    private Bitmap mBm;
    private int mDrawableWidth;
    private int mDrawableMargin;
    private int mProgressColor;
    private int mTintColor;
    private int mMaxCount;
    private float mStep;
    private float mRate;

    public EasyRatingBar(Context context) {
        this(context, null);
    }

    public EasyRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EasyRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.EasyRatingBar);
            mRatingDrawable = array.getDrawable(R.styleable.EasyRatingBar_ratingDrawable);
            mDrawableWidth = array.getDimensionPixelSize(R.styleable.EasyRatingBar_drawableWidth, 0);
            mDrawableMargin = array.getDimensionPixelSize(R.styleable.EasyRatingBar_drawableMargin, 0);
            mProgressColor = array.getColor(R.styleable.EasyRatingBar_progressColor, Color.GREEN);
            mTintColor = array.getColor(R.styleable.EasyRatingBar_tintColor, Color.LTGRAY);
            mMaxCount = array.getInt(R.styleable.EasyRatingBar_maxCount, 5);
            mStep = array.getFloat(R.styleable.EasyRatingBar_step, 0.5f);
            mRate = array.getFloat(R.styleable.EasyRatingBar_rate, 0);
            mSlidable = array.getBoolean(R.styleable.EasyRatingBar_slidable, true);
        }

        setOnTouchListener(mSlidable ? this : null);
    }

    public void setOnRatingSeekListener(OnRatingSeekListener onRatingSeekListener) {
        mOnRatingSeekListener = onRatingSeekListener;
    }

    public void setSlidable(boolean slidable) {
        mSlidable = slidable;
        setOnTouchListener(mSlidable ? this : null);
    }

    public void setRate(float rate) {
        this.mRate = MathUtils.clamp(rate, 0, mMaxCount);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        if (null != mRatingDrawable && mDrawableWidth > 0) {
            width = getPaddingLeft()
                    + getPaddingRight()
                    + mMaxCount * mDrawableWidth
                    + (mMaxCount - 1) * mDrawableMargin;
            height = getPaddingTop()
                    + getPaddingBottom()
                    + mDrawableWidth * mRatingDrawable.getIntrinsicHeight() / mRatingDrawable.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getMeasuredWidth();
            mDrawableWidth = (width - (mMaxCount - 1) * mDrawableMargin - getPaddingLeft() - getPaddingRight()) / mMaxCount;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null);
        } else {
            int flag = Canvas.ALL_SAVE_FLAG;
            canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, flag);
        }

        if (null == mBm) {
            Bitmap bitmap = getBitmapFromDrawable(mRatingDrawable);
            float scale = ((float) mDrawableWidth) / bitmap.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            //bitmap 缩放
            mBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        // 画底部
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mProgressColor);
        float rateWidth = calculateProgress();
        canvas.drawRect(getPaddingLeft(), getPaddingTop(), rateWidth + getPaddingLeft(), getPaddingTop() + mBm.getHeight(), paint);
        paint.setColor(mTintColor);
        canvas.drawRect(rateWidth + getPaddingLeft(), getPaddingTop(), canvas.getWidth() - getPaddingRight(), getPaddingTop() + mBm.getHeight(), paint);

        //draw 上去
        int left = getPaddingLeft();
        for (int i = 0; i < mMaxCount; i++) {
            if (i > 0) {
                paint.setXfermode(mClearMode);
                canvas.drawRect(left, getPaddingTop(), left + mDrawableMargin, getPaddingTop() + mBm.getHeight(), paint);
                left += mDrawableMargin;
            }
            paint.setXfermode(mDstinMode);
            canvas.drawBitmap(mBm, left, getPaddingTop(), paint);
            left += mDrawableWidth;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float newRate = calculateRate(event.getX());
                if (mRate != newRate) {
                    mRate = newRate;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (null != mOnRatingSeekListener) {
                    mOnRatingSeekListener.ratingSeek(getRoundRate());
                }
                break;
            default:
                break;
        }
        return true;
    }

    private int calculateProgress() {
        int progress = 0;
        int rate = (int) Math.floor(mRate);
        float decimals = mRate - rate;
        while (rate > 0) {
            if (progress > 0) {
                progress += mDrawableMargin;
            }
            progress += mDrawableWidth;
            rate--;
        }

        if (decimals >= mStep) {
            progress += mDrawableMargin;
            float stepProgress = mStep * mDrawableWidth;
            while (decimals >= mStep) {
                progress += stepProgress;
                decimals -= mStep;
            }
        }
        return progress;
    }

    private float calculateRate(float x) {
        float newRate = 0;
        if (x > getPaddingLeft()) {
            x -= getPaddingLeft();
            while (x >= mDrawableWidth) {
                newRate++;
                x -= (mDrawableWidth + mDrawableMargin);
            }
            float stepProgress = mStep * mDrawableWidth;
            newRate += ((int) (x / stepProgress)) * mStep;
        }
        newRate = MathUtils.clamp(newRate, 0, mMaxCount);
        return newRate;
    }

    private float getRoundRate() {
        String str = String.valueOf(mStep);
        int scale = 1;
        if (str.contains(".")) {
            scale = str.length() - 1 - str.indexOf(".");
        }
        BigDecimal bd = new BigDecimal(mRate).setScale(scale, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(getWidth(), drawable.getBounds().height(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}