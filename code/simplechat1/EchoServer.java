// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  ChatIF serverUI;
  private boolean closed = false;
  private boolean loginCommand = false;
  //Constructors ****************************************************
 
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) {
    super(port); //Call the superclass constructor
    this.serverUI = serverUI;
    
    try 
    {
      listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

  // Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg    The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {

    if(String.valueOf(msg).subSequence(0, 1).equals("#")){
      if(String.valueOf(msg).substring(7).equals(client.getInfo("login_id"))){
        try{
          sendToAllClients("The login command should only be the first command!");
          client.close();
        }catch(IOException e){}
      }
      else{
        System.out.println("A new client is attempting to connect to the server.");
        System.out.println("Message received " + msg + " from null.");
        System.out.println(msg);
        client.setInfo("login_id", msg);
      }
    }    
    else{
      System.out.println(client.getInfo("login_id") + "> Message received: " + msg + " from " + client);
      this.sendToAllClients(msg);
    }
  }

  public void handleMessageFromServerUI(String message) {
    try
    {
      if(message.subSequence(0, 1).equals("#") ){
        commandsFromServerUI(message);
      }
      else{
        serverUI.display(message);
        sendToAllClients("SERVER MSG> " + message);
      }
      
    }
    catch(Exception e)
    {
      serverUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  public void commandsFromServerUI(String message){
    switch(message){

      case "#quit":
        serverUI.display("quiting...");
        quit();
        break;

      case "#stop":
        stopListening();
        serverUI.display("stop listening...");
        break;
      
      case "#close":
        try{
          stopListening();
          close();
          serverUI.display("close...");
        }
        catch(IOException e){
        }
        break;

      case "#start":
        if(!isListening()){
          try{
            listen();
            serverUI.display("start listening...");
          }
          catch(IOException e){
            serverUI.display("Cannot start listening...");
          }
        }
        else{
          System.out.println("The server is not stopped...");
        }
        break;
        
      case "#getport":
        serverUI.display("The port number is " + getPort());
        break;
      
      default:

        if(message.subSequence(0,8).equals("#setport")){
          if(isClosed()){
            setPort(Integer.parseInt(message.substring(9)));
            serverUI.display("The port number is set");
          }
          else{
            serverUI.display("The server is not closed! Cannnot set a new port number.");
          }
        }
    }
  }
  /**
   * This method terminates the server.
   */
  public void quit() {
    try {
      close();
    } catch (IOException e) {
    }
    System.exit(0);
  }

  //return whether the server is closed
  protected boolean isClosed(){
    return closed;
  }
  /**
   * This method overrides the one in the superclass. Called when the server
   * starts listening for connections.
   */
  protected void serverStarted() {
    System.out.println("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass. Called when the server stops
   * listening for connections.
   */
  protected void serverStopped() {
    System.out.println("Server has stopped listening for connections.");
  }

  /*Override Hook method*/
  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("The client is connected");
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
      System.out.println("The client is disconnected");
    }

  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
      System.out.println("Error occurs in connection threads");
      clientDisconnected(client);
    }
  /**
   * Hook method called when the server is clased.
   * The default implementation does nothing. This method may be
   * overriden by subclasses. When the server is closed while still
   * listening, serverStopped() will also be called.
   */
  protected void serverClosed() {
    System.out.println("The server closed.");
    this.closed = true;
  }

}
//End of EchoServer class
