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
    private PrintWriter out;
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
        public void onClick(View v) {
//        	startText.setText("server!");
        	
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

            
// 		   if (SERVERIP != null)
// 		   {
// 			   try 
// 			   {
//    			   serverStatus.setText("Listening on IP: " + SERVERIP);
//    			   serverSocket = new ServerSocket(SERVERPORT);
//				   // LISTEN FOR INCOMING CLIENTS
//				   Socket client = serverSocket.accept();
//				   BufferedReader serverIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
//				   PrintWriter serverOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
//    			   while (true)
//    			   {
//    				   try 
//    				   {
//	
//	    				   setContentView(R.layout.tetris_layout);
//				            			  
//	    				   TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
//	    				   mTetrisView.setTextView((TextView) findViewById(R.id.text));
//			                            
//	    				   mTetrisView.setMode(READY);
//    				   } catch (Exception e) 
//    				   {
//    					   serverStatus.setText("wat");
//    				   }
//    			   }
//    		   } catch (Exception e) 
//               {
//               	serverStatus.setText("Oops. Problem.");
//               }
// 		   }
      }
    };
    
    
    
    
//    				   try {
//    					   String line = null;
//    					   BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//    					   out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
//    					   Log.d(TAG, "1 " + line);
//    					   while ((line = in.readLine()) != null)
//    					   {
//        					   Log.d(TAG, "2 " + line);
//    						   if (line.equals("playtetris"))
//    							   clientReady = true;
//    						   
////    						   Log.d("ServerActivity", line);
//    						   handler.post(new Runnable()
//    						   {
//    							   @Override
//    							   public void run()
//    							   {
//            						   serverStatus.setText("Hit play to play");
//            						   
//            						   //listen for message and update
////            						   Message m = new Message();
////            						   m.what = MSG_ID;
////									   fromClient = line;
////									   serverHandler.sendMessage(m);
//
//            						   Log.d(TAG, "client: " + clientReady + "server: " + serverReady);
//									      
//					            		  //send a message
//					            		  String sendMsg = mServerMsg.getText().toString();
//					            		  //output the message
//					            		  if (serverReady)
//					            			  out.println("playtetris");
//					            		  else
//					            			  out.println(sendMsg + "\n");
//					            		  
//					            		  out.flush();
//					            		  
//					            		  if (serverReady && clientReady)
//					            		  {
//			        						   setContentView(R.layout.tetris_layout);
//					            			  
//			        						   TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
//			        						   mTetrisView.setTextView((TextView) findViewById(R.id.text));
//					                            
//			        						   mTetrisView.setMode(READY);
//					            		  }
//
//    							   }
//    						   });
//    					   }
//    					   break;
//    				   } catch (Exception e) {
//    					   handler.post(new Runnable() {
//    						   @Override
//    						   public void run()
//    						   {
//    							   serverStatus.setText("Oops. Cnx interrupt");
//    						   }
//    					   });
//    					   e.printStackTrace();
//    				   }
//    			   }
//    		   } else {
//    			   handler.post(new Runnable() {
//    				   @Override
//    				   public void run () {
//    					   serverStatus.setText("Couldn't detect");
//    				   }
//    			   });
//    		   }
//    	   } catch (Exception e) {
//    		   handler.post(new Runnable() {
//    			   @Override
//    			   public void run () {
//    				   serverStatus.setText("ERROR");
//    			   }
//    		   });
//    		   e.printStackTrace();
//    	   }
//
//     	   
//     	   
//     	   
//        }
//    };

    private OnClickListener clientClick = new OnClickListener() {

        @Override
          public void onClick(View v) {
//          	startText.setText("client!");
          	
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
    		  if (out != null)
    			  out.println("playtetris");

     		  out.flush();
    		  
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
//              Socket s = null;
//              try {
//            	  serverSocket = new ServerSocket(SERVERPORT);
//              } catch (IOException e) {
//                 e.printStackTrace();
//              }
//              while (!Thread.currentThread().isInterrupted()) {
//            	  Message m = new Message();
//            	  m.what = MSG_ID;
//            	  try {
//            		  if (s == null)
//            			  s = serverSocket.accept();
//                    
//            		  //listen for message and update
//            		  BufferedReader input = new BufferedReader(
//            				  new InputStreamReader(s.getInputStream()));
//            		  String st = null;
//            		  st = input.readLine();
//            		  fromClient = st;
//            		  serverHandler.sendMessage(m);
//                                     
//            		  //send a message
//            		  String sendMsg = mServerMsg.getText().toString();
//            		  out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s
//            				  .getOutputStream())), true);
//            		  //output the message
//            		  out.println(sendMsg + "\n");
//            		  out.flush();
//
//                 } catch (IOException e) {
//                	 e.printStackTrace();
//                 }
//              }
        	   
//start #2
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
        					   String line = null;
        					   BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        					   out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
     						   serverStatus = (TextView) findViewById(R.id.text2);
        					   Log.d(TAG, "1 " + line);
        					   while ((line = in.readLine()) != null)
        					   {
            					   Log.d(TAG, "2 " + line);
        						   if (line.equals("playtetris"))
        							   clientReady = true;
        						   else
        							   serverStatus.setText(line);

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
 			        						   setContentView(R.layout.tetris_layout);
 					            			  
 			        						   mTetrisView = (TetrisView) findViewById(R.id.tetris);
 			        						   mTetrisView.setTextView((TextView) findViewById(R.id.text));
 					                            
 			        						   mTetrisView.setMode(READY);
// 			        						   serverStatus = (TextView) findViewById(R.id.text2);
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
//               try {
//                   InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
//                   //connecting
//                   Socket s = new Socket(serverAddr, ServerActivity.SERVERPORT);
//                   connected = true;
////
////                   if (connected)
////                   {
////                	   setContentView(R.layout.tetris_layout);
////                   
////                	   TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
////                	   mTetrisView.setTextView((TextView) findViewById(R.id.text));
////                    
////                	   mTetrisView.setMode(READY);
////                   }
////                   
//                   //connected
//                   while (connected) 
//                   {
////                	   Message m = new Message();
////                       m.what = MSG_ID;
//                       try 
//                       {
//                    	   //send a message
//                    	   String sendMsg = mClientMsg.getText().toString();
//                           PrintWriter outclient = new PrintWriter(new BufferedWriter(
//   	                       		new OutputStreamWriter(s.getOutputStream())), true);
//                           //output the message
//                           outclient.println(sendMsg + "\n");
//                           outclient.flush();
//
//                           //listen for message and update
//                           BufferedReader input = new BufferedReader(
//                        		   new InputStreamReader(s.getInputStream()));
//                           String st = null;
//                           st = input.readLine();
//                           fromServer = st;
//
////                           Log.d("ClientActivity", fromServer);
//                   		
//                           if(fromServer.equals("hi"))
//                   				fromServer = "bye";
//
//                           //run tetris
//                           if(fromServer.equals("playtetris"))
//                           {
//                        	   setContentView(R.layout.tetris_layout);
//                            
//                        	   TetrisView mTetrisView = (TetrisView) findViewById(R.id.tetris);
//                        	   mTetrisView.setTextView((TextView) findViewById(R.id.text));
//                            
//                        	   mTetrisView.setMode(READY);
//                           }
////                   		clientHandler.sendMessage(m);
//                             
//                           
//                           //flush
//                               
//                               
//                           //Sent
//                       } catch (Exception e) {
//                           Log.e("ClientActivity", "S: Error", e);
//                       }
//                   }
//                   s.close();
////                   Log.d("ClientActivity", "C: Closed.");
//               } catch (Exception e) {
////                   Log.e("ClientActivity", "C: Error", e);
//                   connected = false;
//               }
               
//start #2
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
     						   clientStatus = (TextView) findViewById(R.id.text2);
        					   Log.d(TAG, "1 " + fromServer);
        					   while ((fromServer = clientIn.readLine()) != null)
        					   {
            					   Log.d(TAG, "2: " + fromServer);
        						   if (fromServer.equals("playtetris"))
        							   serverReady = true;
        						   else
        							   clientStatus.setText(fromServer);
        						   
        						   Log.d(TAG, "client: " + clientReady + "server: " + serverReady);
        						   
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
 					            			  setContentView(R.layout.tetris_layout);
 					            			  
 					            			  mTetrisView = (TetrisView) findViewById(R.id.tetris);
 					            			  mTetrisView.setTextView((TextView) findViewById(R.id.text));
 				                            
 					            			  mTetrisView.setMode(READY);
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
    	   
    	   if (mTetrisView == null || mTetrisView2 == null)
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
           if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
           	mTetrisView.pressKey(5);
           	mTetrisView2.pressKey(5);
               return (true);
           }
           
           return super.onKeyDown(keyCode, msg);
       }       
       
}       
