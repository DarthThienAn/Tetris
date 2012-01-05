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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Start extends Activity {
	
    private static final String TAG = "TetrisView";
	//TetrisView
	/**
	 * mode macros
	 */
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;
    
    TetrisView mTetrisView;
    TetrisView2 mTetrisView2;
    
    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
//    private static final int BLUEUNIT = 1;
//    private static final int BROWNUNIT = 2;
//    private static final int CYANUNIT = 3;
//    private static final int GREENUNIT = 4;
//    private static final int ORANGEUNIT = 5;
//    private static final int PURPLEUNIT = 6;
//    private static final int REDUNIT = 7;
//    private static final int WALL = 8;

    /**
     * mMoveDelay: number of milliseconds between Tetris movements. 
     * This will decrease over time.
     */
//    private static final long mMoveDelay = 50;
    
    /**
     * mLastMove: tracks the absolute time when the Tetris last moved, and is used
     * to determine if a move should be made based on mMoveDelay.
     */
//    private long mLastMove;
    
    /**
     * mStatusText: text shows to the user in some run states
     */
//    private TextView mStatusText;
	
    /**
     * mTetrisGame: a game state containing all the relevant information about the game.
     */
//    private TetrisGame mTetrisGame;

    
 	String ICICLE_KEY = "Tetris-view";
 	private TextView startText;
 	private Button serverButton;
 	private Button clientButton;
 	
 	//server
    // DEFAULT IP
    public static String SERVERIP = "";
    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;
    private TextView serverStatus;
//    private Handler handler = new Handler();
    private ServerSocket serverSocket;
//    private Bundle savedInstanceState;
    private Button playTetris;
    private PrintWriter serverOut;
    private PrintWriter clientOut;
    protected static final int MSG_ID = 0x1337;
//    String line = null;

    private Handler handler = new Handler();
    
    //client
    private TextView clientStatus;
    private EditText serverIp;
    private Button connectPhones;
    private Button clientReadyButton;
    private String serverIpAddress = "";
    private boolean connected = false;
//    String fromServer = "";
    
    //connection
    private boolean serverReady = false;
    private boolean clientReady = false;
    private boolean serverSide = false;
    private boolean clientSide = false;
    private String fromClient = "";
    private String fromServer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.start);
        
        startText = (TextView) findViewById(R.id.start_text);
