import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.io.*;
public class Server {
 public static void main(String[] args) throws Exception {
   try{
     ServerSocket server=new ServerSocket(8888);
     int counter=0;
     System.out.println("Server waiting for clients");
     while(true){
       counter++;
       int clientID = generateID();
      
       Socket serverClient=server.accept();  //server accept the client connection request
       System.out.println(" >> " + "Client No:" + counter +" with clientID: "+clientID+" connected");
       ServerClientThread sct = new ServerClientThread(serverClient,counter, clientID); //send  the request to a separate thread
       sct.start();
     }
   }catch(Exception e){
	   System.out.println("Server socket");
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

 ServerClientThread(Socket inSocket,int counter, int inclientID){
   serverClient = inSocket;
   clientNo=counter;
   clientID = inclientID;
 }
 public void run(){
   try{
     DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
     DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
     String clientMessage="", serverMessage="";
     while(!clientMessage.equals("bye")){
       clientMessage=inStream.readUTF();
       clientMessage = clientMessage+", "+clientID;
       System.out.println("From Client-" +clientNo+ ": Data is(row, col, clientID):"+clientMessage);
       
       serverMessage="From Server to Client:" + clientMessage;
       outStream.writeUTF(serverMessage);
       outStream.flush();
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


