package com.tetris;

//import java.util.ArrayList;
//import java.util.Random;

import java.util.Random;

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

    //types
    private static final int IBLOCK = 0;
    private static final int JBLOCK = 1;
    private static final int LBLOCK = 2;
    private static final int OBLOCK = 3;
    private static final int SBLOCK = 4;
    private static final int TBLOCK = 5;
    private static final int ZBLOCK = 6;
    
    /**
     * Current direction the Tetris is facing.
     */
//    private int mOrientation = FACEUP;
    private static final int FACEUP = 0;
//    private static final int FACERIGHT = 1;
//    private static final int FACEDOWN = 2;
//    private static final int FACELEFT = 3;
    
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
    private TetrisBlock mTetrisBlock = new TetrisBlock(mXTileCount/2, 2, RNG.nextInt(7));

    private boolean[][] oldBlocks = new boolean[mXTileCount][mYTileCount];    
    
    /**
     * Everyone needs a little randomness in their life
     */
    private static final Random RNG = new Random();

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
    	
        mTetrisBlock = new TetrisBlock(mXTileCount/2, 2, RNG.nextInt(7), FACEUP);
//        mTetrisBlock = new TetrisBlock(mXTileCount/2, 2, 0, 0);
//        mTetrisBlock = new TetrisBlock(4, 4, 4, 3, 4, 5, 4, 6);

//        mMoveDelay = 50;
//        mOrientation = FACEUP;
        mTimeDelay = 500;
        mScore = 0;
        oldBlocks = new boolean[mXTileCount][mYTileCount];
        
//        mXTileCount = 10;
//		mXTileCount = 10;
        
    }

    private void initNewBlock()
    {
//        mOrientation = FACEUP;
        mTetrisBlock = new TetrisBlock(mXTileCount/2, 2, RNG.nextInt(7), FACEUP);
//        mTetrisBlock = new TetrisBlock(mXTileCount/2, 2, 0, 0);
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
        map.putInt("mTetrisBlockOrientation", mTetrisBlock.getOrientation());
        map.putInt("mTetrisBlockType", mTetrisBlock.getBlockType());
        map.putInt("mTetrisBlock1x", Integer.valueOf(mTetrisBlock.x1));
        map.putInt("mTetrisBlock1y", Integer.valueOf(mTetrisBlock.y1));
//        map.putInt("mTetrisBlock2x", Integer.valueOf(mTetrisBlock.x2));
//        map.putInt("mTetrisBlock2y", Integer.valueOf(mTetrisBlock.y2));
//        map.putInt("mTetrisBlock3x", Integer.valueOf(mTetrisBlock.x3));
//        map.putInt("mTetrisBlock3y", Integer.valueOf(mTetrisBlock.y3));
//        map.putInt("mTetrisBlock4x", Integer.valueOf(mTetrisBlock.x4));
//        map.putInt("mTetrisBlock4y", Integer.valueOf(mTetrisBlock.y4));

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
        mTetrisBlock = new TetrisBlock (savedState.getInt("mTetrisBlock1x"), savedState.getInt("mTetrisBlock1y"), savedState.getInt("mTetrisBlockType"), savedState.getInt("mTetrisBlockOrientation"));
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
            	rotateClockwise();
                update(); //slightly inefficient, runs updateFalling when unnecessary
            return (true);
            }
        } 
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!((mTetrisBlock.y1 == mYTileCount - 2) || (mTetrisBlock.y2 == mYTileCount - 2) || (mTetrisBlock.y3 == mYTileCount - 2) || (mTetrisBlock.y4 == mYTileCount - 2)))
            {
                if (!((oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1 + 1]) || (oldBlocks[mTetrisBlock.x2][mTetrisBlock.y2 + 1]) || (oldBlocks[mTetrisBlock.x3][mTetrisBlock.y3 + 1]) || (oldBlocks[mTetrisBlock.x4][mTetrisBlock.y4 + 1])))
                	mTetrisBlock.moveBlock(SOUTH);
            }
            return (true);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (!((mTetrisBlock.x1 < 2) || (mTetrisBlock.x2 < 2) || (mTetrisBlock.x3 < 2) || (mTetrisBlock.x4 < 2)))
            {
                if (!((oldBlocks[mTetrisBlock.x1 - 1][mTetrisBlock.y1]) || (oldBlocks[mTetrisBlock.x2 - 1][mTetrisBlock.y2]) || (oldBlocks[mTetrisBlock.x3 - 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 - 1][mTetrisBlock.y4])))
                	mTetrisBlock.moveBlock(WEST);
            }
            return (true);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (!((mTetrisBlock.x1 == (mXTileCount - 2)) || (mTetrisBlock.x2 == (mXTileCount - 2)) || (mTetrisBlock.x3 == (mXTileCount - 2)) || (mTetrisBlock.x4 == (mXTileCount - 2))))
            {
                if (!((oldBlocks[mTetrisBlock.x1 + 1][mTetrisBlock.y1]) || (oldBlocks[mTetrisBlock.x2 + 1][mTetrisBlock.y2]) || (oldBlocks[mTetrisBlock.x3 + 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 + 1][mTetrisBlock.y4])))
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

//            Log.v(TAG, "1x: " + mTetrisBlock.x1 + " 1y: " + mTetrisBlock.y1 + " 2x: " + mTetrisBlock.x2 + " 2y: " + mTetrisBlock.y2 + " 3x: " + mTetrisBlock.x3 + " 3y: " + mTetrisBlock.y3 + " 4x: " + mTetrisBlock.x4 + " 4y: " + mTetrisBlock.y4);
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
    	oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1] = true;
    	oldBlocks[mTetrisBlock.x2][mTetrisBlock.y2] = true;
    	oldBlocks[mTetrisBlock.x3][mTetrisBlock.y3] = true;
    	oldBlocks[mTetrisBlock.x4][mTetrisBlock.y4] = true;
    	

    }
    
    public void updateFalling() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastTimedMove > mTimeDelay)
            {
//                mTetrisBlock.y = mTetrisBlock.y + 1;
                if (!((mTetrisBlock.y1 == mYTileCount - 2) || (mTetrisBlock.y2 == mYTileCount - 2) || (mTetrisBlock.y3 == mYTileCount - 2) || (mTetrisBlock.y4 == mYTileCount - 2)))
                {
                    if(!checkCollision())
                    	mTetrisBlock.fall();
                }
                mLastTimedMove = now;
            }
            checkCollision();
        }
    }

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
    	if (mTetrisBlock.y1 > (mYTileCount - 3) || mTetrisBlock.y2 > (mYTileCount - 3) || mTetrisBlock.y3 > (mYTileCount - 3) || mTetrisBlock.y4 > (mYTileCount - 3))
        {
        	saveBlocks();
        	initNewBlock();
            clearRow();
    		return true;
        }
