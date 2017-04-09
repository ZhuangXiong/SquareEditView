package com.xemenes.library.squareeditview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author： xemenes
 * time：17/4/2
 * email：xionggezhuang@gmail.com
 * GET BUSY LIVING OR GET BUSY DYING .
 */

public class SquareEditView extends View {

    //输入框正在输入的样式
    final int SQUARESELECTMODEL_SELECTHAVEINPUT = 2;
    final int SQUARESELECTMODEL_SELECTCURRENT = 1;
    final int SQUARESELECTMODEL_NORMAL = 0;
    //输入框的字符样式
    final int SQUARETEXTCHARMODEL_PASSWORD = 1;
    final int SQUARETEXTCHARMODEL_NORMAL = 0;

    //方框颜色
    int squareColor = 0;
    //显示字体颜色
    int squareTextColor = 0;
    //显示字体大小
    float squareTextSize = 0;
    //当输入模式不为不同模式时，方框改变的颜色
    int squareSelectColor = 0;
    //方框的间距
    int squarePadding = 0;
    //方格个数
    int squareCount = 0;
    int mRoundRadius = 0;

    int squareTextCharModel = 0;
    int squareSelectModel = 0;

    int layoutWidth, layoutHeight;
    int defalutSquareSize = 60;
    //默认密码样式，圆点半径大小
    int dotRadius;
    int squreWith = 0;

    Paint mSquarePaint;
    Paint mTextPaint;
    List<RectF> rectFs;
    List<String> contents;
    int fixPadding = 2;

    InputMethodManager inputManager;
    OnInputFinishListener onInputfinishaListener;

    public SquareEditView(Context context) {
        super(context);
    }

    public SquareEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareEditView);
        squareSelectModel = typedArray.getInt(R.styleable.SquareEditView_squareSelectModel, 0);
        squareTextCharModel = typedArray.getInt(R.styleable.SquareEditView_squareTextCharModel, 0);
        squareColor = typedArray.getColor(R.styleable.SquareEditView_squareColor, Color.BLACK);
        squareTextColor = typedArray.getColor(R.styleable.SquareEditView_squareTextColor, Color.GRAY);
        squareTextSize = typedArray.getDimension(R.styleable.SquareEditView_squareTextSize, 15);
        squarePadding = (int) typedArray.getDimension(R.styleable.SquareEditView_squarePadding, 0);
        squareSelectColor = typedArray.getColor(R.styleable.SquareEditView_squareSelectColor, Color.BLACK);
        squareCount = typedArray.getInteger(R.styleable.SquareEditView_squareCount, 6);
        typedArray.recycle();

        defalutSquareSize = (int) (density * 50);
        mSquarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSquarePaint.setStrokeWidth(1);
        mSquarePaint.setStyle(Paint.Style.STROKE);
        mSquarePaint.setColor(squareColor);
        mSquarePaint.setAntiAlias(true);


        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(squareTextColor);
        mTextPaint.setAntiAlias(true);

        dotRadius = (int) squareTextSize;
        rectFs = new ArrayList<>();
        contents = new ArrayList<>();

        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        this.setOnKeyListener(new SquareOnKeyListenner());
    }

    public void clearContents() {

        contents.clear();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getLayoutMeasure(heightMeasureSpec);
        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    private void getLayoutMeasure(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

        //wrap_content
        if (specMode == MeasureSpec.AT_MOST || specMode == MeasureSpec.UNSPECIFIED) {
            layoutWidth = squareCount * (defalutSquareSize + squarePadding);
            layoutHeight = defalutSquareSize;

        } else if (specMode == MeasureSpec.EXACTLY) {

            layoutWidth = squareCount * (specSize + squarePadding);
            layoutHeight = specSize;

        }

        squreWith = layoutHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawWithOutPadding(canvas);
        drawSquareText(canvas);
    }

    private void drawWithPadding(Canvas canvas) {

        for (int i = 0; i < squareCount; i++) {
            RectF rectF = new RectF();
        }
    }

    private void drawWithOutPadding(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.set(fixPadding, fixPadding, layoutWidth - fixPadding, layoutHeight - fixPadding);
        canvas.drawRoundRect(rectF, mRoundRadius, mRoundRadius, mSquarePaint);

        for (int i = 0; i < squareCount; i++) {

            int x = i * squreWith;
            canvas.drawLine(x, 0, x, layoutHeight - fixPadding, mSquarePaint);
        }
    }

    /**
     * 填充文字
     */
    private void drawSquareText(Canvas canvas) {
        for (int i = 0; i < contents.size(); i++) {
            float x = (float) (squreWith * (i + 0.5));
            float y = (float) (squreWith * 0.5);
            canvas.drawCircle(x, y, dotRadius, mTextPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            requestFocus();
            inputManager.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            return true;
        }
        return super.onTouchEvent(event);

    }

    /**
     * 失去焦点隐藏 键盘
     *
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            inputManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            inputManager.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        }else {
            inputManager.hideSoftInputFromInputMethod(this.getWindowToken(),0);
        }
    }

    /**
     * 接受键盘输入
     *
     * @return
     */
    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    /**
     * 建立输入法的输入格式
     *
     * @param outAttrs
     * @return
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return new SquareInputConnection(this, false);
    }


    class SquareOnKeyListenner implements OnKeyListener {

        /**
         * 监听键盘输入
         *
         * @param view
         * @param keyCode
         * @param keyEvent
         * @return
         */
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                if (KeyEvent.KEYCODE_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_9) {
                    if (contents.size() <= squareCount) {
                        contents.add(String.valueOf(keyCode));
                        invalidate();
                        isFinishInput();
                    }

                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (!contents.isEmpty()) {
                        contents.remove(contents.size() - 1);
                        invalidate();
                    }

                    return true;
                }

            }
            return false;
        }

        /**
         * 是否输入完成
         */

        protected void isFinishInput() {
            if (contents.size() == squareCount && onInputfinishaListener != null) {
                StringBuffer buffer = new StringBuffer();
                for (String s : contents) {
                    buffer.append(s);
                }
                onInputfinishaListener.setFinishInputListener(buffer.toString());
            }
        }
    }

    public interface OnInputFinishListener {
        public void setFinishInputListener(String content);
    }

    class SquareInputConnection extends BaseInputConnection {

        public SquareInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        /**
         * Default implementation replaces any existing composing text with the given text.
         *
         * @param text
         * @param newCursorPosition
         * @return
         */
        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            return super.commitText(text, newCursorPosition);
        }

        /**
         * 删除范围内的文本
         * The default implementation performs the deletion around the current selection position of the editable text.
         *
         * @param beforeLength
         * @param afterLength
         * @return
         */
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            //发送删除事件
            if (beforeLength == 1 && afterLength == 0) {
                return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }
}
