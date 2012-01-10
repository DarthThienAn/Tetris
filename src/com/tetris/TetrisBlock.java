package com.tetris;

public class TetrisBlock {
	private static final String TAG = "TetrisView";

	private int blockType;
	private static final int IBLOCK = 1;
	private static final int JBLOCK = 2;
	private static final int LBLOCK = 3;
	private static final int OBLOCK = 4;
	private static final int SBLOCK = 5;
	private static final int TBLOCK = 6;
	private static final int ZBLOCK = 7;
	private static final int BLACK = 8;

	private int mOrientation = FACEUP;
	private static final int FACEUP = 0;
	private static final int FACERIGHT = 1;
	private static final int FACEDOWN = 2;
	private static final int FACELEFT = 3;

	private static final int SOUTH = 2;
	private static final int EAST = 3;
	private static final int WEST = 4;

	public int x1;
	public int x2;
	public int x3;
	public int x4;
	public int y1;
	public int y2;
	public int y3;
	public int y4;

	public TetrisBlock(int newx1, int newy1, int newBlockType) {
		x1 = newx1;
		y1 = newy1;
		blockType = newBlockType;
		mOrientation = FACEUP;
		refreshBlock();
	}

	public TetrisBlock(int newx1, int newy1, int newBlockType,
			int newOrientation) {
		x1 = newx1;
		y1 = newy1;
		blockType = newBlockType;
		mOrientation = newOrientation;
		refreshBlock();
	}

	public int getOrientation() {
		return mOrientation;
	}

	public int getBlockType() {
		return blockType;
	}

	public void setBlockType(int blockType) {
		this.blockType = blockType;
	}

	public void fall() {
		y1 += 1;
		refreshBlock();
	}

	public void moveBlock(int mInputDirection) {
		switch (mInputDirection) {
		case EAST: {
			x1 += 1;
			x2 += 1;
			x3 += 1;
			x4 += 1;
			break;
		}
		case WEST: {
			x1 -= 1;
			x2 -= 1;
			x3 -= 1;
			x4 -= 1;
			break;
		}
		case SOUTH: {
			y1 += 1;
			y2 += 1;
			y3 += 1;
			y4 += 1;
			break;
		}
		}
	}

