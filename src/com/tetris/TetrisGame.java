/***
	"Multiplayer Tetris" is an application that offers online Tetris play
	Copyright (C) 2012 Mark Ha

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***/

package com.tetris;

import java.util.Random;

/**
 * TetrisView: implementation of a simple game of Tetris
 */
public class TetrisGame {

	//	private static final String TAG = "TetrisGame";

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
	public static final int WIN = 4;

	// constants for direction
	private static final int SOUTH = 2;
	private static final int EAST = 3;
	private static final int WEST = 4;

	// types
	private static final int IBLOCK = 1;
	//	private static final int JBLOCK = 2;
	//	private static final int LBLOCK = 3;
	//	private static final int OBLOCK = 4;
	//	private static final int SBLOCK = 5;
	//	private static final int TBLOCK = 6;
	//	private static final int ZBLOCK = 7;

	/**
	 * mScore: used to keep score mMoveDelay: number of milliseconds between
	 * Tetris movements. This will decrease over time.
	 */
	private long mScore;
	private long mTimeDelay;

	/**
	 * mLastMove: tracks the absolute time when the Tetris last moved, and is
	 * used to determine if a move should be made based on mMoveDelay.
	 */
	private long mLastTimedMove;

	/**
	 * mTetrisBlock: a list of Coordinates that make up the Tetris piece
	 */
	private TetrisBlock mTetrisBlock = new TetrisBlock(xSize / 2, 1,
			1 + RNG.nextInt(7));
	private boolean[][] oldBlocks = new boolean[xSize][ySize];
	private int[][] savedColors = new int[xSize][ySize];

	/**
	 * Everyone needs a little randomness in their life
	 */
	private static final Random RNG = new Random();

	/**
	 * dimensions of the Tetris world.
	 */
	private static final int xSize = 12;
	private static final int ySize = 22;

	public TetrisGame() {
		mTetrisBlock = new TetrisBlock(xSize / 2, 1, 1 + RNG.nextInt(7));
		oldBlocks = new boolean[xSize][ySize];
		savedColors = new int[xSize][ySize];
		mScore = 0;
		mTimeDelay = 1000;
		mMode = READY;
	}

	public TetrisGame(int blockType) {
		mTetrisBlock = new TetrisBlock(xSize / 2, 1, blockType);
		oldBlocks = new boolean[xSize][ySize];
		savedColors = new int[xSize][ySize];
		mScore = 0;
		mTimeDelay = 1000;
		mMode = RUNNING;
	}

	public TetrisGame(TetrisBlock mTetrisBlock, long mScore, long mTimeDelay) {
		this.mTetrisBlock = mTetrisBlock;
		oldBlocks = new boolean[xSize][ySize];
		savedColors = new int[xSize][ySize];
		this.mScore = mScore;
		this.mTimeDelay = mTimeDelay;
		mMode = READY;
	}

	private void initNewGame() {
		mTetrisBlock = new TetrisBlock(xSize / 2, 1, 1 + RNG.nextInt(7));
		oldBlocks = new boolean[xSize][ySize];
		savedColors = new int[xSize][ySize];
		mScore = 0;
		mTimeDelay = 1000;
	}

	public void updateFalling() {
		if (mMode == RUNNING) {
			long now = System.currentTimeMillis();

			if (now - mLastTimedMove > mTimeDelay) {
				if (!checkCollision())
					mTetrisBlock.fall();
				mLastTimedMove = now;
			}
		}
	}

	public TetrisBlock getTetrisBlock() {
		return mTetrisBlock;
	}

	public long getScore() {
		return mScore;
	}

	public int getMode() {
		return mMode;
	}

	public long getTimeDelay() {
		return mTimeDelay;
	}

	public boolean getBlocks(int x, int y) {
		return oldBlocks[x][y];
	}

	public int getColors(int x, int y) {
		return savedColors[x][y];
	}

	public int getOrientation() {
		return mTetrisBlock.getOrientation();
	}

	public int getBlockType() {
		return mTetrisBlock.getBlockType();
	}

	public int getX() {
		return mTetrisBlock.x1;
	}

	public int getY() {
		return mTetrisBlock.y1;
	}

