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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 */
public class TileView2 extends View {

	/**
	 * Parameters controlling the size of the tiles and their range within view.
	 * Width/Height are in pixels, and Drawables will be scaled to fit to these
	 * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
	 */

	protected static int mTileSize;

	// want it to be 10x20, add 2 to each dimension for walls.
	protected final static int mXTileCount = 12;
	protected final static int mYTileCount = 22;

	private static int mXOffset;
	private static int mYOffset;

	/**
	 * A hash that maps integer handles specified by the subclasser to the
	 * drawable that will be used for that reference
	 */
	private Bitmap[] mTileArray;

	/**
	 * A two-dimensional array of integers in which the number represents the
	 * index of the tile that should be drawn at that locations
	 */
	private int[][] mTileGrid;

	private final Paint mPaint = new Paint();

	public TileView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		DisplayMetrics display = getResources().getDisplayMetrics();
		int w = display.widthPixels;
		mTileSize = (int) (Math.floor(w / mYTileCount) * .9);
	}

	public TileView2(Context context, AttributeSet attrs) {
		super(context, attrs);

		DisplayMetrics display = getResources().getDisplayMetrics();
		int w = display.widthPixels;
		mTileSize = (int) (Math.floor(w / mYTileCount) * .9);
	}

	/**
	 * Rests the internal array of Bitmaps used for drawing tiles, and sets the
	 * maximum index of tiles to be inserted
	 * 
	 * @param tilecount
	 */

	public void resetTiles(int tilecount) {
		mTileArray = new Bitmap[tilecount];
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mXOffset = (w / 2);
		mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

		mTileGrid = new int[mXTileCount][mYTileCount];
		clearTiles();
	}

	/**
	 * Function to set the specified Drawable as the tile for a particular
	 * integer key.
	 * 
	 * @param key
	 * @param tile
	 */
	public void loadTile(int key, Drawable tile) {
		Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, mTileSize, mTileSize);
		tile.draw(canvas);

		mTileArray[key] = bitmap;
	}

	/**
	 * Resets all tiles to 0 (empty)
	 * 
	 */
	public void clearTiles() {
		for (int x = 0; x < mXTileCount; x++) {
			for (int y = 0; y < mYTileCount; y++) {
				setTile(0, x, y);
			}
		}
	}

	/**
	 * Used to indicate that a particular tile (set with loadTile and referenced
	 * by an integer) should be drawn at the given x/y coordinates during the
	 * next invalidate/draw cycle.
	 * 
	 * @param tileindex
	 * @param x
	 * @param y
	 */
	public void setTile(int tileindex, int x, int y) {
		if (mTileGrid == null)
			mTileGrid = new int[mXTileCount][mYTileCount];

		mTileGrid[x][y] = tileindex;
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (int x = 0; x < mXTileCount; x += 1) {
			for (int y = 0; y < mYTileCount; y += 1) {
				if (mTileGrid[x][y] > 0) {
					canvas.drawBitmap(mTileArray[mTileGrid[x][y]], mXOffset + x
							* mTileSize, mYOffset + y * mTileSize, mPaint);
				}
			}
		}
	}
<<<<<<< HEAD
}
=======
}
>>>>>>> 4b16c8423fb0c89e7a18f916f0d60db3ba978cb0
