package socket_server_test_read_write_test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerExample {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 5001));

			while (true) {
				System.out.println("[연결 기다림]");
				Socket socket = serverSocket.accept();
				InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
				System.out.println("[연결 수락함]" + isa.getHostName());

				byte[] bytes = null;
				String message = null;
				InputStream is = socket.getInputStream();
				bytes = new byte[100];
				int readByteCount = is.read(bytes);
				message = new String(bytes, 0, readByteCount, "utf-8");
				System.out.println("[데이터 받기 성공]: " + message);
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