	public int update(int cmd) {
		inputCmd(cmd);
		checkTop();
		updateFalling();
		clearRow();
		return mMode;
	}

	public void inputCmd(int cmd) {
		// up = 1, down = 2, right = 3, left = 4, space = 5
		// no input = 0
		switch (cmd) {
		case 0: {
			break;
		}
		case 1: {
			if (mMode == READY) {
				/*
				 * At the beginning of the game, or the end of a previous one,
				 * we should start a new game.
				 */
				initNewGame();
				setMode(RUNNING);
				checkTop();
				updateFalling();
				clearRow();
				break;
			}
			if (mMode == PAUSE) {
				/*
				 * If the game is merely paused, we should just continue where
				 * we left off.
				 */
				setMode(RUNNING);
				checkTop();
				updateFalling();
				clearRow();
				break;
			}
			if (mMode == RUNNING) {
				rotateClockwise();
				checkTop();
				clearRow();
				break;
			}
		}
		case 2: {
			// if not at the bottom
			if (!checkCollision()) {
				if (!((mTetrisBlock.y1 == ySize - 2)
						|| (mTetrisBlock.y2 == ySize - 2)
						|| (mTetrisBlock.y3 == ySize - 2) || (mTetrisBlock.y4 == ySize - 2))) {
					// if not directly above existing pieces
					if (!((oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1 + 1])
							|| (oldBlocks[mTetrisBlock.x2][mTetrisBlock.y2 + 1])
							|| (oldBlocks[mTetrisBlock.x3][mTetrisBlock.y3 + 1]) || (oldBlocks[mTetrisBlock.x4][mTetrisBlock.y4 + 1]))) {
						mTetrisBlock.moveBlock(SOUTH);
					}
				}
			}
			break;
		}
		case 3: {
			// if not on the left edge
			if (!((mTetrisBlock.x1 < 2) || (mTetrisBlock.x2 < 2)
					|| (mTetrisBlock.x3 < 2) || (mTetrisBlock.x4 < 2))) {
				// if not directly to the right of existing pieces
				if (!((oldBlocks[mTetrisBlock.x1 - 1][mTetrisBlock.y1])
						|| (oldBlocks[mTetrisBlock.x2 - 1][mTetrisBlock.y2])
						|| (oldBlocks[mTetrisBlock.x3 - 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 - 1][mTetrisBlock.y4])))
					mTetrisBlock.moveBlock(WEST);
			}
			break;
		}
		case 4: {
			// if not on the right edge
			if (!((mTetrisBlock.x1 == (xSize - 2))
					|| (mTetrisBlock.x2 == (xSize - 2))
					|| (mTetrisBlock.x3 == (xSize - 2)) || (mTetrisBlock.x4 == (xSize - 2)))) {
				// if not directly to the left of existing pieces
				if (!((oldBlocks[mTetrisBlock.x1 + 1][mTetrisBlock.y1])
						|| (oldBlocks[mTetrisBlock.x2 + 1][mTetrisBlock.y2])
						|| (oldBlocks[mTetrisBlock.x3 + 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 + 1][mTetrisBlock.y4])))
					mTetrisBlock.moveBlock(EAST);
			}
			break;
		}
		case 5: {
			// as long as it doesn't hit anything on the way down
			while (!checkCollision())
				mTetrisBlock.moveBlock(SOUTH);
			break;
		}
		}
	}

	/**
	 * creates a new block at the top, and adds 100 to the player's score.
	 */
	private void initNewBlock() {
		if (!checkTop()) {
			// generate a new block of random type that is not the type of the
			// previous block
			int rng = 1 + RNG.nextInt(7);
			while (rng == mTetrisBlock.getBlockType())
				rng = 1 + RNG.nextInt(7);

			mTetrisBlock = new TetrisBlock(xSize / 2, 1, rng);
			mScore += 100;
		}
	}

	public void newBlock(int blockType, int x, int y) {
		mTetrisBlock = new TetrisBlock(xSize / 2, 1, blockType);
	}

	/**
	 * Updates the current mode of the application (RUNNING or PAUSED or the
	 * like) as well as sets the visibility of textview for notification
	 * 
	 * @param newMode
	 */
	public void setMode(int newMode) {
		mMode = newMode;
	}

	private void saveBlocks() {
		oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1] = true;
		oldBlocks[mTetrisBlock.x2][mTetrisBlock.y2] = true;
		oldBlocks[mTetrisBlock.x3][mTetrisBlock.y3] = true;
		oldBlocks[mTetrisBlock.x4][mTetrisBlock.y4] = true;

		savedColors[mTetrisBlock.x1][mTetrisBlock.y1] = mTetrisBlock
				.getBlockType();
		savedColors[mTetrisBlock.x2][mTetrisBlock.y2] = mTetrisBlock
				.getBlockType();
		savedColors[mTetrisBlock.x3][mTetrisBlock.y3] = mTetrisBlock
				.getBlockType();
		savedColors[mTetrisBlock.x4][mTetrisBlock.y4] = mTetrisBlock
				.getBlockType();
	}

	/**
	 * Check to see if the game is over
	 * 
	 * @return true when game is over, false otherwise
	 */
	public boolean checkTop() {
		for (int x = 0; x < xSize; x++) {
			if (oldBlocks[x][1]) {
				setMode(LOSE);
				return true;
			}
		}

		return false;
	}

	/**
	 * Collision detection, and handler. Return true if collision, false if not.
	 */
	public boolean checkCollision() {
		// if it hits the bottom
		if (mTetrisBlock.y1 > (ySize - 3) || mTetrisBlock.y2 > (ySize - 3)
				|| mTetrisBlock.y3 > (ySize - 3)
				|| mTetrisBlock.y4 > (ySize - 3)) {
			saveBlocks();
			initNewBlock();
			clearRow();
			return true;
		}

		// if it runs into a block
		if (oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1 + 1]
				|| oldBlocks[mTetrisBlock.x2][mTetrisBlock.y2 + 1]
						|| oldBlocks[mTetrisBlock.x3][mTetrisBlock.y3 + 1]
								|| oldBlocks[mTetrisBlock.x4][mTetrisBlock.y4 + 1]) {
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
	public void clearRow() {
		boolean full = true;
		boolean[][] temp = new boolean[xSize][ySize];
		int[][] tempColors = new int[xSize][ySize];

		for (int z = ySize - 2; z > 1; z--) {
			for (int x = 1; x < xSize - 1; x++) {
				if (!oldBlocks[x][z])
					full = false;

				// ideally figure out way to break loop sooner
				// if (!full)
				// return;
			}

			if (full) {
				mScore += 1000;
				for (int x = 0; x < xSize; x++) {
					oldBlocks[x][z] = false;
					savedColors[x][z] = 0;
				}

				for (int x = 1; x < xSize - 1; x++) {
					for (int y = z; y > 1; y--) {
						temp[x][y] = oldBlocks[x][y - 1];
						tempColors[x][y] = savedColors[x][y - 1];
					}
					for (int y = z + 1; y < ySize; y++) {
						temp[x][y] = oldBlocks[x][y];
						tempColors[x][y] = savedColors[x][y];
					}
				}
				oldBlocks = temp;
				savedColors = tempColors;
			}

			full = true;
		}
	}

	// figure out edge cases
	public void rotateClockwise() {
		TetrisBlock mTempBlock = new TetrisBlock(mTetrisBlock.x1,
				mTetrisBlock.y1, mTetrisBlock.getBlockType(),
				mTetrisBlock.getOrientation());

		mTempBlock.rotateClockwise();
		if ((oldBlocks[mTempBlock.x1][mTempBlock.y1])
				|| (oldBlocks[mTempBlock.x2][mTempBlock.y2])
				|| (oldBlocks[mTempBlock.x3][mTempBlock.y3])
				|| (oldBlocks[mTempBlock.x4][mTempBlock.y4]))
			return;
		else {
			mTetrisBlock.rotateClockwise();

			// left side
			if (((mTetrisBlock.x1 < 2) || (mTetrisBlock.x2 < 2)
					|| (mTetrisBlock.x3 < 2) || (mTetrisBlock.x4 < 2))) {
				mTetrisBlock.x1 = 2;
				mTetrisBlock.refreshBlock();
			}

			// right side
			if (((mTetrisBlock.x1 > (xSize - 3))
					|| (mTetrisBlock.x2 > (xSize - 3))
					|| (mTetrisBlock.x3 > (xSize - 3)) || (mTetrisBlock.x4 > (xSize - 3)))) {
				mTetrisBlock.x1 = xSize - 3;
				mTetrisBlock.refreshBlock();
			}

			// bottom
			if ((mTetrisBlock.y1 == (ySize - 2))
					|| (mTetrisBlock.y2 == (ySize - 2))
					|| (mTetrisBlock.y3 == (ySize - 2))
					|| (mTetrisBlock.y4 == (ySize - 2)))
				// || (mTetrisBlock.y2 == (ySize - 4)) || (mTetrisBlock.y3 == (ySize
				// - 4)) || (mTetrisBlock.y4 == (ySize - 4))))
			{
				mTetrisBlock.y1 = ySize - 3;
				mTetrisBlock.refreshBlock();
			}

			// IBLOCK, bottom
			if ((mTetrisBlock.getBlockType() == IBLOCK)
					&& (mTetrisBlock.y1 > ySize - 4)) {
				if (mTetrisBlock.getOrientation() == 1) {
					mTetrisBlock.y1 = ySize - 4;
					mTetrisBlock.refreshBlock();
				}
			}

			// IBLOCK, left side
			if ((mTetrisBlock.getBlockType() == IBLOCK)
					&& (mTetrisBlock.x1 < 3)) {
				if (mTetrisBlock.getOrientation() == 0) {
					mTetrisBlock.x1 = 3;
					mTetrisBlock.refreshBlock();
				}
				if (mTetrisBlock.getOrientation() == 2) {
					mTetrisBlock.x1 = 2;
					mTetrisBlock.refreshBlock();
				}
			}

			// IBLOCK, right side
			if ((mTetrisBlock.getBlockType() == IBLOCK)
					&& (mTetrisBlock.x1 > xSize - 5)) {
				if (mTetrisBlock.getOrientation() == 0) {
					mTetrisBlock.x1 = xSize - 3;
					mTetrisBlock.refreshBlock();
				}
				if (mTetrisBlock.getOrientation() == 2) {
					mTetrisBlock.x1 = xSize - 4;
					mTetrisBlock.refreshBlock();
				}
			}

			// IBLOCK, conflict with oldBlocks
			if ((mTetrisBlock.getBlockType() == IBLOCK)
					&& (oldBlocks[mTetrisBlock.x1][mTetrisBlock.y1 + 2])) {
				mTetrisBlock.y1 -= 1;
				mTetrisBlock.refreshBlock();
			}

			// right, conflict with blocks
			if (mTetrisBlock.x1 < (xSize - 3) || mTetrisBlock.x2 < (xSize - 3)
					|| mTetrisBlock.x3 < (xSize - 3)
					|| mTetrisBlock.x4 < (xSize - 3)) {
				if (((oldBlocks[mTetrisBlock.x1 + 1][mTetrisBlock.y1])
						|| (oldBlocks[mTetrisBlock.x2 + 1][mTetrisBlock.y2])
						|| (oldBlocks[mTetrisBlock.x3 + 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 + 1][mTetrisBlock.y4]))) {
					mTetrisBlock.x1 -= 1;
					mTetrisBlock.refreshBlock();
				}
			}

			// left, conflict with blocks
			if (mTetrisBlock.x1 > 2 || mTetrisBlock.x2 > 2
					|| mTetrisBlock.x3 > 2 || mTetrisBlock.x4 > 2) {
				if (((oldBlocks[mTetrisBlock.x1 - 1][mTetrisBlock.y1])
						|| (oldBlocks[mTetrisBlock.x2 - 1][mTetrisBlock.y2])
						|| (oldBlocks[mTetrisBlock.x3 - 1][mTetrisBlock.y3]) || (oldBlocks[mTetrisBlock.x4 - 1][mTetrisBlock.y4]))) {
					mTetrisBlock.x1 += 1;
					mTetrisBlock.refreshBlock();
				}
			}
		}
	}
}