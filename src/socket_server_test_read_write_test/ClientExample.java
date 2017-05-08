package socket_server_test_read_write_test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientExample {

	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket();
			System.out.println("[연결 요청]");
			socket.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[연결 성공]");

			byte[] bytes = null;
			String message = null;

			OutputStream os = socket.getOutputStream();
			message = "Hello Server";
			bytes = message.getBytes("utf-8");
			os.write(bytes);
			os.flush();
			System.out.println("[데이터 보내기 성공]");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
