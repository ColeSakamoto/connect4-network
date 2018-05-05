
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

		//Vertical check
		int matchsFound = 1, i = 1; 
		while (i < conToWin && this.lastRow - i >= 0) {
			if (grid[this.lastRow][this.lastCol] != grid[this.lastRow - i][this.lastCol])
				break;
			if(grid[this.lastRow][this.lastCol] == lastClient){ // only total up 1 or 2
			matchsFound++;
			i++;
			}
		}
		i = 1;
		while (i < conToWin && this.lastRow + i < boardSize - 1) {
			if (grid[this.lastRow][this.lastCol] != grid[this.lastRow + i][this.lastCol])
				break;
			if(grid[this.lastRow][this.lastCol] == lastClient){
			matchsFound++;
			i++;
			}
		}
	
		if (matchsFound >= conToWin){System.out.println("Vertical win found");return true;}
				//////Horizontal check
				matchsFound = 1; i = 1;
				while (i < conToWin && this.lastCol - i >= 0) {
					if (grid[this.lastRow][this.lastCol] != grid[this.lastRow][this.lastCol - i])
						break;
					if(grid[this.lastRow][this.lastCol] == lastClient){
					matchsFound++;
					i++;
					
					
					}
				}
				i = 1;
				while (i < conToWin && this.lastCol + i < boardSize - 1) {
					if (grid[this.lastRow][this.lastCol] != grid[this.lastRow][this.lastCol + i])
						break;
					if(grid[this.lastRow][this.lastCol] == lastClient){
					matchsFound++;
					i++;
					}
				}
		if (matchsFound >= conToWin){System.out.println("Horizontal win found");return true;}
		
		//Positive diagonal
		matchsFound = 2; i = 1; //matchsFound = 1 changed to 2 (java Server 5 4) but still needed 5 to win
		while (i < conToWin && this.lastRow - i >= 0 && this.lastCol + i < boardSize - 1) {
			if (grid[this.lastRow][this.lastCol] != grid[this.lastRow - i][this.lastCol + i])
				break;
			if(grid[this.lastRow][this.lastCol] == lastClient){
			matchsFound++;
			i++;
			}
		}
		i = 1;
		while (i < conToWin && this.lastRow + i < boardSize - 1 && this.lastCol - i >= 0) {
			if (grid[this.lastRow][this.lastCol] != grid[this.lastRow + i][this.lastCol - i])
				break;
			if(grid[this.lastRow][this.lastCol] == lastClient){
			matchsFound++;
			i++;
			}
		}
		if (matchsFound >= conToWin){System.out.println("Positive diagonal win found");return true;}
		
		//Negative
		matchsFound = 2; i = 1; //matchsFound = 1 changed to 2 (java Server 5 4) but still needed 5 to win
		while (i < conToWin && this.lastRow - i >= 0 && this.lastCol - i >= 0) {
			if (grid[this.lastRow][this.lastCol] != grid[this.lastRow - i][this.lastCol - i])
				break;
			if(grid[this.lastRow][this.lastCol] == lastClient){
			matchsFound++;
			i++;
			}
		}
		i = 1;
		
		while (i < conToWin && this.lastRow + i < boardSize - 1 && this.lastCol + i < boardSize - 1) {
			if (grid[this.lastRow][this.lastCol] != grid[this.lastRow + i][this.lastCol + i])
				break;
			if(grid[this.lastRow][this.lastCol] == lastClient){
			matchsFound++;
			i++;
			}
		}
		if (matchsFound >= conToWin){System.out.println("Negative diagonal win found");return true;}
		return false;
    }
}
