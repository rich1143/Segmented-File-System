package segmentedfilesystem;
import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

    if (args.length != 1) {
      System.out.println("You need to enter a hostname.");
      return;
    }

      DatagramSocket socket = new DatagramSocket();
      byte[] buf = new byte[1028];
      InetAddress address = InetAddress.getByName(args[0]);
      DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 6014);
      socket.send(sendPacket);
      
      List<byte[]> headers = new ArrayList<byte[]>();
      List<byte[]> data = new ArrayList<byte[]>();
      List<byte[]> file0 = new ArrayList<byte[]>();
      List<byte[]> file1 = new ArrayList<byte[]>();
      List<byte[]> file2 = new ArrayList<byte[]>();
      
      boolean keepReceiving = true;
      while (keepReceiving) {
    	  DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
    	  socket.receive(receivePacket);
    	  byte[] received = new byte[receivePacket.getLength()];
    	  
    	  for(int i = 0; i < received.length; i++){
    		  received[i] = receivePacket.getData()[i];
    	  }
    	  
    	  if (received[0]%2 == 0) {
    		  headers.add(received);
    	  } else {
    		  data.add(received);
    	  }
    	  
    	  if (received == null || received.length == 0) {
    		  keepReceiving = false;
    	  }
      }
      
      byte file0ID = headers.get(0)[1];
      byte file1ID = headers.get(1)[1];
      byte file2ID = headers.get(2)[1];
      
      for (int i = 0; i <data.size(); i++) {
    	  byte dataFileID = data.get(i)[1];
    	  
    	  int newP1;
    	  int newP2;
    	  if(data.get(i)[2] < 0){
    		  newP1 = data.get(i)[2] + 256;
    	  } else {
    		  newP1 = data.get(i)[2];
    	  }
    	  if(data.get(i)[3] < 0){
    		  newP2 = data.get(i)[3] + 256;
    	  } else {
    		  newP2 = data.get(i)[3];
    	  }
    	  
    	  int packetNum = 256 * newP1 + newP2;
    	  
    	  if (dataFileID == file0ID) {
    		  file0.add(packetNum, data.get(i));
    	  }
    	  if (dataFileID == file1ID) {
    		  file1.add(packetNum, data.get(i));
    	  }
    	  if (dataFileID == file2ID) {
    		  file2.add(packetNum, data.get(i));
    	  }
      }
      
      byte[] byteFileName0 = new byte[headers.get(0).length - 2];
      byte[] byteFileName1 = new byte[headers.get(1).length - 2];
      byte[] byteFileName2 = new byte[headers.get(2).length - 2];
      
      for (int i = 0; i < byteFileName0.length; i++) {
    	  byteFileName0[i] = headers.get(0)[i + 2];
      }
      
      for (int i = 0; i < byteFileName1.length; i++) {
    	  byteFileName1[i] = headers.get(1)[i + 2];
      }
      
      for (int i = 0; i < byteFileName2.length; i++) {
    	  byteFileName2[i] = headers.get(2)[i + 2];
      }
      
      String fileName0 = new String(byteFileName0);
      String fileName1 = new String(byteFileName1);
      String fileName2 = new String(byteFileName2);
      
      FileOutputStream f1 = new FileOutputStream(fileName0);
      for (int i = 0; i < file0.size(); i++) {
    	  for (int j = 4; j < file0.get(i).length; j++) {
    		  f1.write(file0.get(i)[j]);
    	  }
      }
      
      FileOutputStream f2 = new FileOutputStream(fileName1);
      for (int i = 0; i < file1.size(); i++) {
    	  for (int j = 4; j < file1.get(i).length; j++) {
    		  f2.write(file1.get(i)[j]);
    	  }
      }
      
      FileOutputStream f3 = new FileOutputStream(fileName2);
      for (int i = 0; i < file2.size(); i++) {
    	  for (int j = 4; j < file2.get(i).length; j++) {
    		  f3.write(file2.get(i)[j]);
    	  }
      }
      
      f1.close();
      f2.close();
      f3.close();
      socket.close();
    }
}
