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

		System.out.println("before creating socket");
		
		DatagramSocket socket = new DatagramSocket();
		byte[] buf = new byte[1028];
		InetAddress address = InetAddress.getByName(args[0]);
		DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 6014);
		socket.send(sendPacket);
		
		System.out.println("after sending packet");

		List<byte[]> headers = new ArrayList<byte[]>();
		List<byte[]> data = new ArrayList<byte[]>();
		List<byte[]> file0 = new ArrayList<byte[]>(1000);
		for (int i=0; i<1000; ++i) {
			file0.add(null);
		}
		List<byte[]> file1 = new ArrayList<byte[]>(1000);
		for (int i=0; i<1000; ++i) {
			file1.add(null);
		}
		List<byte[]> file2 = new ArrayList<byte[]>(1000);
		for (int i=0; i<1000; ++i) {
			file2.add(null);
		}
		
		int lastPackets = 0;
		int totalPackets = 3;
		int count = 0;
		while (lastPackets != 3 || count != totalPackets) {
			System.out.println("inside while loop");
			System.out.println("no of loops: " + count);
			count++;
			DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
			socket.receive(receivePacket);
			byte[] received = new byte[receivePacket.getLength()];
			
			for(int i = 0; i < received.length; i++){
				received[i] = receivePacket.getData()[i];
			}

			System.out.println("mod: " + (received[0] % 4));
			System.out.println("packet number " + getPacketNum(received[2], received[3]));
			if (received[0] % 4 == 3) {
				int packetNum = getPacketNum(received[2], received[3]);
				totalPackets += (packetNum + 1);
				lastPackets++;
			}

			if (received[0]%2 == 0) {
				headers.add(received);
				System.out.println("inside adding recieved to header");
				System.out.println("file ID: " + received[1]);
			} else {
				data.add(received);
				System.out.println("inside adding received to data");
				System.out.println("file ID: " + received[1]);
			}
			
		}

		System.out.println("After while loop");
		byte file0ID = headers.get(0)[1];
		byte file1ID = headers.get(1)[1];
		byte file2ID = headers.get(2)[1];

		for (int i = 0; i <data.size(); i++) {
			
			System.out.println("inside for loop");
			byte dataFileID = data.get(i)[1];
			
			int packetNum = getPacketNum(data.get(i)[2], data.get(i)[3]);
			System.out.print("packet No: " + packetNum);

			if (dataFileID == file0ID) {
				System.out.println("adding packet to file");
				file0.set(packetNum, data.get(i));
			}
			if (dataFileID == file1ID) {
				System.out.println("adding packet to file");
				file1.set(packetNum, data.get(i));
			}
			if (dataFileID == file2ID) {
				System.out.println("adding packet to file");
				file2.set(packetNum, data.get(i));
			}
		}

		byte[] byteFileName0 = new byte[headers.get(0).length - 2];
		byte[] byteFileName1 = new byte[headers.get(1).length - 2];
		byte[] byteFileName2 = new byte[headers.get(2).length - 2];

		for (int i = 0; i < byteFileName0.length; i++) {
			byteFileName0[i] = headers.get(0)[i + 2];
			System.out.println("Inside creating file names");
		}

		for (int i = 0; i < byteFileName1.length; i++) {
			byteFileName1[i] = headers.get(1)[i + 2];
			System.out.println("Inside creating file names");
		}

		for (int i = 0; i < byteFileName2.length; i++) {
			byteFileName2[i] = headers.get(2)[i + 2];
			System.out.println("Inside creating file names");
		}

		String fileName0 = new String(byteFileName0);
		String fileName1 = new String(byteFileName1);
		String fileName2 = new String(byteFileName2);

		FileOutputStream f1 = new FileOutputStream(fileName0);
		for (int i = 0; i < file0.size(); i++) {
			System.out.println("Inside output stream loop");
			for (int j = 4; j < file0.get(i).length; j++) {
				System.out.println("Writing to file");
				f1.write(file0.get(i)[j]);
			}
		}

		FileOutputStream f2 = new FileOutputStream(fileName1);
		for (int i = 0; i < file1.size(); i++) {
			System.out.println("Inside output stream loop 1");
			for (int j = 4; j < file1.get(i).length; j++) {
				System.out.println("Writing to file 1");
				f2.write(file1.get(i)[j]);
			}
		}

		FileOutputStream f3 = new FileOutputStream(fileName2);
		for (int i = 0; i < file2.size(); i++) {
			System.out.println("Inside output stream loop 2");
			for (int j = 4; j < file2.get(i).length; j++) {
				System.out.println("Writing to file 2");
				f3.write(file2.get(i)[j]);
			}
		}

		System.out.println("closing files and sockets");
		f1.close();
		f2.close();
		f3.close();
		socket.close();
	}
	
	
	public static int getPacketNum(byte b1, byte b2) {
		int packetNum;
		int newP1;
		int newP2;
		
		if(b1 < 0){
			newP1 = b1 + 256;
		} else {
			newP1 = b1;
		}
		if(b2 < 0){
			newP2 = b2 + 256;
		} else {
			newP2 = b2;
		}

		packetNum = 256 * newP1 + newP2;
		return packetNum;
	}

}
