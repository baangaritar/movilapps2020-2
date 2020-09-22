package co.edu.unal.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import edu.harding.tictactoe.TicTacToeGame;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    private boolean mGameOver;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    private TicTacToeGame.DifficultyLevel selectedLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mGame = new TicTacToeGame();
        mGame.setPlayer(TicTacToeGame.Player.Human);
        mGame.clearBoard();
        clearButtons();
        showFragmentDialog(DIALOG_DIFFICULTY_ID);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.new_game:
                mGame.clearBoard();
                clearButtons();
                Toast.makeText(MainActivity.this, "Continue playing in " + mGame.getDifficultyLevel().toString() + " level", Toast.LENGTH_SHORT).show();
                if(mGame.getPlayer() == TicTacToeGame.Player.Human){
                    mGame.setPlayer(TicTacToeGame.Player.Computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mInfoTextView.setText(R.string.turn_human);
                }
                else {
                    mGame.setPlayer(TicTacToeGame.Player.Human);
                }
                startNewGame(mGame.getDifficultyLevel());
                return true;
            case R.id.ai_difficulty:
                showFragmentDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showFragmentDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showAboutDialog();
                return true;
        }
        return false;


    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_dialog, null);
        builder.setView(layout);
        builder.setPositiveButton("OK", null);
        builder.create().show();

    }

    private void showFragmentDialog(int id){
        final String[] levels = getResources().getStringArray(R.array.difficulty_levels);
        final String[] selected = {null};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        switch (id){
            case DIALOG_QUIT_ID:
                builder.setTitle("");
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                break;
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle("Select a level");
                builder.setSingleChoiceItems(levels, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (levels[which]) {
                            case ("Easy"):
                                selectedLevel = TicTacToeGame.DifficultyLevel.Easy;
                                break;
                            case ("Harder"):
                                selectedLevel = TicTacToeGame.DifficultyLevel.Harder;
                                break;
                            case ("Expert"):
                                selectedLevel = TicTacToeGame.DifficultyLevel.Expert;
                                break;
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedLevel == null || mGame.getDifficultyLevel() == null){
                            selectedLevel = TicTacToeGame.DifficultyLevel.Expert;
                            clearButtons();
                            mGame.clearBoard();
                            startNewGame(selectedLevel);
                        }
                        Toast.makeText(MainActivity.this, "Continue playing", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedLevel == null){
                            selectedLevel = TicTacToeGame.DifficultyLevel.Expert;
                        }
                        Toast.makeText(MainActivity.this, "You select " + selectedLevel.toString() + " level", Toast.LENGTH_SHORT).show();
                        clearButtons();
                        mGame.clearBoard();
                        startNewGame(selectedLevel);
                    }
                });
        }
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(Boolean.FALSE);
        dialog.show();

    }

    private void startNewGame(TicTacToeGame.DifficultyLevel level) {
        mGame.setDifficultyLevel(level);
        if(mGame.getPlayer() == TicTacToeGame.Player.Human){
            mInfoTextView.setText(R.string.first_human);
        }

        mGameOver = Boolean.FALSE;
    }    // End of startNewGame

    private void clearButtons() {
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
    }


    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if(!mGameOver) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);

                    // If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }

                    if (winner == 0)
                        mInfoTextView.setText(R.string.turn_human);
                    else if (winner == 1){
                        mInfoTextView.setText(R.string.result_tie);
                        mGameOver = Boolean.TRUE;
                    }
                    else if (winner == 2){
                        mInfoTextView.setText(R.string.result_human_wins);
                        mGameOver = Boolean.TRUE;
                    } else{
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mGameOver = Boolean.TRUE;
                    }
                }
            }

        }
    }

    private void setMove(char player, int location) {

        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(255, 16, 51));
        else
            mBoardButtons[location].setTextColor(Color.rgb(0, 110, 179));
    }

}