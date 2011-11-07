package com.tetris;

//import java.util.ArrayList;
//import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
//import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/**
 * TetrisView: implementation of a simple game of Tetris
 */
public class TetrisView extends TileView {

//    private static final String TAG = "TetrisView";

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

    /**
     * Current direction the Tetris is headed (input).
     */
//    private int mDirection = SOUTH;
//    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;

    /**
     * Current direction the Tetris is facing.
     */
    private int mOrientation = FACEUP;
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
     * mTetrisPiece: a list of Coordinates that make up the Tetris piece
     */
//    private ArrayList<Coordinate> mTetrisPiece = new ArrayList<Coordinate>();
    private Coordinate mTetrisPiece = new Coordinate(12, 2);
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
//        mTetrisPiece.clear();
//        mTetrisPiece.add(new Coordinate(12, 2));
    	
    	mTetrisPiece = new Coordinate(12, 2);
//        mDirection = SOUTH;
        mOrientation = FACEUP;

//        mMoveDelay = 50;
        mTimeDelay = 1000;
        mScore = 0;
    }

    private void initNewBlock()
    {
    	mTetrisPiece = new Coordinate(12, 2);
    	mOrientation = FACEUP;
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

//        map.putInt("mDirection", Integer.valueOf(mDirection));
//        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putLong("mTimeDelay", Long.valueOf(mTimeDelay));
        map.putLong("mScore", Long.valueOf(mScore));
//        map.putIntArray("mTetrisPiece", coordArrayListToArray(mTetrisPiece));
        map.putInt("mTetrisPieceX", Integer.valueOf(mTetrisPiece.x));
        map.putInt("mTetrisPieceY", Integer.valueOf(mTetrisPiece.y));

        return map;
    }

    /**
     * Restore game state if our process is being relaunched
     * 
     * @param savedState a Bundle containing the game state
     */
    public void restoreState(Bundle savedState) {
        setMode(PAUSE);

//        mDirection = savedState.getInt("mDirection");
//        mMoveDelay = savedState.getLong("mMoveDelay");
        mTimeDelay = savedState.getLong("mTimeDelay");
        mScore = savedState.getLong("mScore");
//        mTetrisPiece = coordArrayToArrayList(savedState.getIntArray("mTetrisPiece"));
        mTetrisPiece = new Coordinate(savedState.getInt("mTetrisPieceX"), savedState.getInt("mTetrisPieceY"));
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
/*                clearTiles();
                updateWalls();
                drawBlock();*/
//                updateTetris();
//                mLastMove = System.currentTimeMillis();
//                mRedrawHandler.sleep(mMoveDelay);
                return (true);
            } 
            if (mMode == PAUSE) {
                /*
                 * If the game is merely paused, we should just continue where
                 * we left off.
                 */
                setMode(RUNNING);
                update();
/*                clearTiles();
                updateWalls();
                drawBlock();*/
//                updateTetris();
//                mLastMove = System.currentTimeMillis();
//                mRedrawHandler.sleep(mMoveDelay);
                return (true);
            } 

            if (mMode == RUNNING) {
//            mDirection = NORTH;
                mOrientation = (mOrientation + 1) % 4;
                update(); //slightly inefficient, runs updateFalling when unnecessary
/*                clearTiles();
                updateWalls();
            	drawBlock();*/
            return (true);
            }
        } 
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        	updateInput(SOUTH);
//        	mDirection = SOUTH;
                return (true);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
        	updateInput(WEST);
//        	mDirection = WEST;
            return (true);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	updateInput(EAST);
//        	mDirection = EAST;
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
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastMove > mMoveDelay) {
                clearTiles();
                updateWalls();
                updateFalling();

                // Collision detection
                // For now we have a 1-square wall around the entire arena
                if ((mTetrisPiece.x < 1) || (mTetrisPiece.y < 1) || (mTetrisPiece.x > mXTileCount - 2)
                        || (mTetrisPiece.y > mYTileCount - 2)) {
//                	saveBlocks();
                	initNewBlock();
//                    setMode(LOSE);
                    return;

                }
                
                drawBlock();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }
    }

/*    private boolean[][] oldBlocks = new boolean[mXTileCount][mYTileCount];
    
    private void saveBlocks()
    {
        switch (mOrientation) {
	        case FACEUP: {
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x + 1][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x - 1][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y - 1] = true;
	        	break;
	        }
	        case FACERIGHT: {
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x + 1][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y + 1] = true;
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y - 1] = true;
	        	break;
	        }
	        case FACEDOWN: {
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x + 1][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x - 1][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y + 1] = true;
		        break;
	        }
	        case FACELEFT: {
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x - 1][mTetrisPiece.y] = true;
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y + 1] = true;
	        	oldBlocks[mTetrisPiece.x][mTetrisPiece.y - 1] = true;
		        break;
	        }
	    }
    }*/
    
    