//        this.savedInstanceState = savedInstanceState;
        serverButton = (Button) findViewById(R.id.server);
        serverButton.setOnClickListener(serverClick);
        clientButton = (Button) findViewById(R.id.client);
        clientButton.setOnClickListener(clientClick);

    }
    
    private OnClickListener serverClick = new OnClickListener() {
    	@Override
    	public void onClick(View v) 
    	{
    		serverSide = true;
    		
    		setContentView(R.layout.server);
    		
    		serverStatus = (TextView) findViewById(R.id.server_status);
    		SERVERIP = getLocalIpAddress();
    		serverStatus.setText("Waiting for connection at: " + SERVERIP);
    		//serverReady button
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
    		//clientReady button
            clientReadyButton = (Button) findViewById(R.id.play_tetris);
            clientReadyButton.setOnClickListener(clientReadyClick);
            
            clientStatus.setText("Waiting to connect");
    	}
    };

      
      //server
      private OnClickListener serverReadyClick = new OnClickListener() {

    	  @Override
          public void onClick(View v) {
    		  serverReady = true;
    		  if (serverOut != null)
    		  {
    			  serverOut.println("playtetris");
    			  serverOut.flush();
    		  }
    		  
    		  if (clientReady)
    		  {
     			  if(mTetrisView == null)
     			  {
     				  Log.d(TAG, "serverReadyClick");
     				  setContentView(R.layout.tetris_layout);
          			  
					   mTetrisView = (TetrisView) findViewById(R.id.tetris);
					   mTetrisView.setTextView((TextView) findViewById(R.id.text));
					   mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);
					   mTetrisView2.setTextView((TextView) findViewById(R.id.text2));
                    
					   mTetrisView.setMode(READY);
					   mTetrisView2.setMode(READY);
					   
	        		   mTetrisView.pressKey(1);
	        		   mTetrisView2.pressKey(1);

		        	   if (serverSide)
		        	   {
//		        		   serverOut.println("1");
//		        		   serverOut.flush();
		        		   serverOut.println((10 + mTetrisView.getBlockType()));
		        		   serverOut.flush();
//		        		   mTetrisView.pressKey(1);
//		        		   mTetrisView2.pressKey(1);
		        	   }
		        	   if (clientSide)
		        	   {
//		        		   clientOut.println("1");
//		        		   clientOut.flush();
		        		   clientOut.println((10 + mTetrisView.getBlockType()));
		        		   clientOut.flush();
//		        		   mTetrisView.pressKey(1);
//		        		   mTetrisView2.pressKey(1);
		        	   }

     			  }
    		  }
          }
      };

      //server
      private OnClickListener clientReadyClick = new OnClickListener() {

    	  @Override
          public void onClick(View v) {
    		  clientReady = true;
    		  if (clientOut != null)
    		  {
    			  clientOut.println("playtetris");
    			  clientOut.flush();
    		  }    		  
    		  
    		  if (serverReady)
    		  {
     			  if(mTetrisView == null)
     			  {
     				  Log.d(TAG, "clientReadyClick");
					   setContentView(R.layout.tetris_layout);
          			  
					   mTetrisView = (TetrisView) findViewById(R.id.tetris);
					   mTetrisView.setTextView((TextView) findViewById(R.id.text));
					   mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);
					   mTetrisView2.setTextView((TextView) findViewById(R.id.text2));
                    
					   mTetrisView.setMode(READY);
					   mTetrisView2.setMode(READY);
					   
	        		   mTetrisView.pressKey(1);
	        		   mTetrisView2.pressKey(1);

		        	   if (serverSide)
		        	   {
//		        		   serverOut.println("1");
//		        		   serverOut.flush();
		        		   serverOut.println((10 + mTetrisView.getBlockType()));
		        		   serverOut.flush();
//		        		   mTetrisView.pressKey(1);
//		        		   mTetrisView2.pressKey(1);
		        	   }
		        	   if (clientSide)
		        	   {
//		        		   clientOut.println("1");
//		        		   clientOut.flush();
		        		   clientOut.println((10 + mTetrisView.getBlockType()));
		        		   clientOut.flush();
//		        		   mTetrisView.pressKey(1);
//		        		   mTetrisView2.pressKey(1);
		        	   }

     			  }
    		  }
          }
      };
      
      private OnClickListener connectClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
        }
    };
      
      // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
      private String getLocalIpAddress() {
          try {
              for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                  NetworkInterface intf = en.nextElement();
                  for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                      InetAddress inetAddress = enumIpAddr.nextElement();
                      if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
                  }
              }
          } catch (SocketException ex) {
              Log.e("Start", ex.toString());
          }
          return null;
      }

       class ServerThread implements Runnable {
           public void run() {
        	   try {
        		   if (SERVERIP != null)
        		   {
        			   handler.post(new Runnable()
        			   {
        				   @Override
        				   public void run()
        				   {
        					   serverStatus.setText("Listening on IP: " + SERVERIP);
        				   }
        			   });
        			   serverSocket = new ServerSocket(SERVERPORT);
        			   
        			   while (true)
        			   {
        				   // LISTEN FOR INCOMING CLIENTS
        				   Socket client = serverSocket.accept();
        				   handler.post(new Runnable()
        				   {
        					   @Override
        					   public void run()
        					   {
        						   serverStatus.setText("Connected.");
        					   }
        				   });

        				   try {
        					   BufferedReader serverIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        					   serverOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        					   
        					   while ((fromClient = serverIn.readLine()) != null)
        					   {
            					   Log.d(TAG, "Server: " + fromClient);
        						   if (fromClient.equals("playtetris"))
        						   {
        							   clientReady = true;
        							   fromClient = "0";
        						   }

        						   if (fromClient.equals(""))
        							   fromClient = "0";
        						   
            					   if (mTetrisView2 != null && !fromClient.equals("0"))
            					   {
            						   handler.post(new Runnable()
            						   {
            							   @Override
            							   public void run()
            							   {
            								   mTetrisView2.pressKey(Integer.parseInt(fromClient));
            							   }
            						   });
            					   }
            					   
								   if(mTetrisView == null)
								   {
//									   if (mTetrisView.getIsNewBlock() && !fromClient.equals("0"))
//									   {
//            						   handler.post(new Runnable()
//            						   {
//            							   @Override
//            							   public void run()
//            							   {
//                        		        	   if (serverSide)
//                        		        	   {
//                        		        		   serverOut.println((10 + mTetrisView.getBlockType()));
//                        		        		   serverOut.flush();
//                        		        	   }
//                        		        	   if (clientSide)
//                        		        	   {
//                        		        		   clientOut.println((10 + mTetrisView.getBlockType()));
//                        		        		   clientOut.flush();
//                        		        	   }
//            							   }
//            						   });
//									   }
            					   
									   if(serverReady && clientReady)
									   {
										   handler.post(new Runnable()
										   {
		        							   @Override
		        							   public void run()
		        							   {
		        								   try {
		        									   serverSocket.close();
		        								   } catch (Exception e)
		        								   {
	        										   serverStatus.setText("Close failed.");
	        										   Log.d(TAG, "Closed Failed");
		        								   }
		        								   
		        								   Log.d(TAG, "ServerThread");
		        								   setContentView(R.layout.tetris_layout);
	 					            			  	
		        								   mTetrisView = (TetrisView) findViewById(R.id.tetris);
		        								   mTetrisView.setTextView((TextView) findViewById(R.id.text));
		        								   mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);
		        								   mTetrisView2.setTextView((TextView) findViewById(R.id.text2));
		        								   
		        								   mTetrisView.setMode(READY);
		        								   mTetrisView2.setMode(READY);
		        								   
		        								   mTetrisView.pressKey(1);
		        								   mTetrisView2.pressKey(1);
	        										   
		        								   if (serverSide)
		        								   {
//            							        		   serverOut.println("1");
//            							        		   serverOut.flush();
		        									   serverOut.println((10 + mTetrisView.getBlockType()));
//            							        		   mTetrisView.pressKey(1);
//            							        		   mTetrisView2.pressKey(1);
		        								   }
		        								   if (clientSide)
		        								   {
//            							        		   clientOut.println("1");
//            							        		   clientOut.flush();
		        									   clientOut.println((10 + mTetrisView.getBlockType()));
//            							        		   mTetrisView.pressKey(1);
//            							        		   mTetrisView2.pressKey(1);
		        								   }
		        							   }
										   });
									   }
								   }
        						   
//								   if(mTetrisView == null)
//								   {
//									   if (mTetrisView.getIsNewBlock())// && !fromClient.equals("0"))
//									   {
//            						   handler.post(new Runnable()
//            						   {
//            							   @Override
//            							   public void run()
//            							   {
//                        		        	   if (serverSide)
//                        		        	   {
//                        		        		   serverOut.println((10 + mTetrisView.getBlockType()));
//                        		        		   serverOut.flush();
//                        		        	   }
//                        		        	   if (clientSide)
//                        		        	   {
//                        		        		   clientOut.println((10 + mTetrisView.getBlockType()));
//                        		        		   clientOut.flush();
//                        		        	   }
//            							   }
//            						   });
//									   }
//								   }
									   
//    	        				   handler.post(new Runnable()
//    	        				   {
//    	        					   @Override
//    	        					   public void run()
//    	        					   {
//    	        						   if (!fromClient.equals("0"))
//    	        							   mTetrisView2.pressKey(Integer.parseInt(fromClient));
//    	        					   }
//    	        				   });        						   
        					   }
        					   break;
        				   } catch (Exception e) {
        					   handler.post(new Runnable() {
        						   @Override
        						   public void run()
        						   {
        							   serverStatus.setText("Oops. Connection interrupted.");
        						   }
        					   });
        					   e.printStackTrace();
        				   }
        			   }
        		   } else {
        			   handler.post(new Runnable() {
        				   @Override
        				   public void run () {
        					   serverStatus.setText("Couldn't detect a connection.");
        				   }
        			   });
        		   }
        	   } catch (Exception e) {
        		   handler.post(new Runnable() {
        			   @Override
        			   public void run () {
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
            	   if (!connected)
        		   {
            		   InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
            		   //connecting
            		   Socket server = new Socket(serverAddr, SERVERPORT);
            		   if (server != null)
            			   connected = true;
            		   
        			   while (connected)
        			   {
        				   handler.post(new Runnable()
        				   {
        					   @Override
        					   public void run()
        					   {
        						   clientStatus.setText("Connected.");
        					   }
        				   });

        				   try {
        					   BufferedReader clientIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
        					   clientOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())), true);
        					   
        					   while ((fromServer = clientIn.readLine()) != null)
        					   {
            					   Log.d(TAG, "Client: " + fromServer);
            					   
        						   if (fromServer.equals("playtetris"))
        						   {
        							   serverReady = true;
        							   fromServer = "0";
        						   }

        						   if (fromServer.equals(""))
        							   fromServer = "0";
        						   
            					   Log.d(TAG, "PostClient: " + fromServer);

            					   if (mTetrisView2 != null && !fromServer.equals("0"))
            					   {
            						   handler.post(new Runnable()
            						   {
            							   @Override
            							   public void run()
            							   {
//                        					   Log.d(TAG, "PostPostClient: " + fromServer);
            								   mTetrisView2.pressKey(Integer.parseInt(fromServer));
            							   }
            						   });
            					   }
            					   
								   if(mTetrisView == null)
								   {
//									   if (mTetrisView.getIsNewBlock() && !fromServer.equals("0"))
//									   {
//            						   handler.post(new Runnable()
//            						   {
//            							   @Override
//            							   public void run()
//            							   {
//                        		        	   if (serverSide)
//                        		        	   {
//                        		        		   serverOut.println((10 + mTetrisView.getBlockType()));
//                        		        		   serverOut.flush();
//                        		        	   }
//                        		        	   if (clientSide)
//                        		        	   {
//                        		        		   clientOut.println((10 + mTetrisView.getBlockType()));
//                        		        		   clientOut.flush();
//                        		        	   }
//            							   }
//            						   });
//									   }

									   if(serverReady && clientReady)
									   {
										   handler.post(new Runnable()
										   {
		        							   @Override
		        							   public void run()
		        							   {
	    										   Log.d(TAG, "ClientThread");
	    										   setContentView(R.layout.tetris_layout);
	 					            			  
	    										   mTetrisView = (TetrisView) findViewById(R.id.tetris);
	    										   mTetrisView.setTextView((TextView) findViewById(R.id.text));
	    										   mTetrisView2 = (TetrisView2) findViewById(R.id.tetris2);
	    										   mTetrisView2.setTextView((TextView) findViewById(R.id.text2));
					                            
	    										   mTetrisView.setMode(READY);
	    										   mTetrisView2.setMode(READY);
	
								        		   mTetrisView.pressKey(1);
								        		   mTetrisView2.pressKey(1);
	        										   
	    							        	   if (serverSide)
	    							        	   {
//	        							        		   serverOut.println("1");
	//        							        		   serverOut.flush();
	    							        		   serverOut.println((10 + mTetrisView.getBlockType()));
	    							        		   serverOut.flush();
	//        							        		   mTetrisView.pressKey(1);
	//        							        		   mTetrisView2.pressKey(1);
	    							        	   }
	    							        	   if (clientSide)
	    							        	   {
	//        							        		   clientOut.println("1");
	//        							        		   clientOut.flush();
	    							        		   clientOut.println((10 + mTetrisView.getBlockType()));
	    							        		   clientOut.flush();
	//        							        		   mTetrisView.pressKey(1);
	//        							        		   mTetrisView2.pressKey(1);
	    							        	   }
		        							   }
										   });
									   }
								   }
        						   
//    	        				   handler.post(new Runnable()
//    	        				   {
//    	        					   @Override
//    	        					   public void run()
//    	        					   {
//    	        						   Log.d("keyme", "mark");
//    	        						   if (!fromServer.equals("0"))
//    	        							   mTetrisView2.pressKey(Integer.parseInt(fromServer));
//    	        					   }
//    	        				   });
        						   
        					   }
        					   break;
        				   } catch (Exception e) {
        					   handler.post(new Runnable() {
        						   @Override
        						   public void run()
        						   {
        							   clientStatus.setText("Oops. Cnx interrupt");
        						   }
        					   });
        					   e.printStackTrace();
        				   }
        			   }
        		   } else {
        			   handler.post(new Runnable() {
        				   @Override
        				   public void run () {
        					   clientStatus.setText("Couldn't detect");
        				   }
        			   });
        		   }
        	   } catch (Exception e) {
        		   handler.post(new Runnable() {
        			   @Override
        			   public void run () {
        				   clientStatus.setText("ERROR");
        			   }
        		   });
        		   e.printStackTrace();
        	   }               
           }
       }

       @Override
       public boolean onKeyDown(int keyCode, KeyEvent msg)
       {
    	   
    	   if (!serverSide && !clientSide)
    		   return false;

    	   if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//    		   mTetrisView2.pressKey(1);
        	   if (serverSide)
        	   {
//        		   serverOut.println((10 + mTetrisView.getBlockType()));
//        		   serverOut.flush();
        		   serverOut.println("1");
        		   serverOut.flush();
        	   }
        	   if (clientSide)
        	   {
//        		   clientOut.println((10 + mTetrisView.getBlockType()));
//        		   clientOut.flush();
        		   clientOut.println("1");
        		   clientOut.flush();
        	   }
    		   mTetrisView.pressKey(1);
    		   return (true);
           } 
