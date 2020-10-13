package edu.harding.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import co.edu.unal.tictactoe.R;

public class BoardView extends View {
    public static final int GRID_WIDTH = 6;
    private static final int PADDING = 50;

    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;

    private Paint mPaint;
    private TicTacToeGame mGame;

    private int boardColor;
    private SharedPreferences mPrefs;


    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void initialize(){
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.computer_icon);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int boardHeight = getHeight();
        int boardWidth = getWidth();

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getBoardColor());
        mPaint.setStrokeWidth(GRID_WIDTH);


        int cellWidth = boardWidth / 3;
        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
        canvas.drawLine(cellWidth * 2 , 0, cellWidth * 2, boardHeight, mPaint);
        canvas.drawLine(0 , cellWidth, boardWidth, cellWidth, mPaint);
        canvas.drawLine(0 , cellWidth * 2, boardWidth, cellWidth * 2, mPaint);


        // Draw all the X and O images
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;

            // Define the boundaries of a destination rectangle for the image
            int left = (col * cellWidth) + PADDING / 2;
            int top = (row * cellWidth) + PADDING / 2;
            int right = (left + cellWidth) - PADDING;
            int bottom = (top + cellWidth) - PADDING;

            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                        null,  // src
                        new Rect(left, top, right, bottom),  // dest
                        null);
            }
            else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                        null,  // src
                        new Rect(left, top, right, bottom),  // dest
                        null);
            }
        }

    }

    public void setGame(TicTacToeGame game) {
        mGame = game;
    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }


    public int getBoardColor() {
        return mPrefs.getInt("color_preference", getResources().getColor(R.color.default_color));
    }

    public void setBoardColor(int boardColor) {
        this.boardColor = boardColor;
    }
}
