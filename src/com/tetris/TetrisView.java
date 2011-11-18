package com.tetris;

//import java.util.ArrayList;
//import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
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
    private int mMode = READY;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;

    //constants for direction
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;

    /**
     * Current direction the Tetris is facing.
     */
    private int mOrientation = FACEDOWN;
    private static final int FACEUP = 0;
    private static final int FACERIGHT = 1;
    private static final int FACEDOWN = 2;
    private static final int FACELEFT = 3;
    
    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
//    private static final int BLOCK = 1;
    private static final int WALL = 2;
    private static final int UNIT = 3;

    /**
     * mScore: used to keep score 
     * mMoveDelay: number of milliseconds between Tetris movements. 
     * This will decrease over time.
     */
    private long mScore = 0;
    private static final long mMoveDelay = 50;
    private long mTimeDelay = 2000;
    
    /**
     * mLastMove: tracks the absolute time when the Tetris last moved, and is used
     * to determine if a move should be made based on mMoveDelay.
     */
    private long mLastMove;
    private long mLastTimedMove;
    
    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView mStatusText;

    /**
     * dimensions of the Tetris world.
     */
/*    private static final int mXTileCount = 10;
    private static final int mYTileCount = 20;*/
    
    /**
     * mTetrisBlock: a list of Coordinates that make up the Tetris piece
     */
    private TetrisBlock mTetrisBlock = new TetrisBlock(13, 2, 13, 3, 12, 2, 14, 2);

    private boolean[][] oldBlocks = new boolean[mXTileCount][mYTileCount];    
    
    /**
     * Everyone needs a little randomness in their life
     */
//    private static final Random RNG = new Random();

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
//            TetrisView.this.updateFalling();
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

    private void initTetrisView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        resetTiles(4);
