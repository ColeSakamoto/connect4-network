
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class ConnectFourNew {

	private JFrame frame;
	private JPanel panel;
	
	private final int rowTiles = BOARDSIZE;
	private final int colTiles = BOARDSIZE;
	private static int[][] grid = new int[1][1];
	private int row, col = 0;
	private int pTurn = 0;
	private boolean win = false;
	private JButton[][] button = new JButton[rowTiles][colTiles];
	private JButton clear;
	
	private GridLayout myGrid = new GridLayout(BOARDSIZE, BOARDSIZE);

	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private URL resource = classLoader.getResource("images/BlackDot.jpg");
	private URL resource1 = classLoader.getResource("images/REDload.gif");
	private URL resource2 = classLoader.getResource("images/BLUEload.gif");
	private URL resource3 = classLoader.getResource("images/BLUEWin.gif");
	private URL resource4 = classLoader.getResource("images/REDWin.gif");
	private URL resource5 = classLoader.getResource("images/Preloader.gif");
	private final ImageIcon blnk = new ImageIcon(resource);
	private final ImageIcon p1 = new ImageIcon(resource1);
	private final ImageIcon p2 = new ImageIcon(resource2);
	private final ImageIcon bWin = new ImageIcon(resource3);
	private final ImageIcon rWin = new ImageIcon(resource4);
	private final ImageIcon status = new ImageIcon(resource5);
	private static int BOARDSIZE;
	private static int CONTOWIN;

	//Controls allowable win orientations
	private boolean hChk = true; //Horizontal check
	private boolean vChk = true; //Vertical check
	private boolean dPChk = true; //Diagonal positive check
	private boolean dNChk = true; //Diagonal negative check 

	private JButton gameStatus; // <--for status button

	public ConnectFourNew() {
		frame = new JFrame("Welcome to Connect Four"); //Window title
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel();
		panel.setLayout(myGrid);
		panel.setBackground(new Color(112,133,146)); //panel color

		gameStatus = new JButton("");

		clear = new JButton("Reset");   //clear button name
		clear.addActionListener(new clearListener());
		
		panel.setPreferredSize(new Dimension(1000, 800));

		// -1 = restricted spot
		// 0 = empty spot
		// 1 = for player1 RED
		// 2 = for player2 BLUE

		int offset = 3; // To disable all rows except for the very bottom on
						// initial start
		int offsetCol = 1; // to make the column size=row size
		for (int x = BOARDSIZE - offset; x >= 0; x--) {
			for (int y = BOARDSIZE - offsetCol; y >= 0; y--) {
				grid[x][y] = -1;
				System.out.println("Setting up restricted spot at " + x + " "
						+ y);
			}
		}
		//Add buttons for first play through
		for (row = 0; row < BOARDSIZE - 1; row++) {
			for (col = 0; col < BOARDSIZE - 1; col++) {
				System.out.println("Setting up button at " + row + " " + col);
				button[row][col] = new JButton(blnk);
				button[row][col].addActionListener(new buttonListener());
				panel.add(button[row][col]);
			}
		}
		///Find "open space" and set color
		for (row = 0; row < BOARDSIZE - 1; row++) {
			for (col = 0; col < BOARDSIZE - 1; col++) {
				if (grid[row][col] == 0)
				{
					button[row][col].setBackground(Color.cyan);
					button[row][col].setOpaque(true);
				}
			}
		}
		//////
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		panel.add(gameStatus); // <---for game status
		gameStatus.setIcon(status); // <---for game status
		panel.add(clear); //add clear button
		System.out.println("HorizontalCheckEnabled?: " + hChk);
		System.out.println("VerticalCheckEnabled?: " + vChk);
		System.out.println("DiagonalPositiveCheckEnabled?: " + dPChk);
		System.out.println("DiagonalNegativeCheckEnabled?: " + dNChk);
		System.out
				.println("<>---------------------------Welcome To Connect 4 v4.6------------------------------<>");
	}

	private class buttonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			for (row = BOARDSIZE - 2; row >= 0; row--) {
				for (col = BOARDSIZE - 1; col >= 0; col--) {
					if (button[row][col] == event.getSource()) {
						if (pTurn % 2 == 0 && grid[row][col] == 0) {
							button[row][col].setIcon(p1);
							grid[row][col] = 1;
						
							System.out.println("Selected Row: " + row
									+ " Col: " + col );
							try {
								grid[row - 1][col] = 0; // set spot above
														// selected to open
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("Maxed height reached");
							}
																					
							///Find "open space" and set color
							for (row = 0; row < BOARDSIZE - 1; row++) {
								for (col = 0; col < BOARDSIZE - 1; col++) {
									if (grid[row][col] == 0)
									{
										button[row][col].setBackground(Color.cyan);
										button[row][col].setOpaque(true);
									}
								}
							}
							//////
														
							if (checkWin()) {
								System.out.println("Red win"); // player1
								gameStatus.setIcon(rWin);
								for (int x = BOARDSIZE - 1; x >= 0; x--) {
									for (int y = BOARDSIZE - 1; y >= 0; y--) {
										grid[x][y] = -1;
									}
								}
							}
							pTurn = pTurn + 1;
							System.out.println("");
							break;
						}
																	
						if (pTurn % 2 == 1 && grid[row][col] == 0) {
							button[row][col].setIcon(p2);
							grid[row][col] = 2;
						
							System.out.println("Selected Row: " + row
									+ " Col: " + col );
							try {
								grid[row - 1][col] = 0; // set spot above
														// selected to open
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("Maxed height reached");
							}
							
						///////For setting open spaces background color
							for (row = 0; row < BOARDSIZE - 1; row++) {
								for (col = 0; col < BOARDSIZE - 1; col++) {
									if (grid[row][col] == 0)
									{
										button[row][col].setBackground(Color.cyan);
										button[row][col].setOpaque(true);
									}
								}
							}
							//////
																												
							if (checkWin()) {
								System.out.println("Blue win"); // player2
								gameStatus.setIcon(bWin);
								for (int x = BOARDSIZE - 1; x >= 0; x--) {
									for (int y = BOARDSIZE - 1; y >= 0; y--) {
										grid[x][y] = -1;
									}
								}
							}
													
							pTurn = pTurn + 1; //change to next player
							System.out.println("");
							break;
						} 
												
						else {
							System.out.println("");
							//For debugging purposes print selected location
							System.out.println("Selected Row: " + row
									+ " Col: " + col );
						}
					}
				}
			}
		}
	}
	
	//Resets the game board
	private class clearListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			for (int x = BOARDSIZE - 2; x >= 0; x--) {
				for (int y = BOARDSIZE - 2; y >= 0; y--) {
					System.out.println("Clearing spot: " + "x=" + x + " y=" + y);
					grid[x][y] = -1;
					button[x][y].setIcon(blnk);

				}
			}
			for (int y = colTiles - 1; y >= 0; y--) {
				grid[BOARDSIZE - 2][y] = 0;
			}

			win = false;
			gameStatus.setIcon(status);
			
			
			///////For setting open spaces background color
			for (row = 0; row < BOARDSIZE - 1; row++) {
				for (col = 0; col < BOARDSIZE - 1; col++) {
					
						button[row][col].setBackground(null);
						if (grid[row][col] == 0) {
							button[row][col].setBackground(Color.cyan);
						}											
				}
			}
			///////////////
			System.out.println("Done");
			System.out.println("");
		}
	}

	private boolean checkWin() {

		///Find "open space" and remove background color
		for (row = 0; row < BOARDSIZE - 1; row++) {
			for (col = 0; col < BOARDSIZE - 1; col++) {
				if (grid[row][col] == 1 || grid[row][col] == 2)
				{
					button[row][col].setBackground(null);
					
				}
			}
		}
		//////
		
		int matchsFound = 1;
		// Horizontal win check
		if (hChk == true) {
			
			for (int x = 0; x < BOARDSIZE; x++) {
				for (int y = 0; y < BOARDSIZE - CONTOWIN + 1; y++) { // added +1 for offset

					if (grid[x][y] != 0 && grid[x][y] != -1) {
						for (int c = 1; c < CONTOWIN; c++) {
							System.out.println("Horizontal check: "+"x:"+x+" "+"y:"+y+" "+"c:"+c); //Debugging
							if (grid[x][y] == grid[x][y + c]) {
								matchsFound++;
							}
						}

					}
					if (matchsFound >= CONTOWIN) {
						win = true;

					}
					matchsFound = 1;
				}
			}
		}
		// Vertical win check
		if (vChk == true) {
			
			for (int x = 0; x < BOARDSIZE - CONTOWIN; x++) {
				for (int y = 0; y < BOARDSIZE; y++) {

					if (grid[x][y] != 0 && grid[x][y] != -1) {
						
						for (int c = 1; c < CONTOWIN; c++) {
							System.out.println("Vertical check: "+"x:"+x+" "+"y:"+y+" "+"c:"+c); //Debugging
							if (grid[x][y] == grid[x + c][y]) {
								matchsFound++;
							}
						}
					}

					if (matchsFound >= CONTOWIN) {
						win = true;

					}
					matchsFound = 1;

				}
			}
		}
		// Diagonal negative check "\" 
		if (dNChk == true) {
			
			for (int x = 0; x < BOARDSIZE - CONTOWIN; x++) {
				for (int y = 0; y < BOARDSIZE - CONTOWIN; y++) { 
																	
					if (grid[x][y] != 0 && grid[x][y] != -1) {
						
						for (int c = 1; c < CONTOWIN; c++) {
							if (grid[x][y] == grid[x + c][y + c]) {
								System.out.println("Diagonal positive check: "+"x:"+x+" "+"y:"+y+" "+"c:"+c); //Debugging
								matchsFound++;
							}
						}
					}
					if (matchsFound >= CONTOWIN) {
						win = true;

					}
					matchsFound = 1;
				}
			}
		}
		// Diagonal positive slope check "/"
		if (dPChk == true) {
			if (this.col != 0) {
				
				for (int x = 0; x < BOARDSIZE; x++) {
					for (int y = 0; y < BOARDSIZE - CONTOWIN; y++) {
						if ((grid[x][y] != 0) && (grid[x][y] != -1) ) {
							for (int c = 1; c < CONTOWIN; c++) {
								
								
								if ((x-c >= 0)&&(y+c <= BOARDSIZE+2)){ //Boundary security  +2 for offset
									System.out.println("Diagonal negative check: "+"x:"+x+" "+"y:"+y+" "+"c:"+c); //Debugging
									if (grid[x][y] == grid[(x - c)][(y + c)] ) {
									matchsFound++;
									}
								}
							}
							System.out.println("");
						}
						if (matchsFound >= CONTOWIN) {
							this.win = true;
							matchsFound = 0;
						}
						matchsFound = 1;
					}
				}
			}
		}
		
		
		///////For setting open spaces background color
		if(this.win==true)
		for (row = 0; row < BOARDSIZE - 1; row++) {
			for (col = 0; col < BOARDSIZE - 1; col++) {
				
					button[row][col].setBackground(null);
					if (grid[row][col] == 0) {
						button[row][col].setBackground(Color.black);
					}									
			}
		}
		///////////////										
		return this.win;
	}
	
	

}
