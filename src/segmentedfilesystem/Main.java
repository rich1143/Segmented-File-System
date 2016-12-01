package segmentedfilesystem;
import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) {

    if (args.length != 1) {
      System.out.println("Usage: java QuoteClient <hostname>");
      return;
    }

      DatagramSocket socket = new DatagramSocket();
      byte[] buf = new byte[256];
      InetAddress address = InetAddress.getByName(args[0]);
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6014);
      socket.send(packet);

    }

}
