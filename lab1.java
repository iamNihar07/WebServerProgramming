//Nihar Gupte ID 1001556441


import java.io.*;
import java.net.*; //networking package
import java.util.*;

public class lab1 implements Runnable //Runnable is an interace to implement
                                     //multi threading 
{
    static ServerSocket listeningSocket; //Welcoming Socket declaration
    
    //driver method
    public static void main(String args[]) throws IOException
    {       
        int port_number = 6789; //predefined listening port as 6789 for this assignment
        listeningSocket = new ServerSocket(port_number); //establishing the listening TCP socket
        lab1 ob = new lab1(); //instantiation
        ob.run(); //invoking a new thread using functionality of Runnable interface
    }

    //method included in interface runnable which implements multi threading
    public void run() 
    {   
        boolean flag=false;
        //catch exceptions thrown by TCP connection sockets
        try  
        {
            while(true) // Process HTTP service requests in an infinite loop.
            {
                flag=false;
                //listening for a TCP connection
                //System.out.println("1");
                Socket connectionSocket = listeningSocket.accept(); //establishing the TCP connection between requesting client and server
                
                //creating input stream attached to socket
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                //System.out.println("1");
                //creating output stream attached to socket
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                //System.out.println("1");

                //Sample HTTP GET Request: GET /filename.html
                String clientSentence = inFromClient.readLine();   
                //System.out.println("1");
                System.out.println(clientSentence);
                
                
                //String capitalizedSentence = clientSentence.toUpperCase() + 'n';
                //outToClient.writeBytes(capitalizedSentence);
                
                StringTokenizer obj = new StringTokenizer(clientSentence); //tokenizing the HTTP request in order to parse it
		  
                String filename = obj.nextToken(); //first token will store the method i.e GET
                filename = obj.nextToken(); //second token stores the name of the web page along with a backslash
                filename = filename.substring(1); //parsing the name of webpage from a HTTP request                
                //System.out.println(filename); //printing filename for debugging purposes
                
                
                
                if(filename.equalsIgnoreCase("index.html") || 
                    filename.equalsIgnoreCase("index.html/") || 
                    filename.equalsIgnoreCase("CN.jpg") || 
                    filename.equalsIgnoreCase("CN.jpg/"))  // i.e requested page is the page we have, then it is status code 200
                {
                    
                    String contentType="";
                    if(filename.equalsIgnoreCase("index.html") || 
                        filename.equalsIgnoreCase("index.html/")) //if it is a textual html file, set content type
                    {
                        contentType="text/html";
                    }
                    else
                    {
                        contentType="image/jpeg"; //if it is an image, set content type
                    }
                    //composing the HTTP Response by Server to the Client
                    /* Format for HTTP Success Response Code
                    HTTP/1.0 200 OK\r\n
                    Content-Type: text/html\r\n
                    \r\n
                    (contents of filename.html)
                    */
                    outToClient.writeBytes("HTTP/1.0 200 OK\r\n"); //http version & status as per RFC
                    outToClient.writeBytes("Content-Type: "+contentType+" \r\n"); //type of data supplied to server to be returned to client
                    outToClient.writeBytes("Connection: close\r\n"); // Closing the stream
                    outToClient.writeBytes("\r\n"); //end of headers
                    
                    //Enabling the client to read the HTTP response message                   
                    FileInputStream temp = new FileInputStream(filename); 
                    
                    byte[] httpresponse = new byte[4096];
                    int i;
                    
                    while((i=temp.read(httpresponse))>0)
                    {
                        outToClient.write(httpresponse, 0, i); //display it on the webpage accordingly
                    }

                    temp.close(); //close the temporary file
                    
                
                 
                }
                else if(filename.equalsIgnoreCase("index.org") || 
                    filename.equalsIgnoreCase("index.php") || 
                    filename.equalsIgnoreCase("index.net")) //moved permanently is because of changes on the server side
                {
                        outToClient.writeBytes("HTTP/1.1 301 Moved Permanently\r\n"); //http version & response code 
			            outToClient.writeBytes("Location: http://127.0.0.1:6789/index.html \r\n"); //redirection location
                        outToClient.writeBytes("\r\n"); //end of headers
                } 
                else if(!(filename.equalsIgnoreCase("index.html"))&&
                        !(filename.equalsIgnoreCase("index.html/"))&&
                        !(filename.equalsIgnoreCase("CN.jpg"))&&
                        !(filename.equalsIgnoreCase("CN.jpg/"))) // Handling Status Code 404, i.e if file requested does not match the given html file
                {
                    //modifying our html page so it displays a 404 error instead of hardcoded success code
                    String errorHTMLcode="<!DOCTYPE html><html><head><title>CSE 4344 Lab1 Assignment</title></head><body><h2> HTTP Response Code 404 - File Not Found </h2></body></html>\n";
                    outToClient.writeBytes("HTTP/1.0 404 Not Found \r\n"); //http version & response code
                    outToClient.writeBytes("Content-Type: text/html \r\n"); //data type to return      
                    outToClient.writeBytes("\r\n"); //end of headers              
                    outToClient.writeBytes(errorHTMLcode);
                }
                
                
                
                //close the connections and streams
                outToClient.close(); 
                inFromClient.close(); 
                connectionSocket.close(); 
            
            }
        }
        catch(Exception err)
        {
            System.err.println(err);
        }
    }
}