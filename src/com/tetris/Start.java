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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Start extends Activity implements OnGestureListener {

//	private static final String TAG = "TetrisView";

	/**
	 * mode macros
	 */
	public static final int PAUSE = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int LOSE = 3;
	public static final int WIN = 4;

	TetrisView mTetrisView;
	TetrisView2 mTetrisView2;

	/**
	 * runs every mDelay seconds and checks if a new block has been created and
	 * whether or not the game is over
	 */
	private RefreshHandler mRefreshHandler = new RefreshHandler();

	String ICICLE_KEY = "Tetris-view";
	/**
	 * mDelay is the delay between each run of the RefreshHandler
	 */
	private static final long mDelay = 50;
	private long mLastMove;

	// server
	// DEFAULT IP
	public static String SERVERIP = "";
	// DESIGNATE A PORT
	public static final int SERVERPORT = 8080;
	private TextView serverStatus;
	private ServerSocket serverSocket;
	private Button playTetris;
	private PrintWriter serverOut;
	private PrintWriter clientOut;
	protected static final int MSG_ID = 0x1337;

	private Handler handler = new Handler();

	// client
	private TextView clientStatus;
	private EditText serverIp;
	private Button connectPhones;
	private Button clientReadyButton;
	private String serverIpAddress = "";
	private boolean connected = false;

	private Button serverButton;
	private Button clientButton;
	private Button soloButton;
	private boolean soloMode = false;
	private boolean serverReady = false;
	private boolean clientReady = false;
	private boolean serverSide = false;
	private boolean clientSide = false;
	private String fromClient = "";
	private String fromServer = "";

	private final int sensitivity = 20;
	
	private GestureDetector gestureScanner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gestureScanner = new GestureDetector(this);

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start);

		serverButton = (Button) findViewById(R.id.server);
		serverButton.setOnClickListener(serverClick);
		clientButton = (Button) findViewById(R.id.client);
		clientButton.setOnClickListener(clientClick);
		soloButton = (Button) findViewById(R.id.solo);
		soloButton.setOnClickListener(soloClick);

	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (mTetrisView == null)
			return false;
		if (!soloMode)
		{
			if (!serverSide && !clientSide)
				return false;
		}
		
