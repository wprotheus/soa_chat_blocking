import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.out;

public class ChatClient implements Runnable
{
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private ClientSocket clientSocket;
	private Scanner scanner;

	public ChatClient()
	{
		scanner = new Scanner(System.in);
	}

	public void start() throws IOException
	{
		try
		{
			clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, ChatServer.PORT));
			System.out.println("Cliente conectado ao servido em: " + SERVER_ADDRESS + " : " + ChatServer.PORT);
			new Thread(this).start();
			messageLoop();
		} finally
		{
			clientSocket.close();
		}
	}

	@Override
	public void run()
	{
		String msg;
		while ((msg = clientSocket.getMessage()) != null)
		{
			System.out.printf("Msg recebida do servidor: %s\n", msg);
		}
	}

	private void messageLoop() throws IOException
	{
		String msg;
		do
		{
			System.out.printf("Digite uma mensagem ou 'sair' para encerrar a aplicação: ");
			msg = scanner.nextLine();
			clientSocket.sendMsg(msg);
		} while (!msg.equalsIgnoreCase("sair"));
	}

	public static void main(String[] args)
	{
		try
		{
			ChatClient client = new ChatClient();
			client.start();
		} catch (IOException e)
		{
			out.println("Erro ao iniciar o chat. Erro: " + e.getMessage());
		}
	}
}
