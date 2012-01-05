package com.tetris;

import java.util.Random;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * TetrisView: implementation of a simple game of Tetris
 */
public class TetrisView extends TileView {

    private static final String TAG = "TetrisView";

    /**
     * Current mode of application: READY to run, RUNNING, or you have already
     * lost. static final ints are used instead of an enum for performance
     * reasons.
     */
//    private int mMode = READY;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;

    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
    private static final int BLUEUNIT = 1;
    private static final int BROWNUNIT = 2;
    private static final int CYANUNIT = 3;
    private static final int GREENUNIT = 4;
    private static final int ORANGEUNIT = 5;
    private static final int PURPLEUNIT = 6;
    private static final int REDUNIT = 7;
    private static final int WALL = 8;

    private boolean isNewBlock = false;
    
    /**
     * mScore: used to keep score 
     * mMoveDelay: number of milliseconds between Tetris movements. 
     * This will decrease over time.
     */
    private static final long mMoveDelay = 50;
    
    /**
     * mLastMove: tracks the absolute time when the Tetris last moved, and is used
     * to determine if a move should be made based on mMoveDelay.
     */
    private long mLastMove;
    
    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView mStatusText;

    /**
     * mTetrisGame: a game state containing all the relevant information about the game.
     */
    private TetrisGame mTetrisGame;
    
    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            TetrisView.this.update();
            TetrisView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };


    /**
     * Constructs a TetrisView based on inflation from XML
     * 
     * @param context
     * @param attrs
     */
    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTetrisView();
   }

    public TetrisView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	initTetrisView();
    }

    private void initTetrisView()
    {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        resetTiles(9);
        loadTile(BLUEUNIT, r.getDrawable(R.drawable.blueunit));
        loadTile(BROWNUNIT, r.getDrawable(R.drawable.brownunit));
        loadTile(CYANUNIT, r.getDrawable(R.drawable.cyanunit));
        loadTile(GREENUNIT, r.getDrawable(R.drawable.greenunit));
        loadTile(ORANGEUNIT, r.getDrawable(R.drawable.orangeunit));
        loadTile(PURPLEUNIT, r.getDrawable(R.drawable.purpleunit));
        loadTile(REDUNIT, r.getDrawable(R.drawable.redunit));
        loadTile(WALL, r.getDrawable(R.drawable.wall));
        
    	if (mTetrisGame == null)
    		mTetrisGame = new TetrisGame();
    }

    /**
     * Save game state so that the user does not lose anything
     * if the game process is killed while we are in the 
     * background.
     * 
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putLong("mTimeDelay", Long.valueOf(mTetrisGame.getTimeDelay()));
        map.putLong("mScore", Long.valueOf(mTetrisGame.getScore()));
        map.putInt("mTetrisBlockOrientation", mTetrisGame.getOrientation());
        map.putInt("mTetrisBlockType", mTetrisGame.getBlockType());
        map.putInt("mTetrisBlock1x", Integer.valueOf(mTetrisGame.getX()));
        map.putInt("mTetrisBlock1y", Integer.valueOf(mTetrisGame.getY()));

        return map;
    }
    
    /**
     * Restore game state if our process is being relaunched
     * 
     * @param savedState a Bundle containing the game state
     */
    public void restoreState(Bundle savedState) {
        setMode(PAUSE);
        mTetrisGame = new TetrisGame(new TetrisBlock (savedState.getInt("mTetrisBlock1x"), savedState.getInt("mTetrisBlock1y"), savedState.getInt("mTetrisBlockType"), savedState.getInt("mTetrisBlockOrientation")), savedState.getLong("mScore"), savedState.getLong("mTimeDelay"));
    }

    /**
     * handles key events in the game. Update the direction our Tetris is traveling
     * based on the DPAD. 
     * 
     * @see android.view.View#onKeyDown(int, android.os.KeyEvent)
     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent msg)
//    {
////    	if (mTetrisGame == null)
////    		mTetrisGame = new TetrisGame();
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//        	mTetrisGame.update(1);
//            setMode(RUNNING); //inefficient?
//            update(); //inefficient: runs updateFalling when no need
//            return (true);
//        } 
//        
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//        	mTetrisGame.update(2);
//            return (true);
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//        	mTetrisGame.update(3);
//            return (true);
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//        	mTetrisGame.update(4);
//            return (true);
//        }
//        if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//        	mTetrisGame.update(5);
//            return (true);
//        }
//        
//        return super.onKeyDown(keyCode, msg);
//    }

    public boolean pressKey(int input)
    {
//    	if (mTetrisGame == null)
//    		mTetrisGame = new TetrisGame();

    	switch (input)
    	{
    	case 0:
    	{
    		return false;
    	}
    	case 1:
    	{
        	mTetrisGame.update(1);
            mTetrisGame.setMode(RUNNING); //inefficient?
            update(); //inefficient: runs updateFalling when no need
            return (true);
    	}
    	case 2:
    	{
        	mTetrisGame.update(2);
            return (true);
    	}
    	case 3:
    	{
        	mTetrisGame.update(3);
            return (true);
    	}
    	case 4:
    	{
        	mTetrisGame.update(4);
            return (true);
    	}
    	case 5:
    	{
        	mTetrisGame.update(5);
            return (true);
    	}
        }
    	return false;
    }    
    
    public int getBlockType()
    {
    	return mTetrisGame.getBlockType();
    }
    
    public boolean getIsNewBlock()
    {
    	return mTetrisGame.getIsNewBlock();
    }
    
/*    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {

    	
        if (touchEvent == MotionEvent.ACTION_UP) {
            if (mMode == READY | mMode == LOSE) {
                initNewGame();
                setMode(RUNNING);
                update();
                return (true);
            } 
            if (mMode == PAUSE) {
                setMode(RUNNING);
                update();
                return (true);
            } 

            if (mMode == RUNNING) {
            	rotateClockwise();
                update(); //slightly inefficient, runs updateFalling when unnecessary
            return (true);
            }
        } 
        
        if (touchEvent == MotionEvent.ACTION_DOWN) {
            if (!((mTetrisBlock.y1 == mYTileCount - 2) || (mTetrisBlock.y2 == mYTileCount - 2) || (mTetrisBlock.y3 == mYTileCount - 2) || (mTetrisBlock.y4 == mYTileCount - 2)))
            {
                if (!((oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1 + 1]) || (oldBlocks[mTetrisBlock.x2][mTetrisBlock.y2 + 1]) || (oldBlocks[mTetrisBlock.x3][mTetrisBlock.y3 + 1]) || (oldBlocks[mTetrisBlock.x4][mTetrisBlock.y4 + 1])))
                	mTetrisBlock.moveBlock(SOUTH);
            }
            return (true);
        }
        if (touchEvent == MotionEvent.ACTION_LEFT) {
            if (!((mTetrisBlock.x1 < 2) || (mTetrisBlock.x2 < 2) || (mTetrisBlock.x3 < 2) || (mTetrisBlock.x4 < 2)))
            {
                if (!((oldBlocks[mTetrisBlock.x1 - 1][mTetrisBlock.y1]) || (oldBlocks[mTetrisBlock.x2 - 1][mTetrisBlock.y2]) || (oldBlocks[mTetrisBlock.x3 - 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 - 1][mTetrisBlock.y4])))
                	mTetrisBlock.moveBlock(WEST);
            }
            return (true);
        }
        if (touchEvent == MotionEvent.ACTION_RIGHT) {
            if (!((mTetrisBlock.x1 == (mXTileCount - 2)) || (mTetrisBlock.x2 == (mXTileCount - 2)) || (mTetrisBlock.x3 == (mXTileCount - 2)) || (mTetrisBlock.x4 == (mXTileCount - 2))))
            {
                if (!((oldBlocks[mTetrisBlock.x1 + 1][mTetrisBlock.y1]) || (oldBlocks[mTetrisBlock.x2 + 1][mTetrisBlock.y2]) || (oldBlocks[mTetrisBlock.x3 + 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 + 1][mTetrisBlock.y4])))
                	mTetrisBlock.moveBlock(EAST);
            }
            return (true);
        }
        if (performLongClick() || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	while (!checkCollision())
        		mTetrisBlock.moveBlock(SOUTH);
            return (true);
        }
        
        return super.onTouchEvent(touchEvent);
    }*/
    
    /**
     * Sets the TextView that will be used to give information (such as "Game
     * Over" to the user.
     * 
     * @param newView
     */
    public void setTextView(TextView newView) {
        mStatusText = newView;
    }

    /**
     * Updates the current mode of the application (RUNNING or PAUSED or the like)
     * as well as sets the visibility of textview for notification
     * 
     * @param newMode
     */
    public void setMode(int newMode)
    {
//    	if (mTetrisGame == null)
//    		mTetrisGame = new TetrisGame();
    	
        if (newMode == RUNNING & mTetrisGame.getMode() != RUNNING) {
        	mTetrisGame.setMode(newMode);
            mStatusText.setVisibility(View.INVISIBLE);
            update();
            return;
        }

    	mTetrisGame.setMode(newMode);

    	CharSequence str = "";
        if (newMode == READY) {
            str = "Tetris\nPress Up To Play";
        }
        if (newMode == PAUSE) {
            str = "Paused\nPress Up To Resume";
        }
        if (newMode == LOSE) {
            str = "Game Over\nScore: " + mTetrisGame.getScore()
                  + "\nPress Up To Play";
        }
    	
//    	<string name="mode_ready">Tetris\nPress Up To Play</string>
//    	<string name="mode_pause">Paused\nPress Up To Resume</string>
//    	<string name="mode_lose_prefix">Game Over\nScore: </string>
//    	<string name="mode_lose_suffix">\nPress Up To Play</string>        Resources res = getContext().getResources();
//        CharSequence str = "";
//        if (newMode == PAUSE) {
//            str = res.getText(R.string.mode_pause);
//        }
//        if (newMode == READY) {
//            str = res.getText(R.string.mode_ready);
//        }
//        if (newMode == LOSE) {
//            str = res.getString(R.string.mode_lose_prefix) + mTetrisGame.getScore()
//                  + res.getString(R.string.mode_lose_suffix);
//        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }

    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the Tetris's location.
     */
    public void update() {

    	mTetrisGame.checkTop();
    	mTetrisGame.update(0);
    	setMode(mTetrisGame.getMode());
    	
    	if (mTetrisGame.getMode() == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastMove > mMoveDelay)
            {
            	clearTiles();
            	mTetrisGame.updateFalling();
            	mTetrisGame.clearRow();                
                updateWalls();
                drawBlock();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }
    }
        
    /**
     * Draws some walls.
     * 
     */
    private void updateWalls()
    {
        for (int x = 0; x < mXTileCount; x++) {
            setTile(WALL, x, 0);
            setTile(WALL, x, mYTileCount - 1);
        }
        for (int y = 1; y < mYTileCount - 1; y++) {
            setTile(WALL, 0, y);
            setTile(WALL, mXTileCount - 1, y);
        }
    }

    /**
     * Figure out which way the Tetris is going, see if he's run into anything (the
     * walls, himself). If he's not going to die, we then add to the
     * front and subtract from the rear in order to simulate motion. If we want to
     * grow him, we don't subtract from the rear.
     * 
     */


    private void drawBlock()
    {
    	int unit = mTetrisGame.getBlockType();
    	TetrisBlock mTetrisBlock = new TetrisBlock(mTetrisGame.getX(), mTetrisGame.getY(), unit, mTetrisGame.getOrientation());
    	
    	setTile(unit, mTetrisBlock.x1, mTetrisBlock.y1);
    	setTile(unit, mTetrisBlock.x2, mTetrisBlock.y2);
    	setTile(unit, mTetrisBlock.x3, mTetrisBlock.y3);
    	setTile(unit, mTetrisBlock.x4, mTetrisBlock.y4);
        

        for (int x = 0; x < mXTileCount; x++)
        {
            for (int y = 0; y < mYTileCount; y++)
            {
	            if (mTetrisGame.getBlocks(x, y)) 
	            {
	                setTile(mTetrisGame.getColors(x, y), x, y);
	            }
            }
        }
    }
}