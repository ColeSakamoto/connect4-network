import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.io.*;
public class Server {
	private static int boardSize;
	private static int conToWin;
	
	public static void main(String[] args) throws Exception {
			
   try{
	   boardSize = Integer.parseInt(args[0]); // argument to declare board size of the game
	   conToWin = Integer.parseInt(args[1]); // argument to declare connections to win for the game
     ServerSocket server=new ServerSocket(8888);
     int counter=0;
     System.out.println("Server waiting for clients");
     while(true){
       counter++;
       int clientID = generateID();
      
       Socket serverClient=server.accept();  //server accept the client connection request
       System.out.println(" >> " + "Client No:" + counter +" with clientID: "+clientID+" connected");
       ServerClientThread sct = new ServerClientThread(serverClient,counter, clientID, boardSize, conToWin); //send  the request to a separate thread
       sct.start();
     }
   } catch (ArrayIndexOutOfBoundsException arr){
	   System.out.println("Missing argumnents exception");
	   System.out.println("Ex. java Server boardSize conToWin");
   }
   
   catch(Exception e){
	   System.out.println("Server socket exception");
     System.out.println(e);
   }
 }
 
 //Generate random clientID
 public static int generateID() {
	 
	 int randomNum = 1 + (int)(Math.random() * ((100000 - 1) + 1));
	 return randomNum;
 }
}




class ServerClientThread extends Thread {
 Socket serverClient;
 int clientNo;
 int clientID;
 int boardSize;
 int conToWin;

 ServerClientThread(Socket inSocket,int counter, int inclientID, int boardSize, int conToWin){
   serverClient = inSocket;
   clientNo=counter;
   clientID = inclientID;
   this.boardSize = boardSize;
   this.conToWin = conToWin;
 }
 public void run(){
   try{
     DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
     DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
     String clientMessage="", serverMessage="";
     while(!clientMessage.equals("bye")){
       clientMessage=inStream.readUTF();//Wait for client message
       clientMessage = clientMessage+", "+clientID;
       System.out.println("From Client-" +clientNo+ ": "+clientMessage);
       /////Do something
       //determine which player goes first
       //check the move of the player
       
       if (clientMessage.contains("start")){ //Clients requests boardSize and conToWin arguments
    	   serverMessage= Integer.toString(boardSize)+","+Integer.toString(conToWin);
           outStream.writeUTF(serverMessage);
           outStream.flush();
       }
       
       
       
       //////
       else{
       serverMessage="From Server to Client: " + clientMessage;
       outStream.writeUTF(serverMessage);
       outStream.flush();
       }
     }
     inStream.close();
     outStream.close();
     serverClient.close();
   } catch(EOFException eof){
	   
   }
   catch(Exception ex){
     System.out.println(ex);
   }finally{
     System.out.println("Client -" + clientNo + " disconnected ");
   }
 }
}