//		if (distanceY < (-5*sensitivity)) {
//			// if (serverSide)
//			// serverOut.println("2");
//			// else
//			// clientOut.println("2");
//
//			mTetrisView.pressKey(5);
//			return (false);
//		}
//
		if (distanceY < (-1*sensitivity)) {
			 if (serverSide)
				 serverOut.println("2");
			 if (clientSide)
				 clientOut.println("2");

			mTetrisView.pressKey(2);
			return (true);
		}

		if (distanceX > sensitivity) {
			 if (serverSide)
				 serverOut.println("3");
			 if (clientSide)
				 clientOut.println("3");

			mTetrisView.pressKey(3);
			return (true);
		}

		if (distanceX < (-1*sensitivity)) {
			 if (serverSide)
				 serverOut.println("4");
			 if (clientSide)
				 clientOut.println("4");

			mTetrisView.pressKey(4);
			return (true);
		}

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		 if (serverSide)
			 serverOut.println("5");
		 if (clientSide)
			 clientOut.println("5");

		if (mTetrisView != null)
			mTetrisView.pressKey(5);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		return;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		if (mTetrisView != null)
		{
//			if ((mTetrisView.getMode() == LOSE) || (mTetrisView.getMode() == WIN))
//				System.exit(0);
//			if (mTetrisView.getMode() == READY) {
//
//				if (serverSide)
//					serverOut.println("1");
//				else
//					clientOut.println("1");
//				
//				mTetrisView.pressKey(1);
//				return (true);
//			}
			if ((mTetrisView.getMode() == LOSE) || (mTetrisView.getMode() == WIN))
				System.exit(0);
			if (mTetrisView.getMode() == READY) {
				if (serverSide)
					serverOut.println("1");
				if (clientSide)
					clientOut.println("1");

				mTetrisView.pressKey(1);
				if (!soloMode)
					mTetrisView2.pressKey(1);
				return (true);
			}

			if (serverSide)
				serverOut.println("1");
			if (clientSide)
				clientOut.println("1");
			
			mTetrisView.pressKey(1);
		}
		return (true);
	}

	private OnClickListener soloClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
				soloMode = true;
				
				setContentView(R.layout.solotetris);

				mTetrisView = (TetrisView) findViewById(R.id.sologame);
				mTetrisView.setTextView((TextView) findViewById(R.id.solotext));

				mTetrisView.setMode(READY);
		}
	};

	private OnClickListener serverClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			serverSide = true;

			setContentView(R.layout.server);

			serverStatus = (TextView) findViewById(R.id.server_status);
			SERVERIP = getLocalIpAddress();
			serverStatus.setText("Waiting for connection at: " + SERVERIP);
			// serverReady button
			playTetris = (Button) findViewById(R.id.play_tetris);
			playTetris.setOnClickListener(serverReadyClick);

			Thread serverThread = new Thread(new ServerThread());
			serverThread.start();
		}
	};

	private OnClickListener clientClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			clientSide = true;

			setContentView(R.layout.client);

			clientStatus = (TextView) findViewById(R.id.client_status);
			serverIp = (EditText) findViewById(R.id.server_ip);
			connectPhones = (Button) findViewById(R.id.connect_phones);
			connectPhones.setOnClickListener(connectClick);
			// clientReady button
			clientReadyButton = (Button) findViewById(R.id.play_tetris);
			clientReadyButton.setOnClickListener(clientReadyClick);

			clientStatus.setText("Enter an IP Address to connect");
		}
	};

	// server
	private OnClickListener serverReadyClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			serverReady = true;
			if (serverOut != null)
				serverOut.println("playtetris");

			if (clientReady) {
				if (mTetrisView == null) {
					setContentView(R.layout.tetris_layout);

					mTetrisView = (TetrisView) findViewById(R.id.tetris);
					mTetrisView.setTextView((TextView) findViewById(R.id.text));
					mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);

					mTetrisView.setMode(READY);
					mTetrisView2.setMode(READY);
				}
			}
		}
	};

	// client
	private OnClickListener clientReadyClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			clientReady = true;
			if (clientOut != null)
				clientOut.println("playtetris");

			if (serverReady) {
				if (mTetrisView == null) {
					setContentView(R.layout.tetris_layout);

					mTetrisView = (TetrisView) findViewById(R.id.tetris);
					mTetrisView.setTextView((TextView) findViewById(R.id.text));
					mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);

					mTetrisView.setMode(READY);
					mTetrisView2.setMode(READY);
				}
			}
		}
	};

	private OnClickListener connectClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!connected) {
				clientStatus.setText("Attempting to connect...");

				serverIpAddress = serverIp.getText().toString();
				if (!serverIpAddress.equals("")) {
					Thread cThread = new Thread(new ClientThread());
					cThread.start();
				}
			} else
				clientStatus.setText("???");

		}
	};
	
	// GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			// Log.e("Start", ex.toString());
		}
		return null;
	}

	class ServerThread implements Runnable {
		public void run() {
			try {
				if (SERVERIP != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							serverStatus
									.setText("Listening on IP: " + SERVERIP);
						}
					});
					serverSocket = new ServerSocket(SERVERPORT);

					while (true) {
						// LISTEN FOR INCOMING CLIENTS
						Socket client = serverSocket.accept();
						handler.post(new Runnable() {
							@Override
							public void run() {
								serverStatus.setText("Connected.");
							}
						});

						try {
							BufferedReader serverIn = new BufferedReader(
									new InputStreamReader(
											client.getInputStream()));
							// set true, for auto-flushing after print
							// statements
							serverOut = new PrintWriter(new BufferedWriter(
									new OutputStreamWriter(
											client.getOutputStream())), true);

							while ((fromClient = serverIn.readLine()) != null) {
								if (fromClient.equals("playtetris")) {
									clientReady = true;
									fromClient = "0";
								} else if (fromClient.equals("gameover")) {
									fromClient = "0";
									handler.post(new Runnable() {
										@Override
										public void run() {
											mTetrisView.setMode(WIN);
											mTetrisView2.setMode(LOSE);
										}
									});
								} else if (fromClient.equals(""))
									fromClient = "0";

								if (mTetrisView2 != null
										&& !fromClient.equals("0")) {
									final String sendValue = fromClient;
									handler.post(new Runnable() {
										@Override
										public void run() {
											if ((mTetrisView.getMode() == READY)
													&& sendValue.equals("1"))
												if (mTetrisView.getMode() == READY)
													mTetrisView.pressKey(1);

											mTetrisView2.pressKey(Integer
													.parseInt(sendValue));
										}
									});
								}

								if (mTetrisView == null) {
									if (serverReady && clientReady) {
										handler.post(new Runnable() {
											@Override
											public void run() {
//												try {
//													serverSocket.close();
//												} catch (Exception e) {
//													serverStatus
//															.setText("Close failed.");
//												}

												setContentView(R.layout.tetris_layout);

												mTetrisView = (TetrisView) findViewById(R.id.tetris);
												mTetrisView
														.setTextView((TextView) findViewById(R.id.text));
												mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);

												mTetrisView.setMode(READY);
												mTetrisView2.setMode(READY);
											}
										});
									}
								}

								update();
								mRefreshHandler.sleep(mDelay);
							}
							break;
						} catch (Exception e) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									serverStatus
											.setText("Oops. Connection interrupted.");
								}
							});
							e.printStackTrace();
						}
					}
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							serverStatus
									.setText("Couldn't detect a connection.");
						}
					});
				}
			} catch (Exception e) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						serverStatus.setText("ERROR");
					}
				});
				e.printStackTrace();
			}
		}
	}

	public class ClientThread implements Runnable {
		public void run() {
			try {
				if (!connected) {
					InetAddress serverAddr = InetAddress
							.getByName(serverIpAddress);
					// connecting
					Socket server = new Socket(serverAddr, SERVERPORT);
					if (server != null)
						connected = true;

					while (connected) {
						if (!serverReady || !clientReady) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									clientStatus.setText("Connected.");
								}
							});
						}

						try {
							BufferedReader clientIn = new BufferedReader(
									new InputStreamReader(
											server.getInputStream()));
							// set true, for auto-flushing after print
							// statements
							clientOut = new PrintWriter(new BufferedWriter(
									new OutputStreamWriter(
											server.getOutputStream())), true);

							while ((fromServer = clientIn.readLine()) != null) {
								if (fromServer.equals("playtetris")) {
									serverReady = true;
									fromServer = "0";
								} else if (fromServer.equals("gameover")) {
									fromServer = "0";
									handler.post(new Runnable() {
										@Override
										public void run() {
											mTetrisView.setMode(WIN);
											mTetrisView2.setMode(LOSE);
										}
									});
								} else if (fromServer.equals(""))
									fromServer = "0";

								if (mTetrisView2 != null
										&& !fromServer.equals("0")) {
									final String sendValue = fromServer;
									handler.post(new Runnable() {
										@Override
										public void run() {
											if (mTetrisView.getMode() == READY)
												mTetrisView.pressKey(1);

											mTetrisView2.pressKey(Integer
													.parseInt(sendValue));
										}
									});
								}

								if (mTetrisView == null) {
									if (serverReady && clientReady) {
										handler.post(new Runnable() {
											@Override
											public void run() {
												setContentView(R.layout.tetris_layout);

												mTetrisView = (TetrisView) findViewById(R.id.tetris);
												mTetrisView
														.setTextView((TextView) findViewById(R.id.text));
												mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);

												mTetrisView.setMode(READY);
												mTetrisView2.setMode(READY);
											}
										});
									}
								}

								update();
								mRefreshHandler.sleep(mDelay);
							}
							break;
						} catch (Exception e) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									clientStatus
											.setText("Oops. Connection interrupted");
								}
							});
							e.printStackTrace();
						}
					}
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							clientStatus.setText("Couldn't detect");
						}
					});
				}
			} catch (Exception e) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						clientStatus.setText("ERROR");
					}
				});
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if (soloMode)
		{
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				mTetrisView.pressKey(1);
				return (true);
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				mTetrisView.pressKey(2);
				return (true);
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				mTetrisView.pressKey(3);
				return (true);
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				mTetrisView.pressKey(4);
				return (true);
			}

			if (keyCode == KeyEvent.KEYCODE_SPACE
					|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				mTetrisView.pressKey(5);
				return (true);
			}
		}
		
		if (!serverSide && !clientSide)
			return false;
		
		if ((mTetrisView.getMode() == LOSE) || (mTetrisView.getMode() == WIN))
			System.exit(0);
		if (mTetrisView.getMode() == READY) {
			if (serverSide)
				serverOut.println("1");
			else
				clientOut.println("1");

			mTetrisView.pressKey(1);
			mTetrisView2.pressKey(1);
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (serverSide)
				serverOut.println("1");
			else
				clientOut.println("1");

			mTetrisView.pressKey(1);
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if (serverSide)
				serverOut.println("2");
			else
				clientOut.println("2");

			mTetrisView.pressKey(2);
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (serverSide)
				serverOut.println("3");
			else
				clientOut.println("3");

			mTetrisView.pressKey(3);
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (serverSide)
				serverOut.println("4");
			else
				clientOut.println("4");

			mTetrisView.pressKey(4);
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_SPACE
				|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			if (serverSide)
				serverOut.println("5");
			else
				clientOut.println("5");

			mTetrisView.pressKey(5);
			return (true);
		}

		return super.onKeyDown(keyCode, msg);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(!soloMode)
			System.exit(0);

