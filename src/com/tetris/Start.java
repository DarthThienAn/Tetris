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
    TextView newStatus;
    
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
    private static final long mMoveDelay = 50;
    
    /**
     * mLastMove: tracks the absolute time when the Tetris last moved, and is used
     * to determine if a move should be made based on mMoveDelay.
     */
    private long mLastMove;
    
    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView mStatusText;
	
    /**
     * mTetrisGame: a game state containing all the relevant information about the game.
     */
    private TetrisGame mTetrisGame;

    
 	String ICICLE_KEY = "Tetris-view";
 	private TextView startText;
 	private Button serverButton;
 	private Button clientButton;
 	
 	//server
    // DEFAULT IP
    public static String SERVERIP = "";
    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;
    String fromClient = "";
    private TextView serverStatus;
    private EditText mServerMsg;
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
    private EditText mClientMsg;
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
        clientButton = (Button) findViewById(R.id.client);
        serverButton.setOnClickListener(serverClick);
        clientButton.setOnClickListener(clientClick);

    }
    
    private OnClickListener serverClick = new OnClickListener() {
      @Override
      public void onClick(View v) 
      {
//        	startText.setText("server!");
    	  serverSide = true;
    	  
    	  
            setContentView(R.layout.server);
            
            serverStatus = (TextView) findViewById(R.id.server_status);
            mServerMsg = (EditText) findViewById(R.id.server_msg);
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
//          	startText.setText("client!");
        	clientSide = true;
          	
            setContentView(R.layout.client);

            clientStatus = (TextView) findViewById(R.id.client_status);
            serverIp = (EditText) findViewById(R.id.server_ip);
            mClientMsg = (EditText) findViewById(R.id.client_msg);
            connectPhones = (Button) findViewById(R.id.connect_phones);
            connectPhones.setOnClickListener(connectClick);
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
    			  serverOut.println("playtetris");

    		  serverOut.flush();
    		  
    		  if (clientReady)
    		  {
    			  setContentView(R.layout.tetris_layout);
    			  
    			  TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
    			  mTetrisView.setTextView((TextView) findViewById(R.id.text));
    			              
    			  mTetrisView.setMode(READY);
    		  }
//        	  startText.setText("play!");
        	  
//          	out.println("playtetris");
//          	out.flush();
//
//        	
//          	setContentView(R.layout.tetris_layout);
//  
//          	TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
//          	mTetrisView.setTextView((TextView) findViewById(R.id.text));
//              
//          	mTetrisView.setMode(READY);
          }
      };

      //server
      private OnClickListener clientReadyClick = new OnClickListener() {

    	  @Override
          public void onClick(View v) {
    		  clientReady = true;
    		  if (clientOut != null)
    			  clientOut.println("playtetris");
    		  
     		  clientOut.flush();
    		  
    		  if (serverReady)
    		  {
    			  setContentView(R.layout.tetris_layout);
    			  
    			  TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
    			  mTetrisView.setTextView((TextView) findViewById(R.id.text));
    			              
    			  mTetrisView.setMode(READY);
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
              Log.e("ServerActivity", ex.toString());
          }
          return null;
      }

      @Override
      protected void onStop() {
          super.onStop();
          try {
               // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
               serverSocket.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
      }
      
      Handler serverHandler = new Handler() {
          public void handleMessage(Message msg) {
             switch (msg.what) {
             case MSG_ID:
                TextView tv = (TextView) findViewById(R.id.server_status);
                tv.setText(fromClient);
                break;
             default:
                break;
             }
             super.handleMessage(msg);
          }
       };
       
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
//        						   try {
//        						   serverSocket.close();
//        						   } catch (Exception e)
//        						   {
//            						   serverStatus.setText("Close failed?.");
//        						   }
        						   
//	        						   setContentView(R.layout.tetris_layout);
//				            			  
//	        						   TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
//	        						   mTetrisView.setTextView((TextView) findViewById(R.id.text));
//			                            
//	        						   mTetrisView.setMode(READY);
//	        						   
//	        						   serverStatus = (TextView) findViewById(R.id.text2);
        					   }
        				   });
