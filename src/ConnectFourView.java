
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ConnectFourView {

	// Static fields: controls allowable win orientations
	private static boolean isOnline = false;
	// Data
	private GameBoard board;
	private int boardSize, conToWin;
	private boolean win = false;
	private String userName = "";
	private int clientNo = 0;
	private boolean startWk = false;

	// UI items
	private JFrame frame;
	private JPanel panel;
	private ImageIcon blnk, p1, p2;
	private ImageIcon bWin, rWin, status;
	private JButton[][] button;
	private JButton clear;
	private JButton gameStatus; // <--for status button
	private JCheckBox player1;
	private JCheckBox player2;

	private DataInputStream inStream;
	private DataOutputStream outStream;
	private DataInputStream inStreamWk;
	private DataOutputStream outStreamWk;
	private BufferedReader br;
	private String clientMessage="",serverMessage="";
	private Socket socket;
	boolean startCheck = false;

	public ConnectFourView(int boardSize, int conToWin, String userName) {

		this.boardSize = boardSize;
		this.conToWin = conToWin;
		this.board = new GameBoard(boardSize, conToWin);

		////////If a user name is inputed this player will become a client
		this.userName = userName;
		if (this.userName.length() >= 1) {
			isOnline = true;
			try {
			 		Socket socket=new Socket("127.0.0.1",8888); //Main connection
			    inStream=new DataInputStream(socket.getInputStream());
			    outStream=new DataOutputStream(socket.getOutputStream());

			    Socket socketwk=new Socket("127.0.0.1",8888); //Connection for worker thread to prevent cross communication
			    inStreamWk=new DataInputStream(socketwk.getInputStream());// between the main connection
			    outStreamWk=new DataOutputStream(socketwk.getOutputStream());

			    outStream.writeUTF("start"); //Signal keyword for requesting boardSize and conToWin arguments from server
		      outStream.flush();
		      serverMessage=inStream.readUTF();//Wait for server response
		      System.out.println("From Server: "+serverMessage);
		      String[] val = serverMessage.split(",");
		      this.boardSize = Integer.parseInt(val[0]); //Get boardSize argument from server
		      this.conToWin = Integer.parseInt(val[1]); //Get Connections to win argument from server
		      clientNo = Integer.parseInt(val[2]); //Get client number from server
		      System.out.println("ClientNo: "+clientNo);
		      WorkerThread wk = new WorkerThread(); //Create the worker thread
		      wk.start();
			} catch (NumberFormatException num){
				System.out.println("Server arguments exception");
				num.printStackTrace();
			} catch(ConnectException ce){
				System.out.println("No server found");
			} catch(Exception e){
				e.printStackTrace();
			}
		}/////////////////


		loadResources();
		setUpPanel();

		this.frame = new JFrame("Welcome to Connect Four"); // Window title
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setContentPane(panel);
		this.frame.pack();
		this.frame.setVisible(true);

		System.out.println("<>---------------------------Welcome To Connect 4 v4.6------------------------------<>");
	}

	private void setUpPanel() {
		this.button = new JButton[boardSize][boardSize];
		this.panel = new JPanel();
		this.panel.setPreferredSize(new Dimension(500, 400));
		this.panel.setLayout(new GridLayout(boardSize + 1, boardSize + 1));
		this.panel.setBackground(new Color(112, 133, 146)); // panel color
		this.gameStatus = new JButton("");
		this.gameStatus.setIcon(status); // <---for game status
		this.clear = new JButton("Reset"); // clear button name
		this.clear.addActionListener(new clearListener());
		this.player1 = new JCheckBox("player1", true);
		//this.player1.addActionListener(new player1Listener());
		this.player2 = new JCheckBox("player2", true);

		initGrid();

		this.panel.add(gameStatus); // <---for game status

		this.panel.add(clear); // add clear button
		if (!isOnline) {
			this.panel.add(player1);
			this.panel.add(player2);
		}
	}

	private void loadResources() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		blnk = null;//new ImageIcon(classLoader.getResource("images/BlackDot.jpg"));
		p1 = new ImageIcon(classLoader.getResource("images/REDload.gif"));
		p2 = new ImageIcon(classLoader.getResource("images/BLUEload.gif"));
		bWin = new ImageIcon(classLoader.getResource("images/BLUEWin.gif"));
		rWin = new ImageIcon(classLoader.getResource("images/REDWin.gif"));
		status = new ImageIcon(classLoader.getResource("images/Preloader.gif"));
	}

	private void initGrid() {
		board.initGrid();
		//board.printBoard();
		System.out.println("boardSize: " + boardSize);

		// Add buttons for first play through
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				//>>>>>>System.out.println("Setting up button at " + row + " " + col);
				JButton button = new CoordinateButton(row, col, blnk);
				button.addActionListener(new buttonListener());
				if (board.get(row, col) == 0) {
					button.setBackground(Color.cyan);
					button.setOpaque(true);
				}
				panel.add(button);
				this.button[row][col] = button;
			}
		}

		nextPlayer();
	}

	private boolean checkWin(CoordinateButton b) {
		System.out.println("inside checkWin in ConnectFourView");

		// win check
		win = board.checkWin();

		updateView();
		return this.win;
	}

	@SuppressWarnings("serial")
	private static class CoordinateButton extends JButton {
		int row, col;

		public CoordinateButton(int row, int col, Icon ic) {
			super(ic);
			this.row = row;
			this.col = col;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}
	}

	private class buttonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			CoordinateButton button = (CoordinateButton) event.getSource();
			final int row = button.getRow();
			final int col = button.getCol();

			startCheck = true; //For starting win check in worker thread
			// For debugging purposes print selected location
			System.out.println("Selected Row: " + row + " Col: " + col);

			////////////For sending position data to server

			if (isOnline == true) {
				try{
					System.out.println("At buttonListener to server: " + "turn," + Integer.toString(row) + "," + Integer.toString(col));
					outStream.writeUTF("turn," + Integer.toString(row)+","+Integer.toString(col));
		      outStream.flush();
		      String message =inStream.readUTF();
		      System.out.println("From server at ButtonListener: " + message);
		      if (message.equals("bad") || message.equals("restrict")) {
						// Not player's turn so do nothing
						return;
					}

					button.setBackground(null);
					if (clientNo == 1){
						button.setIcon(p1);
					} else {
						button.setIcon(p2);
					}
					board.set(row, col, 1);
					if (row != 0) {
						board.set(row - 1, col, 0); // set spot above selected to open
						ConnectFourView.this.button[row - 1][col].setBackground(Color.CYAN);
						ConnectFourView.this.button[row - 1][col].setOpaque(true);
					} else {
						System.out.println("Maxed height reached");
					}
				} catch (SocketException e){
					System.out.println("Game has been won: socket exception");
				} catch (EOFException e){
					System.out.println("Game has been won: EOF exception");
				} catch (NullPointerException e){
					System.out.println("No server to send message");
				} catch(Exception e){
					System.out.println("Client action listener exception");
					e.printStackTrace();
				}
			} else {
				// offline
				if (board.get(row, col) != 0) {
					return;
				}

				button.setBackground(null);
				button.setIcon(board.getCurrentPlayer() == 1 ? p1 : p2);
				board.set(row, col, board.getCurrentPlayer());
				if (row != 0) {
					board.set(row - 1, col, 0); // set spot above selected to open
					ConnectFourView.this.button[row - 1][col].setBackground(Color.CYAN);
				} else {
					System.out.println("Maxed height reached");
				}

				if (checkWin(button)) {
					updateView();
					// for (int i = boardSize - 1; i >= 0; i--) {
					// 	for (int j = boardSize - 1; j >= 0; j--) {
					// 		if (board.get(i, j) == 0) {
					// 			ConnectFourView.this.button[i][j].setBackground(Color.black);
					// 			board.set(i, j, -1);
					// 		} else {
					// 			ConnectFourView.this.button[i][j].setBackground(null);
					// 		}
					// 	}
					// }
					return;
				}
				nextPlayer();
			}
		}
	}

	// CHeck if the next player is AI
	public void nextPlayer() {
		//board.printBoard();
		//System.out.println("checking next player. currentPlayer" + board.getCurrentPlayer());
		if (board.getCurrentPlayer() == 1 && !player1.isSelected() ||
				board.getCurrentPlayer() == 2 && !player2.isSelected()) {
				AI ai = new AI(board);
				int col = ai.bestMove();
				int row = board.addChip(col);
				//System.out.println("AI move: row "+ row + ", col: " + col);
				updateView();
		}
	}

	public void updateView() {
		System.out.println("inside updateView");
		//board.printBoard();
		if (board.getWinner() > 0) {
			System.out.println(board.getLastClient() == 1 ? "Red win" : "Blue win");
			gameStatus.setIcon(board.getLastClient() == 1 ? rWin : bWin);
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				int val = board.get(i, j);
				if (val > 0) {
					button[i][j].setIcon(val == 1 ? p1 : p2);
					button[i][j].setBackground(null);
				} else if (val == 0) {
					//System.out.println("i,j= " + i + " " + j );
					button[i][j].setBackground(Color.CYAN);
					button[i][j].setOpaque(true);
				}
			}
		}
	}

	// Resets the game board
	class clearListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			board.initGrid();
			for (int x = 0; x < boardSize; x++) {
				for (int y = 0; y < boardSize; y++) {
					button[x][y].setIcon(blnk);
					button[x][y].setBackground(null);
					if (board.get(x, y) == 0) {
						button[x][y].setBackground(Color.cyan);
					}
				}
			}

			win = false;
			gameStatus.setIcon(status);

			///////////////
			System.out.println("Done");
			System.out.println("");

			nextPlayer();
		}
  }

	// class player1Listener implents ActionListener {
	// 	public void actionPerformed(ActionEvent event) {
	// 		System.out.println(player.isSelected)
	// 	}
	// }

	class WorkerThread extends Thread {
		public void run() {
			int row = 0;
			int col = 0;
			String[] val;
	    try {
	    	while(true) {
	      	TimeUnit.SECONDS.sleep(1);
					outStreamWk.writeUTF("lastMove");
					outStreamWk.flush();
					String message1 = inStreamWk.readUTF();//Wait for server response
					val = message1.split(",");
					System.out.println("From Server Lastmove?: "+message1);
					if (!val[0].equals("good1") && !val[0].equals("Taken")
						&& Integer.parseInt(val[0]) != clientNo
						&& Integer.parseInt(val[0]) != 0) {
							row = Integer.parseInt(val[1]);
							col = Integer.parseInt(val[2]);
							if (row < 0 || col < 0) continue;
							button[col][row].setBackground(null);

							if (clientNo == 2) {
								button[col][row].setIcon(p1);
							} else {
								button[col][row].setIcon(p2);
							}

							startCheck = true; // start win checking
							 // System.out.println("From Server Lastmove?: "+message1);
							//board.set(row, col, 2);

							if (col > 0){
								button[col-1][row].setBackground(Color.cyan);
								button[col-1][row].setOpaque(true);
								//if (row - 2 >= 0)
								// grid[row - 2][col] = 0; // set spot above selected to open
							}
		      }

					if (startCheck == true) {
			      outStreamWk.writeUTF("checkWin");
			      outStreamWk.flush();
			      message1 = inStreamWk.readUTF();//Wait for server response
			      //System.out.println("From Server checkWin?: "+message1);
			      if (message1.contains("true")) {
			    	  val = message1.split(",");
			    	  int lastClient = Integer.parseInt(val[1]);
							System.out.println(lastClient == 1 ? "Red win" : "Blue win");
							gameStatus.setIcon(lastClient == 1 ? rWin : bWin);
							outStream.writeUTF("exit");// Exit the server
					    outStream.flush();
					    outStreamWk.writeUTF("exit");// Exit the server
					    outStreamWk.flush();
							break;
						}
         	}
	     	}
	    }
      catch (Exception e)
      {
          e.printStackTrace();
      }
    }
	}
}
