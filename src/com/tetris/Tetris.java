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

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

public class Tetris extends Activity {

	private TetrisView mTetrisView;
	private TetrisView2 mTetrisView2;

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
		mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);
		mTetrisView.setTextView((TextView) findViewById(R.id.text));
		// mTetrisView2.setTextView((TextView) findViewById(R.id.text2));

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
		// Store the game state
		outState.putBundle(ICICLE_KEY, mTetrisView.saveState());
		outState.putBundle(ICICLE_KEY, mTetrisView2.saveState());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if (mTetrisView == null)
			return false;

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			mTetrisView.pressKey(1);
			mTetrisView2.pressKey(1);
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			mTetrisView.pressKey(2);
			mTetrisView2.pressKey(2);
			return (true);
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			mTetrisView.pressKey(3);
			mTetrisView2.pressKey(3);
			return (true);
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			mTetrisView.pressKey(4);
			mTetrisView2.pressKey(4);
			return (true);
		}
		if (keyCode == KeyEvent.KEYCODE_SPACE
				|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			mTetrisView.pressKey(5);
			mTetrisView2.pressKey(5);
			return (true);
		}

		return super.onKeyDown(keyCode, msg);
	}
}
