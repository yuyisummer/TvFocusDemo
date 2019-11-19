package com.signway.uidemo.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * wifi设置用的小键盘
 */
public class UIKeyBoardView extends View {

    private static String TAG = "UIKeyBoard";

    private View subView = null;

    private String[][] letters;

    private static String[][] smallLetters = {
            {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
            {"a", "s", "d", "f", "g", "h", "j", "k", "l"},
            {"ABC", "z", "x", "c", "v", "b", "n", "m", "del"},
            {"clear", "123", "space", "!@#", "back"},};
    private static String[][] capitalLetters = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"abc", "Z", "X", "C", "V", "B", "N", "M", "del"},
            {"clear", "123", "space", "!@#", "back"},};

    private static String[][] upletters = {
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"},
            {"/", "@", "#", "$", "%", "^", "&", "*", "`"},
            {"", "(", ")", "-", "_", "'", ";", ":", ""},
            {"", "", "", "", ""},};

    private String[][] numbers = {
            {"+", "1", "2", "3", "del"},
            {"-", "4", "5", "6", "abc"},
            {"*", "7", "8", "9", "!@#"},
            {"/", "=", "0", ".", "back"}};

    private static String[][] symbol = {
            {";", "\"", "%", "$", "[", "(", ")", "]", "del"},
            {"，", "?", "+", "=", "-", "/", "\\", "_", "123"},
            {"~", "^", ":", "*", "&", "{", "}", ".", "abc"},
            {"#", "@", "'", "`", "<", "!", "|", ">", "back"},};


    private KeyCell[][] letterCells = new KeyCell[4][10];
    private KeyCell[][] numCells = new KeyCell[4][5];
    private KeyCell[][] symbolCells = new KeyCell[4][9];
    //private KeyCell[][]

    public static final int TYPE_LETTER_SMALL = 0;
    public static final int TYPE_LETTER_CAPITAL = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_SYMBOL = 3;

    private OnCellTouchListener mOnCellTouchListener = null;

    private int type;

    /**
     * 划分成等份之后的行高
     **/
    private float rowHeight;
    /**
     * 行宽
     **/
    private float rowWidth;
    /**
     * 每个按钮的宽度
     **/
    private float cellWidth;
    /**
     * 每个按钮的高度
     **/
    private float cellHeight;

    /**
     * 按钮之间间隔
     **/
    private float CELL_MARGIN_LEFT = 5;//15;
    /**
     * 每行中上下的间隔
     **/
    private float ROW_PADPING_TOP = 8;
    /**
     * 视图的左右边距
     **/
    private float MARGIN_LEFT = 5;//50;
    /**
     * 视图的上下边距
     **/
    private float MARGIN_TOP = 30;
    private KeyCell[][] currentCells;

    public View getSubView() {
        return subView;
    }

    public void setSubView(View subView) {
        this.subView = subView;
    }

    public UIKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.v(TAG, "Begin  UIKeyBoard");

        type = TYPE_LETTER_SMALL;
        letters = smallLetters;

        // 控制键盘获得焦点
        this.setFocusable(true);

        // 设置在popupwindow 中按键响应
        setFocusableInTouchMode(true);
    }

    /**
     * 设置键盘类型并刷新
     *
     * @param _type
     */
    public void setType(int _type) {
        currentCells = null;
        type = _type;
        if (type == TYPE_LETTER_SMALL) {
            letters = smallLetters;
            cellWidth = (rowWidth - CELL_MARGIN_LEFT * 9) / 10;
            cellHeight = rowHeight - 2 * ROW_PADPING_TOP;
        } else if (type == TYPE_LETTER_CAPITAL) {
            letters = capitalLetters;
            cellWidth = (rowWidth - CELL_MARGIN_LEFT * 9) / 10;
            cellHeight = rowHeight - 2 * ROW_PADPING_TOP;
        } else if (type == TYPE_NUMBER) {
            letters = numbers;
            cellWidth = (rowWidth - CELL_MARGIN_LEFT * 4) / 8;
            cellHeight = rowHeight - 2 * ROW_PADPING_TOP;
        } else if (type == TYPE_SYMBOL) {
            letters = symbol;
            cellWidth = (rowWidth - CELL_MARGIN_LEFT * 8) / 9.5f;
            cellHeight = rowHeight - 2 * ROW_PADPING_TOP;
        }
        Log.v(TAG, "setType " + type);
        clearFocus();
        initCells();
        invalidate();

        Log.v(TAG, "setType= " + type);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right,
                         int bottom) {
        rowHeight = ((bottom - top) - 2 * MARGIN_TOP) / 4;
        rowWidth = (right - left) - 2 * MARGIN_LEFT;

        if (type == TYPE_LETTER_SMALL || type == TYPE_LETTER_CAPITAL) {
            cellWidth = (rowWidth - CELL_MARGIN_LEFT * 9) / 10;
            cellHeight = rowHeight - 2 * ROW_PADPING_TOP;
        } else if (type == TYPE_NUMBER) {
            cellWidth = (rowWidth - CELL_MARGIN_LEFT * 4) / 8;
            cellHeight = rowHeight - 2 * ROW_PADPING_TOP;
        } else if (type == TYPE_SYMBOL) {
            cellWidth = (rowWidth - CELL_MARGIN_LEFT * 8) / 9.5f;
            cellHeight = rowHeight - 2 * ROW_PADPING_TOP;
        }

        initCells();

        Log.v(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 初始化键盘布局
     */
    private void initCells() {
        RectF Bound;
        switch (type) {
            case TYPE_LETTER_SMALL:
            case TYPE_LETTER_CAPITAL:
                Bound = new RectF(MARGIN_LEFT, MARGIN_TOP + ROW_PADPING_TOP,
                        cellWidth + MARGIN_LEFT, MARGIN_TOP + cellHeight
                        + ROW_PADPING_TOP);
                for (int row = 0; row < letters.length; row++) {
                    switch (row) {
                        case 0:
                            for (int j = 0; j < letters[row].length; j++) {
                                letterCells[row][j] = new KeyCell(letters[row][j], new RectF(Bound));
                                Bound.offset(cellWidth + CELL_MARGIN_LEFT, 0);
                            }
                            break;
                        case 1:
                            Bound.offset(cellWidth / 2 + CELL_MARGIN_LEFT / 2, 0);
                            for (int j = 0; j < letters[row].length; j++) {
                                letterCells[row][j] = new KeyCell(letters[row][j], new RectF(Bound));

                                Bound.offset(cellWidth + CELL_MARGIN_LEFT, 0);
                            }
                            break;
                        case 2:
                            Bound.right = Bound.right + cellWidth / 2;
                            letterCells[row][0] = new KeyCell(letters[row][0],
                                    new RectF(Bound));
                            Bound.left = MARGIN_LEFT + cellWidth / 2 * 3
                                    + CELL_MARGIN_LEFT / 2 * 3;
                            Bound.right = Bound.left + cellWidth;
                            for (int j = 1; j < letters[row].length - 1; j++) {
                                letterCells[row][j] = new KeyCell(letters[row][j], new RectF(Bound));

                                Bound.offset(cellWidth + CELL_MARGIN_LEFT, 0);
                            }
                            Bound.right = MARGIN_LEFT + rowWidth;
                            Bound.left = Bound.right - cellWidth / 2 * 3;
                            letterCells[row][letters[row].length - 1] = new KeyCell(
                                    letters[row][letters[row].length - 1], new RectF(
                                    Bound));
                            break;
                        case 3:
                            // total 10 cell width
                            //<clear> 1.5 cell width
                            Bound.right = MARGIN_LEFT + cellWidth * 3 / 2;
                            letterCells[row][0] = new KeyCell(letters[row][0], new RectF(Bound));

                            //<123> 1.5 cell width
                            Bound.left = Bound.right + CELL_MARGIN_LEFT / 2 * 3;
                            Bound.right = Bound.left + cellWidth / 2 * 3 + CELL_MARGIN_LEFT / 2;
                            letterCells[row][2] = new KeyCell(letters[row][1], new RectF(Bound));

                            //<space>  4 cell width
                            Bound.left = Bound.right + CELL_MARGIN_LEFT / 2 * 3;
                            Bound.right = Bound.left + cellWidth * 4 + CELL_MARGIN_LEFT / 2;
                            letterCells[row][4] = new KeyCell(letters[row][2], new RectF(Bound));

                            //<!@#> 1.5 cell width
                            Bound.left = Bound.right + CELL_MARGIN_LEFT / 2 * 3;
                            Bound.right = Bound.left + cellWidth / 2 * 3 + CELL_MARGIN_LEFT / 2;
                            letterCells[row][6] = new KeyCell(letters[row][3], new RectF(Bound));

                            //<back>  1.5 cell width
                            Bound.left = Bound.right + CELL_MARGIN_LEFT / 2 * 3;
                            Bound.right = Bound.left + cellWidth / 2 * 3 + CELL_MARGIN_LEFT / 2;
                            letterCells[row][8] = new KeyCell(letters[row][4], new RectF(Bound));

                            break;
                    }

                    Bound.offset(0, rowHeight); // move to next row and first column
                    Bound.left = MARGIN_LEFT;
                    Bound.right = MARGIN_LEFT + cellWidth;
                }
                currentCells = letterCells;


                break;
            case TYPE_NUMBER:
                Bound = new RectF(MARGIN_LEFT, MARGIN_TOP + ROW_PADPING_TOP,
                        cellWidth + MARGIN_LEFT, MARGIN_TOP + cellHeight
                        + ROW_PADPING_TOP);
                for (int row = 0; row < numbers.length; row++) {
                    numCells[row][0] = new KeyCell(numbers[row][0],
                            new RectF(Bound));
                    Bound.left = MARGIN_LEFT + cellWidth + CELL_MARGIN_LEFT;
                    Bound.right = Bound.left + cellWidth * 2;
                    for (int j = 1; j < numbers[row].length - 1; j++) {
                        numCells[row][j] = new KeyCell(numbers[row][j], new RectF(
                                Bound));
                        Bound.offset(cellWidth * 2 + CELL_MARGIN_LEFT, 0);
                    }

                    Bound.left = MARGIN_LEFT + cellWidth * 7 + CELL_MARGIN_LEFT * 4;
                    Bound.right = Bound.left + cellWidth;
                    numCells[row][4] = new KeyCell(numbers[row][4],
                            new RectF(Bound));

                    Bound.offset(0, rowHeight); // move to next row and first column
                    Bound.left = MARGIN_LEFT;
                    Bound.right = MARGIN_LEFT + cellWidth;
                }
                currentCells = numCells;
                break;
            case TYPE_SYMBOL:
                Bound = new RectF(MARGIN_LEFT, MARGIN_TOP + ROW_PADPING_TOP,
                        cellWidth + MARGIN_LEFT, MARGIN_TOP + cellHeight
                        + ROW_PADPING_TOP);
                for (int row = 0; row < symbol.length; row++) {

                    for (int j = 0; j < symbol[row].length - 1; j++) {
                        symbolCells[row][j] = new KeyCell(symbol[row][j],
                                new RectF(Bound));
                        Bound.offset(cellWidth + CELL_MARGIN_LEFT, 0);
                    }
                    Bound.right = MARGIN_LEFT + rowWidth;
                    symbolCells[row][8] = new KeyCell(symbol[row][8], new RectF(
                            Bound));

                    Bound.offset(0, rowHeight); // move to next row and first column
                    Bound.left = MARGIN_LEFT;
                    Bound.right = MARGIN_LEFT + cellWidth;
                }
                currentCells = symbolCells;
                break;
            default:
                break;
        }

        if (current == null) {
            current = new CurrentKeyHolder();
            current.cell = currentCells[0][0];
            currentCells[0][0].getFocus();
            current.index = 0;
            current.row = 0;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        super.onDraw(canvas);
        Log.v(TAG, "onDraw");
        //canvas.drawColor(0xff111111);
//		canvas.drawColor(0x0000ff);
        canvas.drawColor(00000000);

        // draw cells
        for (KeyCell[] row : currentCells) {
            for (KeyCell cell : row) {
                if (cell != null) {
                    cell.draw(canvas);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int key, KeyEvent event) {

        if (current == null || current.cell == null) {

            Log.v(TAG, "current or cell is null");

            if (currentCells != null) {
                current = new CurrentKeyHolder();
                current.cell = currentCells[0][0];
                currentCells[0][0].getFocus();
                current.index = 0;
                current.row = 0;
                invalidate();
            }

            return super.onKeyDown(key, event);
        }

        Log.v(TAG, "current == " + current);

        if (key == KeyEvent.KEYCODE_ENTER) {
            Log.v(TAG, "current == KEYCODE_ENTER");
            if (mOnCellTouchListener != null) {
                Log.v(TAG, "mOnCellTouchListener");
                mOnCellTouchListener.onTouch(
                        current.cell, this.getSubView());
            }

            switchKeyBoard();

        } else if (key == KeyEvent.KEYCODE_DPAD_UP) {
            Log.v(TAG, "current == KEYCODE_DPAD_UP");

            current.cell.clearFocus();
            current.row = current.row != 0 ? current.row - 1
                    : (currentCells.length - 1);
            // current.cell = currentCells[current.row][current.index];
            for (int i = current.index; i >= 0; i--) {
                if (currentCells[current.row][i] != null) {
                    current.index = i;
                    current.cell = currentCells[current.row][current.index];
                    break;
                }
            }
            current.cell.getFocus();
        } else if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
            Log.v(TAG, "current == KEYCODE_DPAD_DOWN");
            current.cell.clearFocus();
            current.row = current.row != (currentCells.length - 1) ? (current.row + 1)
                    : 0;
            // current.cell = currentCells[current.row][current.index];
            for (int i = current.index; i >= 0; i--) {
                if (currentCells[current.row][i] != null) {
                    current.index = i;
                    current.cell = currentCells[current.row][current.index];
                    break;
                }
            }
            current.cell.getFocus();
        } else if (key == KeyEvent.KEYCODE_DPAD_LEFT) {
            Log.v(TAG, "current == KEYCODE_DPAD_LEFT");
            current.cell.clearFocus();
            current.index = current.index != 0 ? current.index - 1
                    : (currentCells[current.row].length - 1);
            for (int i = current.index; i >= 0; i--) {
                if (currentCells[current.row][i] != null) {
                    current.index = i;
                    current.cell = currentCells[current.row][current.index];
                    break;
                }
            }
            current.cell.getFocus();
        } else if (key == KeyEvent.KEYCODE_DPAD_RIGHT) {

            Log.v(TAG, "current == KEYCODE_DPAD_RIGHT");
            current.cell.clearFocus();
            current.index = current.index != (currentCells[current.row].length - 1) ? (current.index + 1)
                    : 0;
            current.cell = null;
            for (int i = current.index; i < currentCells[current.row].length; i++) {
                if (currentCells[current.row][i] != null) {
                    current.index = i;
                    current.cell = currentCells[current.row][current.index];
                    break;
                }
            }
            if (current.cell == null) {// 如果到右边之后遇到最后一个cell是空，则强制跳到第一个！
                current.cell = currentCells[current.row][0];
                current.index = 0;
            }
            current.cell.getFocus();
        } else if (key == KeyEvent.KEYCODE_MENU) {
            if (keyListener2 != null) {
                keyListener2.onKeyDown2(key, event);
                return false;
            }
        } else if (key == KeyEvent.KEYCODE_BACK) {
            if (keyListener2 != null) {
                keyListener2.onKeyDown2(key, event);
                return true;
            }
        }
        postInvalidate();
        this.setFocusable(true);
        return super.onKeyDown(key, event);
    }

    private OnKeyListener2 keyListener2;

    public interface OnKeyListener2 {
        public boolean onKeyDown2(int key, KeyEvent event);
    }

    public void setKeyListener2(OnKeyListener2 listener) {
        this.keyListener2 = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "current == onTouchEvent");

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (int row = 0; row < currentCells.length; row++) {
                for (int j = 0; j < currentCells[row].length; j++) {
                    if (currentCells[row][j] == null) {
                        continue;
                    }
                    if (currentCells[row][j].hitTest((int) event.getX(),
                            (int) event.getY())) {
                        if (current == null) {
                            current = new CurrentKeyHolder();
                        } else {
                            if (current.cell != null) {
                                current.cell.clearFocus();
                            }
                        }
                        current.row = row;
                        current.index = j;
                        current.cell = currentCells[row][j];
                        current.cell.getFocus();
                        invalidate();
                        return true;
                    }
                }
            }
            if (current != null) {
                current.cell = null;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (current == null || current.cell == null) {// 当页面上没有焦点的时候，actionup时间就直接返回
                return true;
            }

            if (mOnCellTouchListener != null) {

                mOnCellTouchListener.onTouch(current.cell, this.getSubView());
            }
            if (current != null && current.cell != null) {// 当页面上没有焦点的时候，actionup时间就直接返回
                current.cell.clearFocus();
                invalidate();
            }

            switchKeyBoard();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

        }
        return true;
    }

    /**
     * 切换键盘事件
     */

    private static String DELETE = "del";
    private static String BACK = "back";
    private static String NUM = "123";
    private static String SYMBOL = "!@#";
    private static String SPACE = "space";
    private static String LETTER_CAPITAL = "ABC";
    private static String LETTER_SMALL = "abc";

    public void switchKeyBoard() {
        if (current != null) {
            if (current.cell.mText.equals(NUM)) {
                setType(TYPE_NUMBER);
            } else if (current.cell.mText.equals(SYMBOL)) {
                setType(TYPE_SYMBOL);
            } else if (current.cell.mText.equals(LETTER_CAPITAL)) {
                setType(TYPE_LETTER_CAPITAL);
            } else if (current.cell.mText.equals(LETTER_SMALL)) {
                setType(TYPE_LETTER_SMALL);
            }
        }
    }

    @Override
    public void clearFocus() {
        if (current != null) {
            current.cell.clearFocus();
            current = null;
        }
    }

    private CurrentKeyHolder current;

    public interface OnCellTouchListener {
        public void onTouch(KeyCell cell, View view);
    }

    public void setOnCellTouchListener(OnCellTouchListener p) {
        mOnCellTouchListener = p;
    }

    private class CurrentKeyHolder {
        public KeyCell cell;
        public int index;
        public int row;
    }


}
