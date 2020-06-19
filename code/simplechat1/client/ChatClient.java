// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String login_id;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   * @param login_id The login_id of the client
   */
  
  public ChatClient(String login_id, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.login_id = login_id;
    this.clientUI = clientUI;
    openConnection();
    sendToServer("#login " + login_id);
    clientUI.display(login_id + " has logged on.");
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.subSequence(0, 1).equals("#") ){
        commandsFromClientUI(message);
      }
      else{
        sendToServer(message);
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  //Commands to perform special functions
  public void commandsFromClientUI(String message){
    switch(message){

      case "#quit":
        clientUI.display("quiting...");
        quit();
        break;

      case "#logoff":
        try{
          clientUI.display("logging off...");
          closeConnection();
        }
        catch(IOException e) {}
        break;
      
      case "#login":
        if(!isConnected()){
          try{
            clientUI.display("logging in...");
            openConnection();
          }
          catch(IOException e) {
            clientUI.display("Cannot connect to the server...");}
        }
        else{
          clientUI.display("The connection is already connect");
        }
        break;
      
      case "#gethost":
        clientUI.display("The host number is " + getHost());
        break;

      case "#getport":
        clientUI.display("The port number is " + getPort());
        break;
 
      default:

        if(message.subSequence(0,8).equals("#setport")){
          if(!isConnected()){
            setPort(Integer.parseInt(message.substring(9)));
            clientUI.display("Port set to: " + message.substring(9));
          }
          else{
            clientUI.display("The client is not logged off! Cannnot set a new port number.");
          }
        }

        else if(message.subSequence(0,8).equals("#sethost")){
          if(!isConnected()){
            setHost(message.substring(9));
            clientUI.display("Host set to: " + message.substring(9));
          }
          else{
            clientUI.display("The client is not logged off! Cannnot set a new host number.");
          }
        }
        else{
          clientUI.display("Invaild command!");
        }
      }
    
  }

  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  /**---Override Hook Method---
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	protected void connectionClosed() {
    clientUI.display("The server is closed. Connection closed.");
	}

	/**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	protected void connectionException(Exception exception) {
    clientUI.display("Waiting for the server...");
    quit();
	}

	/**
	 * Hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
    clientUI.display("The connection is established");
	}

}
//End of ChatClient class
