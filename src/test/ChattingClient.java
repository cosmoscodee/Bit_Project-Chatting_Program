package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChattingClient extends Application {
	Socket socket;

	void startClient() {
		Thread thread = new Thread() { // ������ ������

			@Override
			public void run() {
				try {
					// ���� ���� �� ����
					socket = new Socket();
					socket.connect(new InetSocketAddress("localhost", 5001));
					Platform.runLater(() -> {
						try {
							socket.getOutputStream().write(txtInput.getText().getBytes());
							socket.getOutputStream().flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						displayText("���� �Ϸ�: " + socket.getRemoteSocketAddress() + "]");
						displayText("[��ȭ�濡 �����Ͽ����ϴ�.]");
						btnConn.setText("������");
						btnSend.setDisable(false); // ������ ��ư Ȱ��ȭ
					});

				} catch (IOException e) {
					Platform.runLater(() -> displayText("[���� ��� �ȵ�"));
					if (!socket.isClosed()) {
						stopClient();
					}
					return;
				}
				receive(); // �������� ���� ������ �ޱ�
			}
		};
		thread.start(); // ������ ����
	}

	void stopClient() {
		try {

			Platform.runLater(() -> {
				displayText("[��ȭ�濡�� �������ϴ�.]");
				btnConn.setText("�� ����");
				btnSend.setDisable(true);
			});
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
		}
	}

	void receive() {
		// ������ �ޱ� �ڵ�
		while (true) {

			try {
				byte[] byteArr = new byte[100];
				InputStream inputStream = socket.getInputStream();

				int readByteCount = inputStream.read(byteArr); // ������ �ޱ�
				if (readByteCount == -1) { // -1�� �����ϸ� ������ ����������
											// ����
					throw new IOException();
				}

				// ���������� �����͸� ���� ���
				String data = new String(byteArr, 0, readByteCount, "utf-8"); // ���ڿ�
																				// ��ȯ

				Platform.runLater(() -> displayText(data));
			} catch (Exception e) {
				Platform.runLater(() -> displayText("[���� ��� �ȵ�]"));
				stopClient();
				break;

			}
		}
	}

	void send(String data) {
		Thread thread = new Thread() { // ������ ������
			@Override
			public void run() {
				try {
					byte[] byteArr = data.getBytes("utf-8");
					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(byteArr);
					outputStream.flush();
					// Platform.runLater(() -> displayText("[������ �Ϸ�]"));
				} catch (Exception e) {
					Platform.runLater(() -> displayText("[���� ��� �ȵ�]"));
					stopClient();
				}
			}
		};
		thread.start();
	}

	// UI �����ڵ�

	TextArea txtDisplay;
	TextField txtInput;
	Button btnConn;
	Button btnSend;

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);

		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0, 0, 2, 0));
		root.setCenter(txtDisplay);

		BorderPane bottom = new BorderPane();
		txtInput = new TextField();
		txtInput.setPrefSize(60, 30);
		BorderPane.setMargin(txtInput, new Insets(0, 1, 1, 1));

		btnConn = new Button("�� ����");
		btnConn.setPrefSize(60, 30);

		btnConn.setOnAction(e -> {
			if (btnConn.getText().equals("�� ����")) {
				Platform.runLater(() -> {
					if (txtInput.getText().equals("")) {
						displayText("[ ä�ÿ��� ����� �г����� ���� �Է����ּ���. ]");
					} else {
						startClient();
					}
				});
			} else if (btnConn.getText().equals("������")) {
				stopClient();
			}
		});

		btnSend = new Button("������");
		btnSend.setPrefSize(60, 30);
		btnSend.setDisable(true);
		btnSend.setOnAction(e -> {
			Platform.runLater(() -> {
				send(txtInput.getText());
				txtInput.setText("");
			});
		});

		bottom.setCenter(txtInput);
		bottom.setLeft(btnConn);
		bottom.setRight(btnSend);
		root.setBottom(bottom);

		Scene scene = new Scene(root);
		// scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client");
		primaryStage.setOnCloseRequest(event -> stopClient());
		primaryStage.show();
	}

	void displayText(String text) {
		txtDisplay.appendText(text + "\n");

	}

	public static void main(String[] args) {
		launch(args);
	}

}