//48      
        				   try {
        					   String fromClient = null;
        					   BufferedReader serverIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        					   serverOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
     						   newStatus = (TextView) findViewById(R.id.text2);
        					   Log.d(TAG, "1 " + fromClient);
        					   while ((fromClient = serverIn.readLine()) != null)
        					   {
         						   newStatus = (TextView) findViewById(R.id.text2);
            					   Log.d(TAG, "2 " + fromClient);
        						   if (fromClient.equals("playtetris"))
        							   clientReady = true;
//        						   else
//        							   newStatus.setText(fromClient);

        						   if (fromClient.equals("1"))
        						   {
        							   newStatus.setText("1");
        						   }
        						   if (fromClient.equals("2"))
        						   {
        							   newStatus.setText("2");
        						   }
        						   if (fromClient.equals("3"))
        						   {
        							   newStatus.setText("3");
        						   }
        						   if (fromClient.equals("4"))
        						   {
        							   newStatus.setText("4");
        						   }
        						   if (fromClient.equals("5"))
        						   {
        							   newStatus.setText("5");
        						   }
        						   
//        						   Log.d("ServerActivity", line);
        						   handler.post(new Runnable()
        						   {
        							   @Override
        							   public void run()
        							   {
//                						   serverStatus.setText("Hit play to play");
                						   
                						   //listen for message and update
//                						   Message m = new Message();
//                						   m.what = MSG_ID;
//										   fromClient = line;
//										   serverHandler.sendMessage(m);

                						   Log.d(TAG, "client: " + clientReady + "server: " + serverReady);
										      
 					            		  //send a message
// 					            		  String sendMsg = mServerMsg.getText().toString();
 					            		  //output the message
// 					            		  if (serverReady)
// 					            			  out.println("playtetris");
// 					            		  else
// 					            			  out.println(sendMsg + "\n");
 					            		  
// 					            		  out.flush();
 					            		  
 					            		  if (serverReady && clientReady)
 					            		  {
 					            			  try{
 					            				  serverSocket.close();
	 			        						   } catch (Exception e)
	 			        						   {
	 			            						   serverStatus.setText("Close failed?.");
	 			        						   }
 					            			  if(mTetrisView == null)
 					            			  {
 					            				  setContentView(R.layout.tetris_layout);
 					            			  
 					            				  mTetrisView = (TetrisView) findViewById(R.id.tetris);
 					            				  mTetrisView.setTextView((TextView) findViewById(R.id.text));
 					                            
 					            				  mTetrisView.setMode(READY);
 					            				  newStatus = (TextView) findViewById(R.id.text2);
 					            			  }
 					            		  }

        							   }
        						   });
        					   }
        					   break;
        				   } catch (Exception e) {
        					   handler.post(new Runnable() {
        						   @Override
        						   public void run()
        						   {
        							   serverStatus.setText("Oops. Cnx interrupt");
        						   }
        					   });
        					   e.printStackTrace();
        				   }
        			   }
        		   } else {
        			   handler.post(new Runnable() {
        				   @Override
        				   public void run () {
        					   serverStatus.setText("Couldn't detect");
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
            		   Socket s = new Socket(serverAddr, SERVERPORT);
            		   if (s != null)
            			   connected = true;
            		   
//        			   handler.post(new Runnable()
//        			   {
//        				   @Override
//        				   public void run()
//        				   {
//        					   clientStatus.setText("First step");
//        				   }
//        			   });
        			   while (connected)
        			   {
        				   handler.post(new Runnable()
        				   {
        					   @Override
        					   public void run()
        					   {
        						   clientStatus.setText("Connected.");
//	        						   setContentView(R.layout.tetris_layout);
//				            			  
//	        						   TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
//	        						   mTetrisView.setTextView((TextView) findViewById(R.id.text));
//			                            
//	        						   mTetrisView.setMode(READY);
//	        						   clientStatus = (TextView) findViewById(R.id.text2);
        					   }
        				   });
//48      
        				   try {
        					   String fromServer = null;
        					   BufferedReader clientIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
        					   clientOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
     						   newStatus = (TextView) findViewById(R.id.text2);
        					   Log.d(TAG, "1 " + fromServer);
        					   while ((fromServer = clientIn.readLine()) != null)
        					   {
         						   newStatus = (TextView) findViewById(R.id.text2);
            					   Log.d(TAG, "2: " + fromServer);
        						   if (fromServer.equals("playtetris"))
        							   serverReady = true;
//        						   else
//        							   newStatus.setText(fromServer);

        						   if (fromServer.equals("1"))
        						   {
        							   newStatus.setText("1");
        						   }
        						   if (fromServer.equals("2"))
        						   {
        							   newStatus.setText("2");
        						   }
        						   if (fromServer.equals("3"))
        						   {
        							   newStatus.setText("3");
        						   }
        						   if (fromServer.equals("4"))
        						   {
        							   newStatus.setText("4");
        						   }
        						   if (fromServer.equals("5"))
        						   {
        							   newStatus.setText("5");
        						   }
        						   
//        						   Log.d(TAG, "client: " + clientReady + "server: " + serverReady);
        						   
//        						   Log.d("ServerActivity", line);
        						   handler.post(new Runnable()
        						   {
        							   @Override
        							   public void run()
        							   {
//                						   clientStatus.setText("Waiting for Server to hit play");
                						   
                						   //listen for message and update
//                						   Message m = new Message();
//                						   m.what = MSG_ID;
//										   fromClient = line;
//										   serverHandler.sendMessage(m);

                						   Log.d(TAG, "client: " + clientReady + "server: " + serverReady);
                						   
										      
 					            		  //send a message
// 					            		  String sendMsg = mClientMsg.getText().toString();
 					            		  //output the message
// 					            		  if (clientReady)
// 	 					            		  clientOut.println("playtetris");
// 					            		  else
// 					            			  clientOut.println(sendMsg + "\n");
// 					            		  clientOut.flush();
 				
 					            		  
 					            		  if(serverReady && clientReady)
 					            		  {
 					            			  if(mTetrisView == null)
 					            			  {
	 					            			  setContentView(R.layout.tetris_layout);
	 					            			  
	 					            			  mTetrisView = (TetrisView) findViewById(R.id.tetris);
	 					            			  mTetrisView.setTextView((TextView) findViewById(R.id.text));
	 				                            
	 					            			  mTetrisView.setMode(READY);
	 					            			  newStatus = (TextView) findViewById(R.id.text2);
 					            			  }
 					            		  }
        							   }
        						   });
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
       
//       Handler clientHandler = new Handler() {
//           public void handleMessage(Message msg) {
//              switch (msg.what) {
//              case MSG_ID:
//                 TextView tv = (TextView) findViewById(R.id.client_text);
//                 tv.setText(fromServer);
//                 break;
//              default:
//                 break;
//              }
//              super.handleMessage(msg);
//           }
//        };

       @Override
       public boolean onKeyDown(int keyCode, KeyEvent msg)
       {
    	   newStatus = (TextView) findViewById(R.id.text2);
    	   
    	   if (!serverSide && !clientSide)
    	   {
    		   return false;
    	   }
    	   
    	   if (mTetrisView == null)
    	   {
    		   mTetrisView = (TetrisView) findViewById(R.id.tetris);
    		   newStatus.setText("it was null!");
    		   return false;
    	   }

    	   if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//        	   newStatus.setText("1: serverSide: " + serverSide + " clientSide: " + clientSide);
    		   mTetrisView.pressKey(1);
//    		   mTetrisView2.pressKey(1);
        	   if (serverSide)
        	   {
        		   serverOut.println("1");
//            	   newStatus.setText("server pressed 1");
        	   }
        	   if (clientSide)
        	   {
        		   clientOut.println("1");
//            	   newStatus.setText("client pressed 1");
        	   }
    		   return (true);
           } 
           
    	   if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//        	   newStatus.setText("i pressed 2");
    		   mTetrisView.pressKey(2);
//    		   mTetrisView2.pressKey(2);
        	   if (serverSide)
        		   serverOut.println("2");
        	   if (clientSide)
        		   clientOut.println("2");
               return (true);
           }
           if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//        	   newStatus.setText("i pressed 3");
        	   mTetrisView.pressKey(3);
//        	   mTetrisView2.pressKey(3);
        	   if (serverSide)
        		   serverOut.println("3");
        	   if (clientSide)
        		   clientOut.println("3");
               return (true);
           }
           if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//        	   newStatus.setText("i pressed 4");
        	   mTetrisView.pressKey(4);
//        	   mTetrisView2.pressKey(4);
        	   if (serverSide)
        		   serverOut.println("4");
        	   if (clientSide)
        		   clientOut.println("4");
        	   return (true);
           }
           if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//        	   newStatus.setText("i pressed 5");
        	   mTetrisView.pressKey(5);
//        	   mTetrisView2.pressKey(5);
        	   if (serverSide)
        		   serverOut.println("5");
        	   if (clientSide)
        		   clientOut.println("5");
               return (true);
           }
           
           return super.onKeyDown(keyCode, msg);
       }       
       
}       
