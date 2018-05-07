
public class GameBoard {

	int lastRow = 0;
	int lastCol = 0;
	int boardSize = 0;
	int conToWin = 0;
	int lastClient = 0; //Change last made by player 1 or 2?
	boolean win = false;
	int turn = 1; //Player's turn 1 or 2

	int grid[][];

	public GameBoard(int boardSize, int conToWin){
		this.boardSize = boardSize;
		this.conToWin = conToWin;
		initGrid();
	}

	public void initGrid(){
		// -1 = restricted spot
		// 0 = empty spot
		// 1 = for player1 RED
		// 2 = for player2 BLUE
		// Disable all rows except bottom row
		grid = new int[boardSize][boardSize];
		for (int x = 0; x < boardSize - 1; x++) {
			for (int y = 0; y < boardSize; y++) {
				grid[x][y] = -1;
			}
		}
	}

	public void setLastMove(int row, int col){
		this.lastRow = row;
		this.lastCol = col;
	}

	public int get(int row, int col) {
		return grid[row][col];
	}

	public void set(int row, int col, int val) {
		grid[row][col] = val;
		if (val > 0) {
			lastRow = row;
			lastCol = col;
			lastClient = val;
		}
	}

	public int getlastRow(){
		return this.lastRow;
	}
	public int getlastCol(){
		return this.lastCol;
	}
	public int getboardSize(){
		return this.boardSize;
	}
	public int getconToWin(){
		return this.conToWin;
	}

	public void printBoard() {
		System.out.println("inside checkWin() in GameBoard()");
		System.out.println("current player: " + lastClient);
		System.out.println("last row: " + lastRow);
		System.out.println("last col: " + lastCol);
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (grid[i][j] != -1) {
					System.out.print(" ");
				}
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private int countDir(int col, int row, int dx, int dy, int player) {
		int x = col + dx;
		int y = row + dy;
		int count = 0;
		while(x >= 0 && x < boardSize && y >= 0 && y < boardSize && grid[y][x] == player){
			count++;
			x += dx;
			y += dy;
		}
		return count;
	}

  public boolean checkWin() {
		printBoard();
		int[] dx = { 0, 1, 1, 1, 0,-1,-1,-1};
		int[] dy = { 1, 1, 0,-1,-1,-1, 0, 1};
		for(int dir = 0; dir < 4; dir++){
			int count = 1 + countDir(lastCol, lastRow, dx[dir], dy[dir], lastClient) +
											countDir(lastCol, lastRow, -dx[dir], -dy[dir], lastClient);
			if(count >= conToWin){
				return true;
			}
		}
		return false;
  }
}
