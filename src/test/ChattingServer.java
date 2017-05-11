package test;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChattingServer extends Application {
	// field
	ExecutorService executorService;
	ServerSocket serverSocket;
	List<Client> connections = new Vector<Client>();
	
	// method
	void startServer() {
		executorService = Executors.newFixedThreadPool(10);// ������ 10�� �Ҵ�

		// executorService =
		// Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		// ������Ǯ ��ü ���� newFixedThreadPool - �ְ��� ������ ���� �ھ��� ����ŭ

		try {
			// �������� ���� �� ���ε� �ڵ�
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 5001));

			// ���ܰ� �߻��ϸ� ���������� �����ϰ� �ݾ��ش�
		} catch (Exception e) {
			if (!serverSocket.isClosed()) {
				stopServer();
			}
			return; // startServer() ����
		}
		
		//��������ڵ� runnable-�۾���ü
		Runnable runnable = new Runnable() { // ���� ���� �۾� ��ü
			@Override
			public void run() { // ���� ������ ���� run()�� ������ ���ش�
				//
//				Socket socket = null;
				byte getNickname[] = null;
				String nickname = null;
				
				
				//
				Platform.runLater(() -> { // Platform.runLater()�� ���� ��û �ڵ�
											// �����û (���ٽ�)
					displayText("[���� ����]");		// javafx �ʵ�κп� text ���

					btnStartStop.setText("stop"); // start -> stop ���� ����
				});

				while (true) {	//���� �۾� accept
					try {
						
						Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ��û ����
																// ���
						String message = "[���� ����: " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName() + "]"; // �����ּҿ�
																			// ���罺����
																			// ���
						
						Platform.runLater(() -> displayText(message));
						
						InputStream is = socket.getInputStream();
						
						getNickname = new byte[20];
						is.read(getNickname);
						nickname = new String(getNickname).trim();
						
						System.out.println(nickname + "�� ����");
					
						//Client client = new Client(socket, nickname);
						connections.add(new Client(socket, nickname)); // client�� connections�� �߰�
						
						//notifyConnector = nickname + "[���� �ο� �� : " + connections.size() +"]";
						//Platform.runLater(() -> displayText(notifyConnector));
						
						Platform.runLater(() -> displayText("[���� �ο� ��: " + connections.size() + "]")); // ����
																										// Ŭ���̾�Ʈ
																										// ����

					} catch (IOException e) { // accept() ���� �߻���
						if (!serverSocket.isClosed()) {
							stopServer();
						}
						break; // while()�� ����
					}
				}
			}
		};
		executorService.submit(runnable); // ������Ǯ���� ó��

	}// startServer() end

	void stopServer() {

		try {
			Iterator<Client> iterator = connections.iterator(); // �ݺ��� ȣ��
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}

			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}

			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}

			Platform.runLater(() -> { // �ڹ�FX UI ����
				displayText("[���� ����]");
				btnStartStop.setText("start"); // stop -> start ��ư ����
			});
		} catch (Exception e) {
		}
	}

	class Client { // ������ ����ڵ�
		Socket socket;
		String nickname = null;

		Client(Socket socket, String nickname) { // ������
			this.socket = socket;
			this.nickname = nickname;

			receive();
		}

		void receive() {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						while (true) { // ��� Ŭ���̾�Ʈ�� �����͸� �޾ƾ��ϹǷ� ���ѷ����� ����
							byte[] byteArr = new byte[100]; // ����Ʈ �迭 ����
							InputStream inputStream = socket.getInputStream();
							// Ŭ���̾�Ʈ�� �� ������ �ޱ�

							int readByteCount = inputStream.read(byteArr);
							if (readByteCount == -1) { // Ŭ���̾�Ʈ�� ���� ���� �ߴٸ�
								throw new IOException(); // ���������� IOException �߻�
							}

							String message = "[��û ó��:" + socket.getRemoteSocketAddress() + ": "
									+ Thread.currentThread().getName() + "]";
							Platform.runLater(() -> displayText(message));

							String data = new String(byteArr, 0, readByteCount, "utf-8");
							//���ڿ� ��ȯ, byteArr�� 0 �ε������� ���� ����Ʈ �� ��ŭ ���ڿ��� ��ȯ 
							data = Client.this.nickname + " : " + data;
							for (Client client : connections) {
								client.send(data);

							}
						}

					} catch (Exception e) {
						try {
							connections.remove(Client.this);
							String message = "[" + Client.this.nickname + "�Բ��� �����̽��ϴ�. : " + socket.getRemoteSocketAddress() + " : "
									+ Thread.currentThread().getName() + "]";
							Platform.runLater(() -> displayText(message));
							socket.close();
						} catch (IOException e1) {
						}

					}
				}
			};
			executorService.submit(runnable);

		}

		void send(String data) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						byte[] byteArr = data.getBytes("utf-8");
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(byteArr);
						outputStream.flush();
					} catch (Exception e) {
						String message = "[Ŭ���̾�Ʈ ��� �ȵ�: " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName() + "]";
						Platform.runLater(() -> displayText(message));
						connections.remove(Client.this);
						try {
							socket.close();
						} catch (IOException e2) {
						}
					}
				}
			};
			executorService.submit(runnable);
		}
	}

	// UI �����ڵ�

	TextArea txtDisplay;
	Button btnStartStop;

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);

		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0, 0, 2, 0));
		root.setCenter(txtDisplay);

		btnStartStop = new Button("start");
		btnStartStop.setPrefHeight(30);
		btnStartStop.setMaxWidth(Double.MAX_VALUE);
		btnStartStop.setOnAction(e -> {
			if (btnStartStop.getText().equals("start")) {
				startServer();
			} else if (btnStartStop.getText().equals("stop")) {
				stopServer();
			}
		});

		root.setBottom(btnStartStop);

		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.setOnCloseRequest(event -> stopServer());
		primaryStage.show();
	}

	void displayText(String text) { // ���÷��̿� ���ڿ� ���
		txtDisplay.appendText(text + "\n");
	}

	public static void main(String[] args) {
		launch(args);
	}

}