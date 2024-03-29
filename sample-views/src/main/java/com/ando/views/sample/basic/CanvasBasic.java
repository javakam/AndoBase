package com.ando.views.sample.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ando.views.sample.R;
import com.ando.views.utils.UIUtils;

/**
 * Title: LineView
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/11  10:52
 */
public class CanvasBasic extends View {
    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint mPathPaint;

    public CanvasBasic(Context context) {
        this(context, null,0);
    }

    public CanvasBasic(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CanvasBasic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.setBackgroundColor(Color.parseColor("#E1FFFF"));
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setDither(true);
        mLinePaint.setSubpixelText(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.MAGENTA);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(UIUtils.getDimens(context,R.dimen.font_24));
        mTextPaint.setSubpixelText(true);

        mPathPaint = new Paint();
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setColor(Color.BLUE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0, 300, 60, mLinePaint);
        canvas.drawText("早上好 GoodMorning!", 100, 200, mTextPaint);

        //Path
        Path path = new Path();
        path.lineTo(100, 100);
        path.rLineTo(50, 0);
        path.arcTo(150, 100, 230, 230, -90, 90, false);
        //前提-》paint.setStyle(Style.STROKE); FILL 或 FILL_AND_STROKE，Path 会自动封闭子图形
        //lineTo() arcTo() 等方法的时候，则是每一次断线（即每一次「抬笔」），都标志着一个子图形的结束，以及一个新的子图形的开始
        path.close();
        canvas.drawPath(path, mPathPaint);

    }
}