//        loadTile(BLOCK, r.getDrawable(R.drawable.tetrisblock));
        loadTile(WALL, r.getDrawable(R.drawable.wall));
        loadTile(UNIT, r.getDrawable(R.drawable.redunit));
    	
    }
    

    private void initNewGame() {
//        mTetrisBlock.clear();
//        mTetrisBlock.add(new Coordinate(12, 2));
    	
        mTetrisBlock = new TetrisBlock(13, 2, 13, 3, 12, 2, 14, 2);
//        mTetrisBlock = new TetrisBlock(4, 4, 4, 3, 4, 5, 4, 6);

//        mMoveDelay = 50;
        mOrientation = FACEDOWN;
        mTimeDelay = 500;
        mScore = 0;
        oldBlocks = new boolean[mXTileCount][mYTileCount];
        
//        mXTileCount = 10;
//		mXTileCount = 10;
        
    }

    private void initNewBlock()
    {
        mOrientation = FACEDOWN;
        mTetrisBlock = new TetrisBlock(13, 2, 13, 3, 12, 2, 14, 2);
        mScore += 100;
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

        map.putLong("mTimeDelay", Long.valueOf(mTimeDelay));
        map.putLong("mScore", Long.valueOf(mScore));
        map.putInt("mTetrisBlock1x", Integer.valueOf(mTetrisBlock.firstx));
        map.putInt("mTetrisBlock1y", Integer.valueOf(mTetrisBlock.firsty));
        map.putInt("mTetrisBlock2x", Integer.valueOf(mTetrisBlock.secondx));
        map.putInt("mTetrisBlock2y", Integer.valueOf(mTetrisBlock.secondy));
        map.putInt("mTetrisBlock3x", Integer.valueOf(mTetrisBlock.thirdx));
        map.putInt("mTetrisBlock3y", Integer.valueOf(mTetrisBlock.thirdy));
        map.putInt("mTetrisBlock4x", Integer.valueOf(mTetrisBlock.fourthx));
        map.putInt("mTetrisBlock4y", Integer.valueOf(mTetrisBlock.fourthy));

        return map;
    }

    /**
     * Restore game state if our process is being relaunched
     * 
     * @param savedState a Bundle containing the game state
     */
    public void restoreState(Bundle savedState) {
        setMode(PAUSE);

        mTimeDelay = savedState.getLong("mTimeDelay");
        mScore = savedState.getLong("mScore");
        mTetrisBlock = new TetrisBlock (savedState.getInt("mTetrisBlock1x"), savedState.getInt("mTetrisBlock1y"), savedState.getInt("mTetrisBlock2x"), savedState.getInt("mTetrisBlock2y"), savedState.getInt("mTetrisBlock3x"), savedState.getInt("mTetrisBlock3y"), savedState.getInt("mTetrisBlock4x"), savedState.getInt("mTetrisBlock4y"));
    }

    /**
     * handles key events in the game. Update the direction our Tetris is traveling
     * based on the DPAD. 
     * 
     * @see android.view.View#onKeyDown(int, android.os.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mMode == READY | mMode == LOSE) {
                /*
                 * At the beginning of the game, or the end of a previous one,
                 * we should start a new game.
                 */
                initNewGame();
                setMode(RUNNING);
                update();
                return (true);
            } 
            if (mMode == PAUSE) {
                /*
                 * If the game is merely paused, we should just continue where
                 * we left off.
                 */
                setMode(RUNNING);
                update();
                return (true);
            } 

            if (mMode == RUNNING) {
//                mOrientation = (mOrientation + 1) % 4;
            	rotateClockwise();
                update(); //slightly inefficient, runs updateFalling when unnecessary
/*                clearTiles();
                updateWalls();
            	drawBlock();*/
            return (true);
            }
        } 
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!((mTetrisBlock.firsty == mYTileCount - 2) || (mTetrisBlock.secondy == mYTileCount - 2) || (mTetrisBlock.thirdy == mYTileCount - 2) || (mTetrisBlock.fourthy == mYTileCount - 2)))
            {
                if (!((oldBlocks[mTetrisBlock.firstx][mTetrisBlock.firsty + 1]) || (oldBlocks[mTetrisBlock.secondx][mTetrisBlock.secondy + 1]) || (oldBlocks[mTetrisBlock.thirdx][mTetrisBlock.thirdy + 1]) || (oldBlocks[mTetrisBlock.fourthx][mTetrisBlock.fourthy + 1])))
                	mTetrisBlock.moveBlock(SOUTH);
            }
            return (true);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (!((mTetrisBlock.firstx < 2) || (mTetrisBlock.secondx < 2) || (mTetrisBlock.thirdx < 2) || (mTetrisBlock.fourthx < 2)))
            {
                if (!((oldBlocks[mTetrisBlock.firstx - 1][mTetrisBlock.firsty]) || (oldBlocks[mTetrisBlock.secondx - 1][mTetrisBlock.secondy]) || (oldBlocks[mTetrisBlock.thirdx - 1][mTetrisBlock.thirdy]) || (oldBlocks[mTetrisBlock.fourthx - 1][mTetrisBlock.fourthy])))
                	mTetrisBlock.moveBlock(WEST);
            }
            return (true);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (!((mTetrisBlock.firstx == (mXTileCount - 2)) || (mTetrisBlock.secondx == (mXTileCount - 2)) || (mTetrisBlock.thirdx == (mXTileCount - 2)) || (mTetrisBlock.fourthx == (mXTileCount - 2))))
            {
                if (!((oldBlocks[mTetrisBlock.firstx + 1][mTetrisBlock.firsty]) || (oldBlocks[mTetrisBlock.secondx + 1][mTetrisBlock.secondy]) || (oldBlocks[mTetrisBlock.thirdx + 1][mTetrisBlock.thirdy]) || (oldBlocks[mTetrisBlock.fourthx + 1][mTetrisBlock.fourthy])))
                	mTetrisBlock.moveBlock(EAST);
            }
            return (true);
        }
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
        	while (!checkCollision())
        		mTetrisBlock.moveBlock(SOUTH);
            return (true);
        }
        
        return super.onKeyDown(keyCode, msg);
    }

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
    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == RUNNING & oldMode != RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == PAUSE) {
            str = res.getText(R.string.mode_pause);
        }
        if (newMode == READY) {
            str = res.getText(R.string.mode_ready);
        }
        if (newMode == LOSE) {
            str = res.getString(R.string.mode_lose_prefix) + mScore
                  + res.getString(R.string.mode_lose_suffix);
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }

    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the Tetris's location.
     */
    public void update() {
    	
    	checkTop();
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

//            Log.v(TAG, "1x: " + mTetrisBlock.firstx + " 1y: " + mTetrisBlock.firsty + " 2x: " + mTetrisBlock.secondx + " 2y: " + mTetrisBlock.secondy + " 3x: " + mTetrisBlock.thirdx + " 3y: " + mTetrisBlock.thirdy + " 4x: " + mTetrisBlock.fourthx + " 4y: " + mTetrisBlock.fourthy);
//            Log.v(TAG, "mXTileCount: " + mXTileCount);

            if (now - mLastMove > mMoveDelay)
            {
            	clearTiles();
                updateWalls();
                updateFalling();
                checkCollision();
                clearRow();                
                drawBlock();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }
    }
    
    private void saveBlocks()
    {
    	oldBlocks[mTetrisBlock.firstx][mTetrisBlock.firsty] = true;
    	oldBlocks[mTetrisBlock.secondx][mTetrisBlock.secondy] = true;
    	oldBlocks[mTetrisBlock.thirdx][mTetrisBlock.thirdy] = true;
    	oldBlocks[mTetrisBlock.fourthx][mTetrisBlock.fourthy] = true;
    	
/*        switch (mOrientation) {
	        case FACEUP: {
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x + 1][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x - 1][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y - 1] = true;
	        	break;
	        }
	        case FACERIGHT: {
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x + 1][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y + 1] = true;
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y - 1] = true;
	        	break;
	        }
	        case FACEDOWN: {
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x + 1][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x - 1][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y + 1] = true;
		        break;
	        }
	        case FACELEFT: {
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x - 1][mTetrisBlock.y] = true;
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y + 1] = true;
	        	oldBlocks[mTetrisBlock.x][mTetrisBlock.y - 1] = true;
		        break;
	        }
	    }*/
    }
    
    