//    	   Log.d(TAG, "1+");
           
    	   if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//    		   mTetrisView2.pressKey(2);
        	   if (serverSide)
        	   {
//        		   serverOut.println((10 + mTetrisView.getBlockType()));
//        		   serverOut.flush();
        		   serverOut.println("2");
        		   serverOut.flush();
        	   }
        	   if (clientSide)
        	   {
//        		   clientOut.println((10 + mTetrisView.getBlockType()));
//        		   clientOut.flush();
        		   clientOut.println("2");
        		   clientOut.flush();
        	   }
    		   mTetrisView.pressKey(2);
               return (true);
           }
//    	   Log.d(TAG, "2+");
    	   
           if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//        	   mTetrisView2.pressKey(3);
        	   if (serverSide)
        	   {
//        		   serverOut.println((10 + mTetrisView.getBlockType()));
//        		   serverOut.flush();
        		   serverOut.println("3");
        		   serverOut.flush();
        	   }
        	   if (clientSide)
        	   {
//        		   clientOut.println((10 + mTetrisView.getBlockType()));
//        		   clientOut.flush();
        		   clientOut.println("3");
        		   clientOut.flush();
        	   }
        	   mTetrisView.pressKey(3);
               return (true);
           }
//    	   Log.d(TAG, "3+");
    	   
           if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//        	   mTetrisView2.pressKey(4);
        	   if (serverSide)
        	   {
//        		   serverOut.println((10 + mTetrisView.getBlockType()));
//        		   serverOut.flush();
        		   serverOut.println("4");
        		   serverOut.flush();
        	   }
        	   if (clientSide)
        	   {
//        		   clientOut.println((10 + mTetrisView.getBlockType()));
//        		   clientOut.flush();
        		   clientOut.println("4");
        		   clientOut.flush();
        	   }
        	   mTetrisView.pressKey(4);
        	   return (true);
           }
