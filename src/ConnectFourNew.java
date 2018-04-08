
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class ConnectFourNew {

	// Data
	private int[][] grid;
	//private int row, col, 
	private int pTurn;
	private int boardSize, conToWin;
	private boolean win = false;

	// Controls allowable win orientations
	private boolean hChk = true; // Horizontal check
	private boolean vChk = true; // Vertical check
	private boolean dPChk = true; // Diagonal positive check
	private boolean dNChk = true; // Diagonal negative check

	// UI items
	private JFrame frame;
	private JPanel panel;
	private ImageIcon blnk, p1, p2;
	private ImageIcon bWin, rWin, status;
	private JButton[][] button;
	private JButton clear;
	private JButton gameStatus; // <--for status button

	public ConnectFourNew(int boardSize, int conToWin) {

		this.boardSize = boardSize;
		this.conToWin = conToWin;
		this.grid = new int[boardSize][boardSize];

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
				System.out.println("Setting up restricted spot at " + x + " " + y);
			}
		}
		// Add buttons for first play through
		for (int row = 0; row < boardSize - 1; row++) {
			for (int col = 0; col < boardSize - 1; col++) {
				System.out.println("Setting up button at " + row + " " + col);
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
			CoordinateButton button = (CoordinateButton)event.getSource();
			final int row = button.getRow();
			final int col = button.getCol();
			
			if (grid[row][col] == 0) {
				button.setIcon(pTurn % 2 == 0 ? p1 : p2);
				grid[row][col] = pTurn % 2 == 0 ? 1 : 2;

				System.out.println("Selected Row: " + row + " Col: " + col);
				
				if (row != 0) {
					grid[row - 1][col] = 0; // set spot above selected to open	
					ConnectFourNew.this.button[row - 1][col].setBackground(Color.CYAN);;
				}
				else
					System.out.println("Maxed height reached");

				if (checkWin()) {
					System.out.println("Red win"); // player1
					gameStatus.setIcon(rWin);
					for (int x = boardSize - 1; x >= 0; x--) {
						for (int y = boardSize - 1; y >= 0; y--) {
							grid[x][y] = -1;
						}
					}
				}
				pTurn = pTurn + 1;
				System.out.println("");
			}

			else {
				System.out.println("");
				// For debugging purposes print selected location
				System.out.println("Selected Row: " + row + " Col: " + col);
			}
		}
	}

	// Resets the game board
	private class clearListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			for (int x = boardSize - 2; x >= 0; x--) {
				for (int y = boardSize - 2; y >= 0; y--) {
					System.out.println("Clearing spot: " + "x=" + x + " y=" + y);
					grid[x][y] = -1;
					button[x][y].setIcon(blnk);
				}
			}
			for (int y = boardSize - 1; y >= 0; y--) {
				grid[boardSize - 2][y] = 0;
			}

			win = false;
			gameStatus.setIcon(status);

			/////// For setting open spaces background color
			for (int row = 0; row < boardSize - 1; row++) {
				for (int col = 0; col < boardSize - 1; col++) {

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

		/// Find "open space" and remove background color
		for (int row = 0; row < boardSize - 1; row++) {
			for (int col = 0; col < boardSize - 1; col++) {
				if (grid[row][col] == 1 || grid[row][col] == 2) {
					button[row][col].setBackground(null);

				}
			}
		}
		//////

		int matchsFound = 1;
		// Horizontal win check
		if (hChk == true) {

			for (int x = 0; x < boardSize; x++) {
				for (int y = 0; y < boardSize - conToWin + 1; y++) { // added +1 for offset

					if (grid[x][y] != 0 && grid[x][y] != -1) {
						for (int c = 1; c < conToWin; c++) {
							System.out.println("Horizontal check: " + "x:" + x + " " + "y:" + y + " " + "c:" + c); // Debugging
							if (grid[x][y] == grid[x][y + c]) {
								matchsFound++;
							}
						}

					}
					if (matchsFound >= conToWin) {
						win = true;

					}
					matchsFound = 1;
				}
			}
		}
		// Vertical win check
		if (vChk == true) {

			for (int x = 0; x < boardSize - conToWin; x++) {
				for (int y = 0; y < boardSize; y++) {

					if (grid[x][y] != 0 && grid[x][y] != -1) {

						for (int c = 1; c < conToWin; c++) {
							System.out.println("Vertical check: " + "x:" + x + " " + "y:" + y + " " + "c:" + c); // Debugging
							if (grid[x][y] == grid[x + c][y]) {
								matchsFound++;
							}
						}
					}

					if (matchsFound >= conToWin) {
						win = true;

					}
					matchsFound = 1;

				}
			}
		}
		// Diagonal negative check "\"
		if (dNChk == true) {

			for (int x = 0; x < boardSize - conToWin; x++) {
				for (int y = 0; y < boardSize - conToWin; y++) {

					if (grid[x][y] != 0 && grid[x][y] != -1) {

						for (int c = 1; c < conToWin; c++) {
							if (grid[x][y] == grid[x + c][y + c]) {
								System.out.println(
										"Diagonal positive check: " + "x:" + x + " " + "y:" + y + " " + "c:" + c); // Debugging
								matchsFound++;
							}
						}
					}
					if (matchsFound >= conToWin) {
						win = true;

					}
					matchsFound = 1;
				}
			}
		}
		// Diagonal positive slope check "/"
		if (dPChk == true) {
				for (int x = 0; x < boardSize; x++) {
					for (int y = 0; y < boardSize - conToWin; y++) {
						if ((grid[x][y] != 0) && (grid[x][y] != -1)) {
							for (int c = 1; c < conToWin; c++) {

								if ((x - c >= 0) && (y + c <= boardSize + 2)) { // Boundary security +2 for offset
									System.out.println(
											"Diagonal negative check: " + "x:" + x + " " + "y:" + y + " " + "c:" + c); // Debugging
									if (grid[x][y] == grid[(x - c)][(y + c)]) {
										matchsFound++;
									}
								}
							}
							System.out.println("");
						}
						if (matchsFound >= conToWin) {
							this.win = true;
							matchsFound = 0;
						}
						matchsFound = 1;
					}
				}
		}

		/////// For setting open spaces background color
		if (this.win == true)
			for (int row = 0; row < boardSize - 1; row++) {
				for (int col = 0; col < boardSize - 1; col++) {

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