/*    public void updateFalling() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastTimedMove > mTimeDelay) {
                clearTiles();
                updateWalls();

                mTetrisPiece.y = mTetrisPiece.y + 1;
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
                mTetrisPiece.y = mTetrisPiece.y + 1;
                mLastTimedMove = now;
            }
        }
    }
    
    public void updateInput(int mInputDirection)
    {
        if (mMode == RUNNING) {
            switch (mInputDirection) {
    	        case EAST: {
    	            mTetrisPiece.x = mTetrisPiece.x + 1;
    	            break;
    	        }
    	        case WEST: {
    	            mTetrisPiece.x = mTetrisPiece.x - 1;
    	            break;
    	        }
    	        case SOUTH: {
    	            mTetrisPiece.y = mTetrisPiece.y + 1;
    	            break;
    	        }
            }

            //prevent sides
            if (mTetrisPiece.x < 1)
            	mTetrisPiece.x = 2;
            if (mTetrisPiece.x > mXTileCount - 2)
            	initNewBlock();
//            	mTetrisPiece.x = mXTileCount - 2;

            //prevent above
            if (mTetrisPiece.y < 1)
            	mTetrisPiece.y = 2;
            if (mTetrisPiece.y > mYTileCount - 2)
            	mTetrisPiece.y = mYTileCount - 2;
            
/*            clearTiles();
            updateWalls();
            drawBlock();*/
//            mRedrawHandler.sleep(mMoveDelay);
        }
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
/*    private void updateTetris() {

//        Coordinate head = mTetrisPiece;
        Coordinate newHead = new Coordinate(1, 1);

/*        switch (mDirection) {
	        case EAST: {
	            newHead = new Coordinate(head.x + 1, head.y);
	            break;
	        }
	        case WEST: {
	            newHead = new Coordinate(head.x - 1, head.y);
	            break;
	        }
	        case SOUTH: {
	            newHead = new Coordinate(head.x, head.y + 1);
	            break;
	        }
	        //rotate
	        case NORTH: {
	            newHead = new Coordinate(head.x, head.y);
	            mOrientation = (mOrientation + 1) % 4;
	            break;
	        }
        }

        // Collision detection
        // For now we have a 1-square wall around the entire arena
        if ((newHead.x < 1) || (newHead.y < 1) || (newHead.x > mXTileCount - 2)
                || (newHead.y > mYTileCount - 2)) {
            setMode(LOSE);
            return;

        }

        // push a new head onto the ArrayList and pull off the tail
//        mTetrisPiece.add(0, newHead);
//        mTetrisPiece.remove(mTetrisPiece.size() - 1);

        mTetrisPiece = new Coordinate(newHead.x, newHead.y);
//        mDirection = NORTH;
        drawBlock();
    }*/

    private void drawBlock()
    {
        switch (mOrientation) {
	        case FACEUP: {
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x - 1, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x + 1, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y - 1);
		        break;
	        }
	        case FACERIGHT: {
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x + 1, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y + 1);
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y - 1);
		        break;
	        }
	        case FACEDOWN: {
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x - 1, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x + 1, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y + 1);
		        break;
	        }
	        case FACELEFT: {
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x - 1, mTetrisPiece.y);
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y + 1);
		        setTile(UNIT, mTetrisPiece.x, mTetrisPiece.y - 1);
		        break;
	        }
	    }
        
/*        int x = 0;
        int y = 0;
        
        for (x : oldBlocks[x][y])
        {
            for (y : oldBlocks[x][y])
            {
	            if (oldBlocks[x][y]) 
	            {
	                setTile(UNIT, x, y);
	            }
	            y++;
            }
            x++;
        }*/

/*        for (int x = 0; x < mXTileCount; x++)
        {
            for (int y = 0; y < mYTileCount; y++)
            {
	            if (oldBlocks[x][y]) 
	            {
	                setTile(UNIT, x, y);
	            }
            }
        }*/
        
        
    }
    
    /**
     * Simple class containing two integer values and a comparison function.
     * There's probably something I should use instead, but this was quick and
     * easy to build.
     * 
     */
    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

/*        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }*/

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }
    
}
