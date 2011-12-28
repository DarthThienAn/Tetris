package com.tetris;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class Tetris extends Activity {

    private TetrisView mTetrisView;
    private TetrisView mTetrisView2;
    
    private static String ICICLE_KEY = "Tetris-view";

    /**
     * Called when Activity is first created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.tetris_layout);

        mTetrisView = (TetrisView) findViewById(R.id.tetris);
        mTetrisView2 = (TetrisView) findViewById(R.id.tetris2);
        mTetrisView.setTextView((TextView) findViewById(R.id.text));
        mTetrisView2.setTextView((TextView) findViewById(R.id.text2));

        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
            mTetrisView.setMode(TetrisView.READY);
            mTetrisView2.setMode(TetrisView.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mTetrisView.restoreState(map);
                mTetrisView2.restoreState(map);
            } else {
                mTetrisView.setMode(TetrisView.PAUSE);
                mTetrisView2.setMode(TetrisView.PAUSE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mTetrisView.setMode(TetrisView.PAUSE);
        mTetrisView2.setMode(TetrisView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        outState.putBundle(ICICLE_KEY, mTetrisView.saveState());
        outState.putBundle(ICICLE_KEY, mTetrisView2.saveState());
    }

}

