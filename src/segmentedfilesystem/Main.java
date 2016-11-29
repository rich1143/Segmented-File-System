package segmentedfilesystem;
import java.io.*;

public class Main {

    public static void main(String[] args) {

      DatagramSocket socket = new DatagramSocket();
      byte[] buf = new byte[256];
      InetAddress address = InetAddress.getByName("sputnik");
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6014);
      socket.send(packet);

    }

}
