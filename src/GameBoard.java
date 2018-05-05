
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
		this.grid = new int[boardSize][boardSize];
		initGrid();
	}

	public void initGrid(){
		// -1 = restricted spot
				// 0 = empty spot
				// 1 = for player1 RED
				// 2 = for player2 BLUE
				int offset = 2; // To disable all rows except for the very bottom on initial start
				int offsetCol = 1; // to make the column size=row size
				for (int x = boardSize - offset; x >= 0; x--) {
					for (int y = boardSize - offsetCol; y >= 0; y--) {
						grid[x][y] = -1;
						//System.out.println("Setting up restricted spot at " + x + " " + y);
					}
				}
	}

	public void setLastMove(int row, int col){
		this.lastRow = row;
		this.lastCol = col;
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

    public int getInfo(int row, int col){
    	return grid[row][col];
    }
    public void setGrid(int row, int col, int info){
    	grid[row][col] = info;
    }



    public boolean checkWin() {
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
			int[] dx = { 0, 1, 1, 1, 0,-1,-1,-1};
			int[] dy = { 1, 1, 0,-1,-1,-1, 0, 1};
			int player = lastClient;
			for(int dir = 0; dir < 8; dir++){
				int x = lastCol + dx[dir];
				int y = lastRow + dy[dir];
				int count = 1;
				if(count >= conToWin){
					return true;
				}
				while(x >= 0 && x < boardSize && y >= 0 && y < boardSize && grid[y][x] == player){
					count++;
					x += dx[dir];
					y += dy[dir];
					if(count >= conToWin){
						return true;
					}
				}
			}
			return false;
    }
}