//		if (serverSide && (serverOut != null))
//			serverOut.println("gameover");
//		else if (clientSide && (clientOut != null))
//			clientOut.println("gameover");
		
		// Pause the game along with the activity
		if (mTetrisView != null)
			mTetrisView.setMode(TetrisView.PAUSE);
//		if (mTetrisView2 != null)
//			mTetrisView2.setMode(TetrisView.PAUSE);
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (serverSide)
			serverOut.println("gameover");
		if (clientSide)
			clientOut.println("gameover");
			
		if (serverSide) {
			try {
				// CLOSE THE SOCKET UPON EXITING
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.exit(0);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Store the game state
		if (mTetrisView != null)
			outState.putBundle(ICICLE_KEY, mTetrisView.saveState());
		if (mTetrisView2 != null)
			outState.putBundle(ICICLE_KEY, mTetrisView2.saveState());
	}

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			update();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public void update() {
		if (mTetrisView != null) {
			if (mTetrisView.getMode() == RUNNING) {
				long now = System.currentTimeMillis();

				if (now - mLastMove > mDelay) {
					if (serverSide)
						serverOut.println((10 + mTetrisView.getBlockType()));
					else
						clientOut.println((10 + mTetrisView.getBlockType()));

					mLastMove = now;
				}
				mRefreshHandler.sleep(mDelay);
			} else if (mTetrisView.getMode() == LOSE) {
				if (serverSide)
					serverOut.println("gameover");
				else
					clientOut.println("gameover");
				mTetrisView2.setMode(WIN);
			}

		}
	}
<<<<<<< HEAD
}
=======
}
>>>>>>> 4b16c8423fb0c89e7a18f916f0d60db3ba978cb0
