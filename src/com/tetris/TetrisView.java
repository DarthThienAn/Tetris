package com.tetris;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
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
	public static final int PAUSE = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int LOSE = 3;
	public static final int WIN = 4;

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
	private static final int WALLTOP = 9;

	/**
	 * mScore: used to keep score mMoveDelay: number of milliseconds between
	 * Tetris movements. This will decrease over time.
	 */
	private static final long mMoveDelay = 50;

	/**
	 * mLastMove: tracks the absolute time when the Tetris last moved, and is
	 * used to determine if a move should be made based on mMoveDelay.
	 */
	private long mLastMove;

	/**
	 * mStatusText: text shows to the user in some run states
	 */
	private TextView mStatusText;

	/**
	 * mTetrisGame: a game state containing all the relevant information about
	 * the game.
	 */
	private TetrisGame mTetrisGame;

	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
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

	public TetrisView(Context context, AttributeSet attrs, int defStyle, TetrisGame a) {
		super(context, attrs, defStyle);
		initTetrisView();
		mTetrisGame = a;
	}
	
	private void initTetrisView() {
		setFocusable(true);

		Resources r = this.getContext().getResources();

		resetTiles(10);
		loadTile(BLUEUNIT, r.getDrawable(R.drawable.blueunit));
		loadTile(BROWNUNIT, r.getDrawable(R.drawable.brownunit));
		loadTile(CYANUNIT, r.getDrawable(R.drawable.cyanunit));
		loadTile(GREENUNIT, r.getDrawable(R.drawable.greenunit));
		loadTile(ORANGEUNIT, r.getDrawable(R.drawable.orangeunit));
		loadTile(PURPLEUNIT, r.getDrawable(R.drawable.purpleunit));
		loadTile(REDUNIT, r.getDrawable(R.drawable.redunit));
		loadTile(WALL, r.getDrawable(R.drawable.wall));
		loadTile(WALLTOP, r.getDrawable(R.drawable.walltop));

		if (mTetrisGame == null)
			mTetrisGame = new TetrisGame();
	}

	/**
	 * Save game state so that the user does not lose anything if the game
	 * process is killed while we are in the background.
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
	 * @param savedState
	 *            a Bundle containing the game state
	 */
	public void restoreState(Bundle savedState) {
		setMode(PAUSE);
		mTetrisGame = new TetrisGame(new TetrisBlock(
				savedState.getInt("mTetrisBlock1x"),
				savedState.getInt("mTetrisBlock1y"),
				savedState.getInt("mTetrisBlockType"),
				savedState.getInt("mTetrisBlockOrientation")),
				savedState.getLong("mScore"), savedState.getLong("mTimeDelay"));
	}

	public boolean pressKey(int input) {
		if ((mTetrisGame.getMode() == LOSE) || (mTetrisGame.getMode() == WIN)) {
			System.exit(0);
		}

		switch (input) {
		case 0: {
			return false;
		}
		case 1: {
			mTetrisGame.update(1);
			update();
			return (true);
		}
		case 2: {
			mTetrisGame.update(2);
			return (true);
		}
		case 3: {
			mTetrisGame.update(3);
			return (true);
		}
		case 4: {
			mTetrisGame.update(4);
			return (true);
		}
		case 5: {
			mTetrisGame.update(5);
			return (true);
		}
		}
		return false;
	}

	public int getBlockType() {
		return mTetrisGame.getBlockType();
	}

	public int getMode() {
		return mTetrisGame.getMode();
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
	 * Updates the current mode of the application (RUNNING or PAUSED or the
	 * like) as well as sets the visibility of textview for notification
	 * 
	 * @param newMode
	 */
	public void setMode(int newMode) {
		if (newMode == RUNNING & mTetrisGame.getMode() != RUNNING) {
			mTetrisGame.setMode(newMode);
			mStatusText.setVisibility(View.INVISIBLE);
			update();
			return;
		}

		mTetrisGame.setMode(newMode);

		CharSequence str = "";
		if (newMode == READY) {
			str = "Tetris\nPress Any Key to Begin";
		} else if (newMode == PAUSE) {
			str = "Paused\nPress Up To Resume";
		} else if (newMode == LOSE) {
			str = "Game Over - you lose!\nScore: " + mTetrisGame.getScore()
					+ "";
		} else if (newMode == WIN) {
			str = "Victory - you win!\nScore: " + mTetrisGame.getScore() + "";
		}

		mStatusText.setText(str);
		mStatusText.setVisibility(View.VISIBLE);
	}

	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the Tetris's
	 * location.
	 */
	public void update() {

		mTetrisGame.checkTop();
		mTetrisGame.update(0);
		setMode(mTetrisGame.getMode());

		if (mTetrisGame.getMode() == RUNNING) {
			long now = System.currentTimeMillis();

			if (now - mLastMove > mMoveDelay) {
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
	 * Figure out which way the Tetris is going, see if he's run into anything
	 * (the walls, himself). If he's not going to die, we then add to the front
	 * and subtract from the rear in order to simulate motion. If we want to
	 * grow him, we don't subtract from the rear.
	 * 
	 */

	private void drawBlock() {
		int unit = mTetrisGame.getBlockType();
		TetrisBlock mTetrisBlock = new TetrisBlock(mTetrisGame.getX(),
				mTetrisGame.getY(), unit, mTetrisGame.getOrientation());

		setTile(unit, mTetrisBlock.x1, mTetrisBlock.y1);
		setTile(unit, mTetrisBlock.x2, mTetrisBlock.y2);
		setTile(unit, mTetrisBlock.x3, mTetrisBlock.y3);
		setTile(unit, mTetrisBlock.x4, mTetrisBlock.y4);

		for (int x = 0; x < mXTileCount; x++) {
			for (int y = 0; y < mYTileCount; y++) {
				if (mTetrisGame.getBlocks(x, y)) {
					setTile(mTetrisGame.getColors(x, y), x, y);
				}
			}
		}
	}
}