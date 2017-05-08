package socket_server_test_chatting;

import java.awt.List;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ServerExample extends Application {
	ExecuteService executorService;
	ServerSocket serverSocket;
	List<Client> connextions = new Vector<Client>();

	void startServer() {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
		} catch (Exception e) {
			if (!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					displayText("[서버 시작]");
					btnStartStop.setText("stop");
				});
				
				while(true) {
					try {
						Socket socket = serverSocket.accept(); //클라이언트 연결 요청 까지 기다림
						String message = "[연결 수락: "+ socket.getRemoteSocketAddress() + ": "+ Thread.currentThread().getName()+"]";
						Platform.runLater(()-> {
							displayText(message);
						});
						
						Client client = new Client(socket);
						connetions.add(client);
						Platform.runLater(()-> {
							displayText("연결 개수: "+connetions.size+"]");
						});
					} catch(IOException e) {
						if(!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
					
				}
			}
		};
		
		
	}

	void stopServer() {

	}

	class Client {
		Client(Socket socket) {
			
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}
}