package segmentedfilesystem;
import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) throws IOException {

    if (args.length != 1) {
      System.out.println("You need to enter a hostname.");
      return;
    }

      DatagramSocket socket = new DatagramSocket();
      byte[] buf = new byte[256];
      InetAddress address = InetAddress.getByName(args[0]);
      DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 6014);
      socket.send(sendPacket);
      
      boolean keepReceiving = true;
      while (keepReceiving) {
    	  DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
    	  socket.receive(receivePacket);
    	  byte[] received = receivePacket.getData();
    	  
    	  if (received == null || received.length == 0) {
    		  keepReceiving = false;
    	  }
      }
    	  
      socket.close();
    }

}
