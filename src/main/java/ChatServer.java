import java.io.*;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatServer
{
	public static final int PORT = 8000;
	private ServerSocket serverSocket;
	private final List<ClientSocket> clientSocketLinkedList = new LinkedList<>();

	public void start() throws IOException
	{
		serverSocket = new ServerSocket(PORT);
		System.out.println("Servidor iniciado na porta: " + PORT);
		clientConnectionLoop();
	}

	private void clientConnectionLoop() throws IOException
	{
		while (true)
		{
			ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
			clientSocketLinkedList.add(clientSocket);
			new Thread(()-> clientMessageLoop(clientSocket)).start();
		}
	}

	private void clientMessageLoop(ClientSocket clientSocket)
	{
		String msg;
		try
		{
			while ((msg = clientSocket.getMessage()) != null)
			{
				if("sair".equalsIgnoreCase(msg))
					return;
				System.out.printf("Msg recebida do cliente %s: %s\n",
						clientSocket.getRemoteSocketAddress(), msg);
				sendMsgToAll(clientSocket, msg);
			}
		} finally
		{
			clientSocket.close();
		}

	}

	private void sendMsgToAll(ClientSocket sender, String msg)
	{
		Iterator<ClientSocket> clientSocketIterator = clientSocketLinkedList.iterator();
		while (clientSocketIterator.hasNext())
		{
			ClientSocket cs = clientSocketIterator.next();
			if(!sender.equals(cs))
				if (!cs.sendMsg("Cliente: " + sender.getRemoteSocketAddress() + ": " + msg))
					clientSocketIterator.remove();
		}
	}
	public static void main(String[] args)
	{
		try
		{
			ChatServer server = new ChatServer();
			server.start();
		} catch (IOException e)
		{
			System.out.println("Erro ao iniciar o servidor. Erro: " + e.getMessage());
		}
	}
}