/*    public void updateFalling() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastTimedMove > mTimeDelay) {
                clearTiles();
                updateWalls();

                mTetrisBlock.y = mTetrisBlock.y + 1;
                drawBlock();
                
                mLastTimedMove = now;
            }
            mRedrawHandler.sleep(mTimeDelay);
        }
    }*/
    public void updateFalling() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastTimedMove > mTimeDelay)
            {
//                mTetrisBlock.y = mTetrisBlock.y + 1;
                if (!((mTetrisBlock.firsty == mYTileCount - 2) || (mTetrisBlock.secondy == mYTileCount - 2) || (mTetrisBlock.thirdy == mYTileCount - 2) || (mTetrisBlock.fourthy == mYTileCount - 2)))
                {
                    if(!checkCollision())
                    	mTetrisBlock.fall();
                }
                mLastTimedMove = now;
            }
            checkCollision();
        }
    }
    
/*    public void updateInput(int mInputDirection)
    {
        if (mMode == RUNNING) {
            switch (mInputDirection) {
    	        case EAST: {
    	            mTetrisBlock.x = mTetrisBlock.x + 1;
    	            break;
    	        }
    	        case WEST: {
    	            mTetrisBlock.x = mTetrisBlock.x - 1;
    	            break;
    	        }
    	        case SOUTH: {
    	            mTetrisBlock.y = mTetrisBlock.y + 1;
    	            break;
    	        }
            }
            checkCollision();
        }
    }*/


    /** 
     * Check to see if the game is over
     * @return true when game is over, false otherwise
     */
    private boolean checkTop()
    {
        for (int x = 0; x < mXTileCount; x++)
        {
        	if (oldBlocks[x][2])
        	{
        		setMode(LOSE);
        		return true;
        	}
        }
    	
    	return false;
    }
    
    /**
     * Collision detection, and handler.
     */
    private boolean checkCollision()
    {

    	//if it hits the bottom
    	if (mTetrisBlock.firsty > (mYTileCount - 3) || mTetrisBlock.secondy > (mYTileCount - 3) || mTetrisBlock.thirdy > (mYTileCount - 3) || mTetrisBlock.fourthy > (mYTileCount - 3))
        {
        	saveBlocks();
        	initNewBlock();
            clearRow();
    		return true;
        }
    	
        if (oldBlocks[mTetrisBlock.firstx][mTetrisBlock.firsty + 1] || oldBlocks[mTetrisBlock.secondx][mTetrisBlock.secondy + 1] || oldBlocks[mTetrisBlock.thirdx][mTetrisBlock.thirdy + 1] || oldBlocks[mTetrisBlock.fourthx][mTetrisBlock.fourthy + 1])
        {
        	saveBlocks();
        	initNewBlock();
            clearRow();                
    		return true;
        }
    	
/*        if (oldBlocks[mTetrisBlock.x][mTetrisBlock.y + 1])
        {
        	saveBlocks();
        	initNewBlock();
            clearRow();                
        }*/
        
        return false;
    }
    
    /**
     * check to see if there are any full rows. If there are, clear them.
     */
    private void clearRow()
    {
    	boolean full = true;
    	boolean[][] temp = new boolean[mXTileCount][mYTileCount];
    	
    	for (int z = mYTileCount - 2; z > 1; z--)
    	{
    		Log.v(TAG, "z: " + z);
	        for (int x = 1; x < mXTileCount - 1; x++)
	        {
	        	if (!oldBlocks[x][z])
	        		full = false;
	
	        	//ideally figure out way to break loop sooner
//	        	if (!full)
//	        		return;
	        }
	        
	        if (full)
	        {
	        	mScore += 1000;
	            for (int x = 0; x < mXTileCount; x++)
	            {
	            	oldBlocks[x][z] = false;
	            }
	            
	            for (int x = 1; x < mXTileCount - 1; x++)
	            {
	                for (int y = z; y > 1; y--)
	                	temp[x][y] = oldBlocks[x][y - 1];
	                for (int y = z + 1; y < mYTileCount; y++)
	                	temp[x][y] = oldBlocks[x][y];
	            }
	            oldBlocks = temp;
	        }
	        
    		full = true;
    	}
    }
    
    private void rotateClockwise()
    {
        if (((mTetrisBlock.firstx < 2) || (mTetrisBlock.secondx < 2) || (mTetrisBlock.thirdx < 2) || (mTetrisBlock.fourthx < 2)))
    	{
//        	mTetrisBlock.moveBlock(EAST);
        	mTetrisBlock.firstx = 2;
    	}
        if (((mTetrisBlock.firstx == (mXTileCount - 2)) || (mTetrisBlock.secondx == (mXTileCount - 2)) || (mTetrisBlock.thirdx == (mXTileCount - 2)) || (mTetrisBlock.fourthx == (mXTileCount - 2))))
    	{
//        	mTetrisBlock.moveBlock(WEST);
        	mTetrisBlock.firstx = mXTileCount - 3;
    	}

        mTetrisBlock.rotateClockwise(mOrientation);
        mOrientation = (mOrientation + 1) % 4;
	}    
    /**
     * Draws some walls.
     * 
     */
    private void updateWalls() {
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
    	setTile(UNIT, mTetrisBlock.firstx, mTetrisBlock.firsty);
    	setTile(UNIT, mTetrisBlock.secondx, mTetrisBlock.secondy);
    	setTile(UNIT, mTetrisBlock.thirdx, mTetrisBlock.thirdy);
    	setTile(UNIT, mTetrisBlock.fourthx, mTetrisBlock.fourthy);
        
/*        switch (mOrientation) {
	        case FACEUP: {
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x - 1, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x + 1, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y - 1);
		        break;
	        }
	        case FACERIGHT: {
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x + 1, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y + 1);
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y - 1);
		        break;
	        }
	        case FACEDOWN: {
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x - 1, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x + 1, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y + 1);
		        break;
	        }
	        case FACELEFT: {
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x - 1, mTetrisBlock.y);
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y + 1);
		        setTile(UNIT, mTetrisBlock.x, mTetrisBlock.y - 1);
		        break;
	        }
	    }*/

        for (int x = 0; x < mXTileCount; x++)
        {
            for (int y = 0; y < mYTileCount; y++)
            {
	            if (oldBlocks[x][y]) 
	            {
	                setTile(UNIT, x, y);
	            }
            }
        }
    }
}
