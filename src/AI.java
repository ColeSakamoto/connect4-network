
public class AI {
	private GameBoard base;
	private int depth;
	private final int[] dx = {-1, 0, 1, 1};
	private final int[] dy = { 1, 1, 1, 0};

	public AI(GameBoard gameBoard){
		base = new GameBoard(gameBoard);
		if(gameBoard.getSize()>17){
			depth = 1;
		} else if(gameBoard.getSize()>14){
			depth = 4;
		} else if(gameBoard.getSize()>=7){
			depth = 4;
		} else depth = 7;
	}

	public int bestMove(){
		int maxValue = -1000;
		int bestmove = 0;
		for(int i = 0; i < base.getSize(); i++){
			if(base.getHeight(i) >= base.getSize()) continue;
			GameBoard newBoard = new GameBoard(base);
			newBoard.addChip(i);
			System.out.println("inside bestMove:");
			int value = min(newBoard, 0);
			System.out.println("value = " + value);
			newBoard.printBoard();
			if(value == maxValue){
				if(Math.abs(newBoard.getSize()/2 - i) < Math.abs(newBoard.getSize()/2 - bestmove)){
					bestmove = i;
				}
			}
			if(value > maxValue){
				maxValue = value;
				bestmove = i;
			}
		}
		while(bestmove+1 < base.getSize() && base.getHeight(bestmove) == base.getSize()){
			bestmove++;
		}
		return bestmove;
	}

	public int min(GameBoard board, int d){
		if(d >= depth) return getValue(board);
		int minValue = 1000;
		for(int i = 0; i < base.getSize(); i++){
			if(board.getHeight(i) >= board.getSize()) continue;
			GameBoard newBoard = new GameBoard(board);
			newBoard.addChip(i);
			//System.out.println("inside min, d= " + d);
			//newBoard.printBoard();
			if(newBoard.getWinner() > 0){
				if(newBoard.getWinner() == base.getCurrentPlayer()) return 1000;
				else return -1000;
			}
			int value = max(newBoard, d + 1);
			if(value < minValue) minValue = value;
		}
		return minValue;
	}

	public int max(GameBoard board, int d){
		if(d >= depth) return getValue(board);
		int maxValue = -10000;
		for(int i = 0; i < base.getSize(); i++){
			if(board.getHeight(i) >= board.getSize()) continue;
			GameBoard newBoard = new GameBoard(board);
			newBoard.addChip(i);
			//System.out.println("inside max, d= " + d);
			//newBoard.printBoard();
			if(newBoard.getWinner() > 0){
				if(newBoard.getWinner() == base.getCurrentPlayer()) return 1000;
				else return -1000;
			}
			int value = min(newBoard, d + 1);
			if(value > maxValue) maxValue = value;
		}
		return maxValue;
	}

	public boolean inRange(int x, int y, int range){
		if(x >= 0 && x < range && y >=0 && y < range) return true;
		else return false;
	}

	public int getValue(GameBoard board){ //currentPlayer win:1000 currentPlayer lose:-1000
    int[][] data = board.getBoard().clone();
		int size = board.getSize();
		int score = 0;
		for(int j = 0; j < size; j++){
			boolean rowIsEmpty = true;
			for(int i = 0; i < size; i++){
				if(data[i][j]>0){
					rowIsEmpty = false;
					int color = data[i][j];
					for(int dir = 0; dir < 4; dir++){
						int count = 1;
						int x = i - dx[dir];
						int y = j - dy[dir];
						if(inRange(x,y,size) && data[x][y] != color){
							x = i + dx[dir];
							y = j + dy[dir];
							while(inRange(x,y,size) && data[x][y] == color){
								count++;
								if(count == base.getConToWin()){
									if(color == 1) score = -1000;
									else score = 1000;
									if(base.getCurrentPlayer() == 1) score *= -1;
									return score;
								}
								x += dx[dir];
								y += dy[dir];
							}
						}
						if(count == 3) score += (color == 1?-1:1) * 50;
						if(count == 2) score += (color == 1?-1:1) * 25;
						if(count == 1) score += (color == 1?-1:1) * 10;
						if(inRange(x,y,size) && data[x][y] == 0) score += (color == 1?-1:1) * 5 * count;
						x = i - dx[dir];
						y = j - dy[dir];
						if(inRange(x,y,size) && data[x][y] == 0) score += (color == 1?-1:1) * 5 * count;

					}
				}
			}
			if(base.getCurrentPlayer() == 1) score *= -1;
			if(rowIsEmpty) break;
		}
		return score;
	}
}