	public void flip0() {
		switch (blockType) {
		case IBLOCK: {
			x2 = x1 + 1;
			x3 = x1 - 1;
			x4 = x1 - 2;
			y2 = y1;
			y3 = y1;
			y4 = y1;
			break;
		}
		case JBLOCK: {
			x2 = x1 - 1;
			x3 = x1 + 1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1;
			y4 = y1 + 1;
			break;
		}
		case LBLOCK: {
			x2 = x1 + 1;
			x3 = x1 - 1;
			x4 = x1 - 1;
			y2 = y1;
			y3 = y1;
			y4 = y1 + 1;
			break;
		}
		case OBLOCK: {
			x2 = x1 + 1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		case SBLOCK: {
			x2 = x1 - 1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1 - 1;
			y4 = y1 - 1;
			break;
		}
		case TBLOCK: {
			x2 = x1;
			x3 = x1 - 1;
			x4 = x1 + 1;
			y2 = y1 + 1;
			y3 = y1;
			y4 = y1;
			break;
		}
		case ZBLOCK: {
			x2 = x1 + 1;
			x3 = x1;
			x4 = x1 - 1;
			y2 = y1;
			y3 = y1 - 1;
			y4 = y1 - 1;
			break;
		}
		}
	}

	public void flip90() {
		switch (blockType) {
		case IBLOCK: {
			x2 = x1;
			x3 = x1;
			x4 = x1;
			y2 = y1 - 1;
			y3 = y1 + 1;
			y4 = y1 + 2;
			break;
		}
		case JBLOCK: {
			x2 = x1;
			x3 = x1;
			x4 = x1 - 1;
			y2 = y1 - 1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		case LBLOCK: {
			x2 = x1;
			x3 = x1;
			x4 = x1 - 1;
			y2 = y1 + 1;
			y3 = y1 - 1;
			y4 = y1 - 1;
			break;
		}
		case OBLOCK: {
			x2 = x1 + 1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		case SBLOCK: {
			x2 = x1;
			x3 = x1 + 1;
			x4 = x1 + 1;
			y2 = y1 - 1;
			y3 = y1;
			y4 = y1 + 1;
			break;
		}
		case TBLOCK: {
			x2 = x1 - 1;
			x3 = x1;
			x4 = x1;
			y2 = y1;
			y3 = y1 - 1;
			y4 = y1 + 1;
			break;
		}
		case ZBLOCK: {
			x2 = x1;
			x3 = x1 + 1;
			x4 = x1 + 1;
			y2 = y1 + 1;
			y3 = y1;
			y4 = y1 - 1;
			break;
		}
		}
	}

	public void flip180() {
		switch (blockType) {
		case IBLOCK: {
			x2 = x1 - 1;
			x3 = x1 + 1;
			x4 = x1 + 2;
			y2 = y1;
			y3 = y1;
			y4 = y1;
			break;
		}
		case JBLOCK: {
			x2 = x1 + 1;
			x3 = x1 - 1;
			x4 = x1 - 1;
			y2 = y1;
			y3 = y1;
			y4 = y1 - 1;
			break;
		}
		case LBLOCK: {
			x2 = x1 - 1;
			x3 = x1 + 1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1;
			y4 = y1 - 1;
			break;
		}
		case OBLOCK: {
			x2 = x1 + 1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		case SBLOCK: {
			x2 = x1 + 1;
			x3 = x1;
			x4 = x1 - 1;
			y2 = y1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		case TBLOCK: {
			x2 = x1;
			x3 = x1 + 1;
			x4 = x1 - 1;
			y2 = y1 - 1;
			y3 = y1;
			y4 = y1;
			break;
		}
		case ZBLOCK: {
			x2 = x1 - 1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		}
	}

	public void flip270() {
		switch (blockType) {
		case IBLOCK: {
			x2 = x1;
			x3 = x1;
			x4 = x1;
			y2 = y1 + 1;
			y3 = y1 - 1;
			y4 = y1 - 2;
			break;
		}
		case JBLOCK: {
			x2 = x1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1 + 1;
			y3 = y1 - 1;
			y4 = y1 - 1;
			break;
		}
		case LBLOCK: {
			x2 = x1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1 - 1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		case OBLOCK: {
			x2 = x1 + 1;
			x3 = x1;
			x4 = x1 + 1;
			y2 = y1;
			y3 = y1 + 1;
			y4 = y1 + 1;
			break;
		}
		case SBLOCK: {
			x2 = x1;
			x3 = x1 - 1;
			x4 = x1 - 1;
			y2 = y1 + 1;
			y3 = y1;
			y4 = y1 - 1;
			break;
		}
		case TBLOCK: {
			x2 = x1 + 1;
			x3 = x1;
			x4 = x1;
			y2 = y1;
			y3 = y1 + 1;
			y4 = y1 - 1;
			break;
		}
		case ZBLOCK: {
			x2 = x1;
			x3 = x1 - 1;
			x4 = x1 - 1;
			y2 = y1 - 1;
			y3 = y1;
			y4 = y1 + 1;
			break;
		}
		}
	}

	public void refreshBlock() {
		switch (mOrientation) {
		case FACEUP: {
			flip0();
			break;
		}
		case FACERIGHT: {
			flip90();
			break;
		}
		case FACEDOWN: {
			flip180();
			break;
		}
		case FACELEFT: {
			flip270();
			break;
		}
		}
	}

	public void rotateClockwise() {
		mOrientation = (mOrientation + 1) % 4;

		if (blockType == IBLOCK) {
			if (mOrientation == FACEDOWN)
				x1 -= 1;
			if (mOrientation == FACEUP)
				x1 += 1;
		}

		refreshBlock();
	}
}