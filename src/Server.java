import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.io.*;

public class Server {
	private static int boardSize;
	private static int conToWin;
	private static GameBoard board;

	public static void main(String[] args) throws Exception {

		try {
			boardSize = Integer.parseInt(args[0]); // argument to declare board size of the game
			conToWin = Integer.parseInt(args[1]); // argument to declare connections to win for the game
			if (conToWin <= 2) {
				System.out.println("Connections to win argument must be > 2");
				System.exit(0);
			}
			else if (conToWin > boardSize) {
				System.out.println("Connections to win argument must be <= board size");
				System.exit(0);
			}
			else if (20 < boardSize) {
				System.out.println("Board size must be <= 20");
				System.exit(0);
			}
			board = new GameBoard(boardSize, conToWin);
			ServerSocket server = new ServerSocket(8888);
			int counter = 0;
			int id = 0;
			System.out.println("Server waiting for clients");
			while (true) {
				counter++;
				if (counter == 2) {
					id = 11;
				} //Flag for worker connection
				else if (counter == 3) {
					id = 2;
				} else if (counter == 4) {
					id = 22;
				} else if (counter > 4) {
					id = 1; 
					counter = 1;
					board = new GameBoard(boardSize, conToWin);
					System.out.println("Gameboard reseted");
				} //Flag for worker connection
				else {
					id = 1;
				}
				int clientID = generateID();

				Socket serverClient = server.accept(); //server accept the client connection request
				System.out.println(" >> " + "Client No:" + id + " with clientID: " + clientID + " connected");

				ServerClientThread sct = new ServerClientThread(serverClient, id, clientID, board); //send  the request to a separate thread
				sct.start();
				//if (runThread(serverClient, id, clientID, board) == -1) {
				//	counter--;
				//}
			}
		} catch (ArrayIndexOutOfBoundsException arr) {
			System.out.println("Missing argumnents exception");
			System.out.println("Ex. java Server boardSize conToWin");
		} catch (Exception e) {
			System.out.println("Server socket exception");
			System.out.println(e);
		}
	}
/*
	public static int runThread(Socket serverClient, int id, int clientID, GameBoard board) {
		try {
			ServerClientThread sct = new ServerClientThread(serverClient, id, clientID, board); //send  the request to a separate thread
			sct.start();
		} catch (SocketException sc) {
			return -1;
		}
		return 0;
	}
*/
	//Generate random clientID
	public static int generateID() {

		int randomNum = 1 + (int) (Math.random() * ((100000 - 1) + 1));
		return randomNum;
	}
}

class ServerClientThread extends Thread {
	Socket serverClient;
	int clientNo;
	int clientID;
	int boardSize;
	int conToWin;
	GameBoard board;

	ServerClientThread(Socket inSocket, int counter, int inclientID, GameBoard board) {
		serverClient = inSocket;
		clientNo = counter;
		clientID = inclientID;
		this.boardSize = board.boardSize;
		this.conToWin = board.conToWin;
		this.board = board;
	}

	public void run() {
		try {
			DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
			DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
			String clientMessage = "", serverMessage = "";
			String[] val;

			while (!clientMessage.equals("bye")) {
				val = clientMessage.split(",");
				clientMessage = inStream.readUTF();//Wait for client message
				clientMessage = clientMessage + "," + clientID;

				//System.out.println("From Client-" +clientNo+ ": "+clientMessage);

				if (clientMessage.contains("start")) { //Clients requests boardSize and conToWin arguments
					//System.out.println("From Client-" +clientNo+ ": "+clientMessage);
					serverMessage = Integer.toString(boardSize) + "," + Integer.toString(conToWin) + "," + clientNo;
					outStream.writeUTF(serverMessage);
					outStream.flush();
				} else if (clientMessage.contains("lastMove")) { //Clients requests boardSize and conToWin arguments
					String lastMessage = Integer.toString(board.lastClient) + "," + Integer.toString(board.lastCol) + ","
							+ Integer.toString(board.lastRow);
					//System.out.println("Server message to Client-" +clientNo+": "+serverMessage);
					//System.out.println();
					outStream.writeUTF(lastMessage);
					outStream.flush();
				} else if (clientMessage.contains("turn") && clientNo == board.turn) {
					val = clientMessage.split(",");

					if (board.get(Integer.parseInt(val[1]), Integer.parseInt(val[2])) != 0) {
						outStream.writeUTF("restrict");
						outStream.flush();

					} else {
						if (clientNo == 1) {
							board.turn = 2;
						} // For player turn control
						else if (clientNo == 2) {
							board.turn = 1;
						}

						System.out.println("From Client-" + clientNo + ": Recording move: " + clientMessage);

						if (board.get(Integer.parseInt(val[1]), Integer.parseInt(val[2])) == 0) { //only mark an open spot
							board.lastRow = Integer.parseInt(val[1]);
							board.lastCol = Integer.parseInt(val[2]);
							board.lastClient = clientNo;
							System.out.println("server setting clientNO");
							board.set(board.lastRow, board.lastCol, clientNo); //set position of grid to player number
							if (board.lastRow > 0) {
								board.set(board.lastRow - 1, board.lastCol, 0); //set spot above to open
							}
							outStream.writeUTF("good1");
							outStream.flush();
						} else if (board.get(Integer.parseInt(val[1]), Integer.parseInt(val[2])) == -1) {
							outStream.writeUTF("restrict");
							outStream.flush();
						} else {
							System.out.println("BoardInfo: " + board.get(Integer.parseInt(val[1]), Integer.parseInt(val[2])));
							outStream.writeUTF("Taken");
							outStream.flush();
						}
					}
				} else if (clientMessage.contains("checkWin")) {
					System.out.println("server checking win");
					outStream.writeUTF(Boolean.toString(board.checkWin()) + "," + board.lastClient);
					outStream.flush();
				} else if (clientMessage.contains("exit")) {
					break;
				} else if (clientMessage.contains("reset")) {
					System.out.println("reset");
					outStream.writeUTF("reset");
					outStream.flush();
				}

				//////
				else {
					System.out.println("bad from client: " + clientNo + ":" + clientMessage);
					outStream.writeUTF("bad");
					outStream.flush();
				}
			}
			inStream.close();
			outStream.close();
			serverClient.close();
		} catch (SocketException sc) {
			System.out.println("Socket Exception");
		}

		catch (EOFException eof) {

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Client No:" + clientNo + " disconnected ");
		}
	}
}