//    	Log.v(TAG, mTetrisBlock.y1 + " " + mTetrisBlock.y2 + " " + mTetrisBlock.y3 + " " + mTetrisBlock.y4 + " ;");
        if (oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1 + 1] || oldBlocks[mTetrisBlock.x2][mTetrisBlock.y2 + 1] || oldBlocks[mTetrisBlock.x3][mTetrisBlock.y3 + 1] || oldBlocks[mTetrisBlock.x4][mTetrisBlock.y4 + 1])
        {
        	saveBlocks();
        	initNewBlock();
            clearRow();                
    		return true;
        }
    	
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
//    		Log.v(TAG, "z: " + z);
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
        mTetrisBlock.rotateClockwise();
        
        if (((mTetrisBlock.x1 < 2) || (mTetrisBlock.x2 < 2) || (mTetrisBlock.x3 < 2) || (mTetrisBlock.x4 < 2)))
        {
        	mTetrisBlock.x1 = 2;
        	mTetrisBlock.refreshBlock();
        }
    	if ((mTetrisBlock.x1 < 3) && (mTetrisBlock.getBlockType() == IBLOCK))
		{
			if (mTetrisBlock.getOrientation() == 0)
			{
	    		mTetrisBlock.x1 = 3;
	        	mTetrisBlock.refreshBlock();
			}
			if (mTetrisBlock.getOrientation() == 2)
	    	{
	    		mTetrisBlock.x1 = 2;
	        	mTetrisBlock.refreshBlock();
	    	}
		}

        if (((mTetrisBlock.x1 == (mXTileCount - 2)) || (mTetrisBlock.x2 == (mXTileCount - 2)) || (mTetrisBlock.x3 == (mXTileCount - 2)) || (mTetrisBlock.x4 == (mXTileCount - 2))))
        {
        	mTetrisBlock.x1 = mXTileCount - 3;
        	mTetrisBlock.refreshBlock();
        }
    	if ((mTetrisBlock.x1 > mXTileCount - 5) && (mTetrisBlock.getBlockType() == IBLOCK))
		{
			if (mTetrisBlock.getOrientation() == 0)
			{
	    		mTetrisBlock.x1 = mXTileCount - 3;
	        	mTetrisBlock.refreshBlock();
			}
			if (mTetrisBlock.getOrientation() == 2)
	    	{
	    		mTetrisBlock.x1 = mXTileCount - 4;
	        	mTetrisBlock.refreshBlock();
	    	}
		}
    	
    	if ((mTetrisBlock.y1 > mYTileCount - 4) && (mTetrisBlock.getBlockType() == IBLOCK))
		{
			if (mTetrisBlock.getOrientation() == 1)
			{
	    		mTetrisBlock.y1 = mYTileCount - 4;
	        	mTetrisBlock.refreshBlock();
			}
//			if (mTetrisBlock.getOrientation() == 3)
//	    	{
//	    		mTetrisBlock.y1 = mYTileCount - 3;
//	        	mTetrisBlock.refreshBlock();
//	    	}
		}

    	if ((mTetrisBlock.getBlockType() == IBLOCK) && (oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1 + 2]))
    	{
    		mTetrisBlock.y1 -= 1;
    		mTetrisBlock.refreshBlock();
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
    	setTile(UNIT, mTetrisBlock.x1, mTetrisBlock.y1);
    	setTile(UNIT, mTetrisBlock.x2, mTetrisBlock.y2);
    	setTile(UNIT, mTetrisBlock.x3, mTetrisBlock.y3);
    	setTile(UNIT, mTetrisBlock.x4, mTetrisBlock.y4);
        
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
