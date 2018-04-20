
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;

public class ConnectFourNew {

	// Static fields: controls allowable win orientations
	private static boolean hChk = true; // Horizontal check
	private static boolean vChk = true; // Vertical check
	private static boolean dPChk = true; // Diagonal positive check
	private static boolean dNChk = true; // Diagonal negative check
	private static boolean isOnline = false;
	// Data
	private int[][] grid;
	private int pTurn;
	private int boardSize, conToWin;
	private boolean win = false;
	private String userName = "";

	// UI items
	private JFrame frame;
	private JPanel panel;
	private ImageIcon blnk, p1, p2;
	private ImageIcon bWin, rWin, status;
	private JButton[][] button;
	private JButton clear;
	private JButton gameStatus; // <--for status button

	private DataInputStream inStream;
	private DataOutputStream outStream;
	private BufferedReader br;
	private String clientMessage="",serverMessage="";
	private Socket socket;
	public ConnectFourNew(int boardSize, int conToWin, String userName) {

		this.boardSize = boardSize;
		this.conToWin = conToWin;
		this.grid = new int[boardSize][boardSize];
		
		
		
		
		////////If a user name is inputed this player will become a client
		this.userName = userName;
		if (this.userName.length() >= 1) {
			isOnline = true;
			try{
			 Socket socket=new Socket("127.0.0.1",8888);
			    inStream=new DataInputStream(socket.getInputStream());
			    outStream=new DataOutputStream(socket.getOutputStream());
			    br=new BufferedReader(new InputStreamReader(System.in));
			   
			} catch(ConnectException ce){
				System.out.println("No server found");
			}
			
			catch(Exception e){
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
		
		System.out.println("HorizontalCheckEnabled?: " + hChk);
		System.out.println("VerticalCheckEnabled?: " + vChk);
		System.out.println("DiagonalPositiveCheckEnabled?: " + dPChk);
		System.out.println("DiagonalNegativeCheckEnabled?: " + dNChk);
		System.out.println("<>---------------------------Welcome To Connect 4 v4.6------------------------------<>");
	}

	private void setUpPanel() {
		this.button = new JButton[boardSize][boardSize];
		this.panel = new JPanel();
		this.panel.setPreferredSize(new Dimension(1000, 800));
		this.panel.setLayout(new GridLayout(boardSize, boardSize));
		this.panel.setBackground(new Color(112, 133, 146)); // panel color
		this.gameStatus = new JButton("");
		this.gameStatus.setIcon(status); // <---for game status
		this.clear = new JButton("Reset"); // clear button name
		this.clear.addActionListener(new clearListener());

		initGrid();
		
		this.panel.add(gameStatus); // <---for game status
		this.panel.add(clear); // add clear button
	}

	private void loadResources() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		blnk = new ImageIcon(classLoader.getResource("images/BlackDot.jpg"));
		p1 = new ImageIcon(classLoader.getResource("images/REDload.gif"));
		p2 = new ImageIcon(classLoader.getResource("images/BLUEload.gif"));
		bWin = new ImageIcon(classLoader.getResource("images/BLUEWin.gif"));
		rWin = new ImageIcon(classLoader.getResource("images/REDWin.gif"));
		status = new ImageIcon(classLoader.getResource("images/Preloader.gif"));
	}

	private void initGrid() {
		// -1 = restricted spot
		// 0 = empty spot
		// 1 = for player1 RED
		// 2 = for player2 BLUE
		int offset = 3; // To disable all rows except for the very bottom on initial start
		int offsetCol = 1; // to make the column size=row size
		for (int x = boardSize - offset; x >= 0; x--) {
			for (int y = boardSize - offsetCol; y >= 0; y--) {
				grid[x][y] = -1;
				//>>>>>System.out.println("Setting up restricted spot at " + x + " " + y);
			}
		}
		// Add buttons for first play through
		for (int row = 0; row < boardSize - 1; row++) {
			for (int col = 0; col < boardSize - 1; col++) {
				//>>>>>>System.out.println("Setting up button at " + row + " " + col);
				JButton button = new CoordinateButton(row, col, blnk);
				button.addActionListener(new buttonListener());
				if (grid[row][col] == 0) {
					button.setBackground(Color.cyan);
					button.setOpaque(true);
				}
				panel.add(button);
				this.button[row][col] = button;
			}
		}
	}

	private static boolean checkHorizontal(int[][] grid, int row, int col, int conToWin, int boardSize) {
		int matchsFound = 1, i = 1;
		while (i < conToWin && col - i >= 0) {
			if (grid[row][col] != grid[row][col - i])
				break;
			matchsFound++;
			i++;
		}
		i = 1;
		while (i < conToWin && col + i < boardSize - 1) {
			if (grid[row][col] != grid[row][col + i])
				break;
			matchsFound++;
			i++;
		}
		return matchsFound < conToWin ? false : true;
	}

	private static boolean checkVertical(int[][] grid, int row, int col, int conToWin, int boardSize) {
		int matchsFound = 1, i = 1;
		while (i < conToWin && row - i >= 0) {
			if (grid[row][col] != grid[row - i][col])
				break;
			matchsFound++;
			i++;
		}
		i = 1;
		while (i < conToWin && row + i < boardSize - 1) {
			if (grid[row][col] != grid[row + i][col])
				break;
			matchsFound++;
			i++;
		}
		return matchsFound < conToWin ? false : true;
	}

	private static boolean checkPositiveDiagonal(int[][] grid, int row, int col, int conToWin, int boardSize) {
		int matchsFound = 1, i = 1;
		while (i < conToWin && row - i >= 0 && col + i < boardSize - 1) {
			if (grid[row][col] != grid[row - i][col + i])
				break;
			matchsFound++;
			i++;
		}
		i = 1;
		while (i < conToWin && row + i < boardSize - 1 && col - i >= 0) {
			if (grid[row][col] != grid[row + i][col - i])
				break;
			matchsFound++;
			i++;
		}
		return matchsFound < conToWin ? false : true;
	}

	private static boolean checkNegativeDiagonal(int[][] grid, int row, int col, int conToWin, int boardSize) {
		int matchsFound = 1, i = 1;
		while (i < conToWin && row - i >= 0 && col - i >= 0) {
			if (grid[row][col] != grid[row - i][col - i])
				break;
			matchsFound++;
			i++;
		}
		i = 1;
		while (i < conToWin && row + i < boardSize - 1 && col + i < boardSize - 1) {
			if (grid[row][col] != grid[row + i][col + i])
				break;
			matchsFound++;
			i++;
		}
		return matchsFound < conToWin ? false : true;
	}

	private boolean checkWin(CoordinateButton b) {
		int row = b.getRow();
		int col = b.getCol();

		// Horizontal win check
		if (hChk && checkHorizontal(grid, row, col, conToWin, boardSize))
			win = true;

		// Vertical win check
		if (vChk && checkVertical(grid, row, col, conToWin, boardSize))
			win = true;

		// Diagonal negative check "\"
		if (dNChk && checkNegativeDiagonal(grid, row, col, conToWin, boardSize))
			win = true;

		// Diagonal positive slope check "/"
		if (dPChk && checkPositiveDiagonal(grid, row, col, conToWin, boardSize))
			win = true;
		
	///////For setting open spaces background color
		for (row = 0; row < boardSize - 1; row++) {
			for (col = 0; col < boardSize - 1; col++) {
				if (grid[row][col] == 0)
				{
					button[row][col].setBackground(Color.cyan);
					button[row][col].setOpaque(true);
				}
			}
		}
		//////			

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

			// For debugging purposes print selected location
			System.out.println("Selected Row: " + row + " Col: " + col);
			
			////////////For sending position data to server
			String clientMessage = Integer.toString(row)+", "+Integer.toString(col);
			if (isOnline == true){
				try{
				outStream.writeUTF(clientMessage);
			      outStream.flush();
			      serverMessage=inStream.readUTF();
			      System.out.println(serverMessage);
				} catch (NullPointerException e){
					System.out.println("No server to send message");
				}
				
				catch(Exception e){
					System.out.println("Client action listener exception");
					e.printStackTrace();
				}
			}
			//////////////
			
			if (grid[row][col] != 0)
				return;

			button.setBackground(null);
			button.setIcon(pTurn == 0 ? p1 : p2);
			grid[row][col] = pTurn == 0 ? 1 : 2;
			if (row != 0) {
				grid[row - 1][col] = 0; // set spot above selected to open
				ConnectFourNew.this.button[row - 1][col].setBackground(Color.CYAN);
			} else
				System.out.println("Maxed height reached");

			if (checkWin(button)) {
				System.out.println(pTurn == 0 ? "Red win" : "Blue win");
				gameStatus.setIcon(pTurn == 0 ? rWin : bWin);
				for (int i = boardSize - 2; i >= 0; i--) {
					for (int j = boardSize - 2; j >= 0; j--) {
						ConnectFourNew.this.button[i][j].setBackground(null);
						if (grid[i][j] == 0)
							ConnectFourNew.this.button[i][j].setBackground(Color.black);
						grid[i][j] = -1;
					}
				}		
				
				
				
			}
			pTurn = (pTurn + 1) % 2;
			System.out.println("");
			
			
			
			
		}
		
		
		
	}

	// Resets the game board
	private class clearListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			for (int x = 0; x < boardSize - 1; x++) {
				for (int y = 0; y < boardSize - 1; y++) {
					System.out.println("Clearing spot: " + "x=" + x + " y=" + y);
					grid[x][y] = -1;
					button[x][y].setIcon(blnk);
					button[x][y].setBackground(new JButton().getBackground());
				}
				if (x == boardSize - 2) {
					for (int y = 0; y < boardSize - 1; y++) {
						grid[x][y] = 0;
						button[x][y].setBackground(Color.cyan);
					}
				}
			}
			win = false;
			gameStatus.setIcon(status);
			System.out.println("Done");
			System.out.println("");
		}
	}



}