//    	   Log.d(TAG, "4+");

           if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//        	   mTetrisView2.pressKey(5);
        	   if (serverSide)
        	   {
//        		   serverOut.println((10 + mTetrisView.getBlockType()));
//        		   serverOut.flush();
        		   serverOut.println("5");
        		   serverOut.flush();
        	   }
        	   if (clientSide)
        	   {
//        		   clientOut.println((10 + mTetrisView.getBlockType()));
//        		   clientOut.flush();
        		   clientOut.println("5");
        		   clientOut.flush();
        	   }
        	   mTetrisView.pressKey(5);
               return (true);
           }
//    	   Log.d(TAG, "5+");
           
           return super.onKeyDown(keyCode, msg);
       }       

       @Override
       protected void onPause() {
           super.onPause();
           // Pause the game along with the activity
           mTetrisView.setMode(TetrisView.PAUSE);
           mTetrisView2.setMode(TetrisView.PAUSE);
       }

       @Override
       protected void onStop() {
           super.onStop();

           if(serverSide)
           {
        	   try {
                   // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
           		   serverSocket.close();
           	   } catch (IOException e) {
           		   e.printStackTrace();
           	   }
            }
       }
       
       @Override
       public void onSaveInstanceState(Bundle outState) {
           //Store the game state
           outState.putBundle(ICICLE_KEY, mTetrisView.saveState());
           outState.putBundle(ICICLE_KEY, mTetrisView2.saveState());
       }
       
}       
