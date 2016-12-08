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
		List<byte[]> numOfPackets = new ArrayList<byte[]>();
		
		int lastPackets = 0;
		int totalPackets = 3;
		int count = 0;
		while (lastPackets != 3 || count != totalPackets) {
			count++;
			
			DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
			socket.receive(receivePacket);
			byte[] received = new byte[receivePacket.getLength()];
			
			for(int i = 0; i < received.length; i++){
				received[i] = receivePacket.getData()[i];
			}
			
			byte status = received[0];
			if (status % 2 == 0) {
				headers.add(received);
			} else if (status % 4 == 3) {
				int packetNum = getPacketNum(received[2], received[3]);
				totalPackets += (packetNum + 1);
				lastPackets++;
				data.add(received);
				numOfPackets.add(received);
			} else {
				data.add(received);
			}
		}
		
		byte file0ID = headers.get(0)[1];
		byte file1ID = headers.get(1)[1];
		byte file2ID = headers.get(2)[1];
		
		int file0Size = getListSize(numOfPackets, file0ID);
		int file1Size = getListSize(numOfPackets, file1ID);
		int file2Size = getListSize(numOfPackets, file2ID);
		
		List<byte[]> file0 = makeList(data, file0Size, file0ID);
		List<byte[]> file1 = makeList(data, file1Size, file1ID);
		List<byte[]> file2 = makeList(data, file2Size, file2ID);
		
		String fileName0 = getFileName(headers, 0);
		String fileName1 = getFileName(headers, 1);
		String fileName2 = getFileName(headers, 2);
		
		writeToDisk(file0, fileName0);
		writeToDisk(file1, fileName1);
		writeToDisk(file2, fileName2);

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
	
	public static void sortPackets(byte[] received, List<byte[]> headers, List<byte[]> data) {
		if (received[0] % 2 == 0) {
			headers.add(received);
		} else {
			data.add(received);
		}
	}
	
	public static int getListSize(List<byte[]> numOfPackets, byte fileID) {
		int numOfEachPacket = -1;
		for (int i = 0; i < numOfPackets.size(); i++) {
			byte[] arr = numOfPackets.get(i);
			if (arr[1] == fileID) {
				numOfEachPacket = getPacketNum(arr[2], arr[3]) + 1;
			}
		}
		return numOfEachPacket;
	}
	
	public static List<byte[]> makeList(List<byte[]> data, int fileSize, byte fileID) {
		List<byte[]> list = new ArrayList<byte[]>(fileSize);
		for (int i = 0; i < fileSize; i++) {
			list.add(null);
		}
		
		for (int i = 0; i < data.size(); i++) {
			byte dataFileID = data.get(i)[1];
			int packetNum = getPacketNum(data.get(i)[2], data.get(i)[3]);
			
			if (dataFileID == fileID) {
				list.set(packetNum, data.get(i));
			}
		}
		
		return list;
	}
	
	public static String getFileName(List<byte[]> headers, int fileNum) {
		String fileName = null;
		byte[] byteFileName = new byte[headers.get(fileNum).length - 2];
		
		for (int i = 0; i < byteFileName.length; i++) {
			byteFileName[i] = headers.get(fileNum)[i + 2];
		}
		
		fileName = new String(byteFileName);
		return fileName;
	}
	
	public static void writeToDisk(List<byte[]> list, String fileName) throws IOException {
		FileOutputStream file = new FileOutputStream(fileName);
		
		for (int i = 0; i < list.size(); i++) {
			for (int j = 4; j < list.get(i).length; j++) {
				file.write(list.get(i)[j]);
			}
		}
		file.close();
	}
}