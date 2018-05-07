import javax.swing.JFrame;

public class Console {

	private static void printErrorMissingArg() {
		System.out.println("<>---------------------MISSING ARGUMENTS-----------------------<>");
		System.out.println("No arguments");
		System.out.println("Ex. java -jar Connect4.jar 6 4 is OK");
		System.out.println("Ex. java -jar Connect4.jar <BoardSize> <ConnetionsToWin>");
		System.out.println("<>-------------------------------------------------------------<>");
	}

	private static void printErrorMissingArg2() {
		System.out.println("<>---------------------MISSING ARGUMENTS-----------------------<>");
		System.out.println("These arguments will cause a problem...");
		System.out.println("");
		System.out.println("Ex. java -jar Connect4.jar 6 4 is OK");
		System.out.println("Ex. java -jar Connect4.jar 6   is INVALID");
		System.out.println("<>-------------------------------------------------------------<>");
	}

	private static void printErrorInvalidInput() {
		System.out.println("<>---------------------INVALID INPUT-----------------------<>");
		System.out.println("Game is not winnable!!!");
		System.out.println("");
		System.out.println(
				"Note: BoardSize cannot be zero and connections needed to win must be less than or equal to the BoardSize");
		System.out.println("Ex. BoardSize = 8, ConnectionToWin = 6, is OK");
		System.out.println("Ex. BoardSize = 8, ConnectionToWin = 9, is INVALID");
		System.out.println("Ex. BoardSize = 8, ConnectionToWin = 0, is INVALID");
		System.out.println("<>---------------------------------------------------------<>");
	}

	private static void printErrorInvalidInput2() {
		System.out.println("<>---------------------INVALID INPUT-----------------------<>");
		System.out.println("BoardSize must be less than or equal to 20 and greater than 2");
		System.out.println("");
		System.out.println("2 <= BoardSize <= 20");
		System.out.println("<>---------------------------------------------------------<>");
	}

	private static void printErrorInvalidInput3() {
		System.out.println("<>---------------------INVALID INPUT-----------------------<>");
		System.out.println("These arguments will cause a problem...");
		System.out.println("");
		System.out.println("Arguments must be a integer");
		System.out.println("Ex. java -jar Connect4.jar 6 4 is OK");
		System.out.println("Ex. java -jar Connect4.jar 6 e is INVALID");
		System.out.println("Ex. java -jar Connect4.jar 6 3.2 is INVALID");
		System.out.println("<>---------------------------------------------------------<>");
	}

	private static void printErrorCon2() {
		System.out.println("<>---------------------INVALID INPUT-----------------------<>");
		System.out.println("These arguments will cause a problem...");
		System.out.println("");
		System.out.println("Connections to win must be > 2");
		System.out.println("<>---------------------------------------------------------<>");
	}

	private static void invokeUI(int boardSize, int conToWin, String userName) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				new ConnectFourView(boardSize, conToWin, userName);
			}
		});
	}

	public static void start(String[] args) {
		if (args.length == 0) {
			printErrorMissingArg();
			return;
		}
		if (args.length < 2) {
			printErrorMissingArg2();
			return;
		}


		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true"); // Fixes "Comparison method violates its
																			// general contract!"
		int boardSize = 0;
		int conToWin = 0;
        String userName = "";
		try {
			boardSize = Integer.parseInt(args[0]); // boardSize
			conToWin = Integer.parseInt(args[1]); // Connections needed to win

		} catch (NumberFormatException e) {
			printErrorInvalidInput3();
		}
		try{
			 userName = args[2]; //Game will be played in online mode.  boardSize and conToWin will be determined by server


		}
		catch (ArrayIndexOutOfBoundsException e){
			System.out.println("No user name");
		}

		if (conToWin <= 2) {
			printErrorCon2();
			return;
		}

		if (boardSize < conToWin || conToWin <= 0) {
			printErrorInvalidInput();
			return;
		}
		if (boardSize > 20 || boardSize <= 2) {
			printErrorInvalidInput2();
			return;
		}

		System.out.println("BoardSize: " + boardSize);
		System.out.println("ConnectToWinSize: " + conToWin);
		System.out.println("User name: " + userName);
		int offset = 0; // Offset grid starts at (0,0) instead of (1,1)
		invokeUI(boardSize + offset, conToWin, userName);


	}
}
