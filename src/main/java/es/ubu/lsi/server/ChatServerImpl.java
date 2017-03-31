/**
 * 
 */
package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Clase para el chat del servidor.
 * 
 * @author Andres Miguel Teran - Francisco Saiz Güemes
 *
 */
public class ChatServerImpl implements ChatServer {

	/**
	 * Puerto por defecto.
	 */
	private static final int DEFAULT_PORT = 1500;

	/**
	 * Id del cliente.
	 */
	private static int clientId = 0;

	/**
	 * Fecha.
	 */
	private SimpleDateFormat sdf;

	/**
	 * Puerto.
	 */
	private int port;

	/**
	 * Si el hilo esta vivo o no.
	 */
	private boolean alive;

	/**
	 * Lista de usuarios conectados.
	 */
	private ArrayList<ServerThreadForClient> usersList;

	/**
	 * Lista con los nombres de usuarios.
	 */
	private ArrayList<String> userNamesList;

	/**
	 * Construye un servidor.
	 * 
	 * @param port
	 *            puerto para establecer conexion
	 */
	public ChatServerImpl(int port) {
		// Validar si el puerto esta disponible y sino poner por defecto
		try {
			Socket test = new Socket("localhost", port);
			this.port = port;
			test.close();
		} catch (Exception e) {
			this.port = DEFAULT_PORT;
		}
	}

	/**
	 * Espera y acepta peticiones de los clientes.
	 */
	public void startup() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			alive = true;
			usersList = new ArrayList<ServerThreadForClient>();
			userNamesList = new ArrayList<String>();
			sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("Servidor iniciado...");
			System.out.println("________________________________________\n");
		} catch (IOException e) {
			System.err.println("No se puede iniciar el servidor.");
		}

		while (alive) {
			try {
				clientSocket = serverSocket.accept();
				ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, clientId++);
				usersList.add(clientThread);
				clientThread.start();
			} catch (IOException e) {
				System.err.println("# Cliente no aceptado.");
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("No se puede cerrar el servidor.");
		} finally {
			// Apagar el servidor
			System.exit(1);
		}
	}

	/**
	 * Apaga correctamente el servidor, finalizando los clientes que haya.
	 */
	public void shutdown() {
		if (usersList.size() != 0) {
			for (ServerThreadForClient client : usersList) {
				client.finalizarCliente();
			}
		}
		System.out.println("Apagando servidor...");
		alive = false;
	}

	/**
	 * Manda un mensaje a todos los clientes.
	 * 
	 * @param message
	 *            mensaje a mandar
	 */
	public synchronized void broadcast(ChatMessage message) {
		String fecha = sdf.format(new Date());
		String msg = fecha + " " + message.getMessage();

		System.out.println(msg);

		for (ServerThreadForClient client : usersList) {
			try {
				if (client.getClientId() != message.getId()) {
					client.out.writeObject(msg);
				}
			} catch (IOException e) {
				System.out.println("# " + client.getUsername() + "No recibe mensajes.");
			}
		}
	}

	/**
	 * Manda un mensaje a todos los clientes.
	 * 
	 * @param message
	 *            mensaje a mandar
	 * 
	 * @param emisor
	 *            emisor que envia el mensaje
	 */
	public synchronized void broadcast(ChatMessage message, String emisor) {
		String fecha = sdf.format(new Date());
		String msg = fecha + " " + emisor + ": " + message.getMessage();

		System.out.println(msg);

		for (ServerThreadForClient client : usersList) {
			try {
				if (client.getClientId() != message.getId()) {
					client.out.writeObject(msg);
				}
			} catch (IOException e) {
				System.out.println("# " + client.getUsername() + "No recibe mensajes.");
			}
		}
	}

	/**
	 * Elimina un cliente de la lista.
	 * 
	 * @param id
	 *            id del cliente que vamos a eliminar
	 */
	public void remove(int id) {
		ServerThreadForClient cliente = null;
		boolean flag = false;
		if (usersList.size() != 0) {
			for (ServerThreadForClient client : usersList) {
				if (client.getClientId() == id) {
					cliente = client;
					flag = true;
					break;
				}
			}
			if (flag) {
				userNamesList.remove(cliente.getUsername().toLowerCase());
				usersList.remove(cliente);
				System.out.println("El cliente no se encuentra conectado.");
			}
		} else {
			System.out.println("No hay clientes conectados.");
		}
	}

	/**
	 * Banea o desbanea a un usuario.
	 * 
	 * @param username
	 *            nombre del usuario a banear o desbanear
	 * @param operacion
	 *            banear o desbanear
	 * @return true/false si se ha realizado la operación
	 */
	public boolean banUnbanUser(String username, ChatMessage.MessageType operacion) {
		boolean realizado = false;
		boolean banned = (operacion == ChatMessage.MessageType.BAN) ? true : false;
		for (ServerThreadForClient client : usersList) {
			if (client.getUsername().equalsIgnoreCase(username)) {
				client.setBanned(banned);
				realizado = true;
			}
		}
		if (!realizado)
			System.out.println("El usuario a banear o desbanear no existe.");
		return realizado;
	}

	/**
	 * Arranca el hilo principal de ejecucion del servidor
	 * 
	 * @param args
	 *            argumentos pasados
	 */
	public static void main(String[] args) {
		ChatServerImpl server = new ChatServerImpl(DEFAULT_PORT);
		server.startup();
	}

	/**
	 * Hilo para comunicarse con el servidor.
	 * 
	 * @author Andres Miguel Teran - Francisco Saiz Güemes
	 *
	 */
	private class ServerThreadForClient extends Thread {
		/**
		 * Id del cliente.
		 */
		private int id;

		/**
		 * Nombre de usuario.
		 */
		private String username;

		/**
		 * Canal de entrada.
		 */
		private ObjectInputStream in;

		/**
		 * Canal de salida.
		 */
		private ObjectOutputStream out;

		/**
		 * Socket del cliente.
		 */
		private Socket clientSocket;

		/**
		 * Si el usuario está baneado o no.
		 */
		private boolean banned;

		/**
		 * Booleano para ver si ha acabado el cliente.
		 */
		private boolean finalizado;

		public ServerThreadForClient(Socket clientSocket, int id) {

			this.clientSocket = clientSocket;
			this.id = id;
			this.banned = false;

			try {
				this.out = new ObjectOutputStream(clientSocket.getOutputStream());
				this.in = new ObjectInputStream(clientSocket.getInputStream());
				this.username = (String) in.readObject();

				// Si el usuario ya esta registrado pues no lo aceptamos
				if (userNamesList.contains(username.toLowerCase())) {
					this.out.writeObject("Ya hay un usuario registrado con ese nombre.");
					remove(id);
					finalizarCliente();
				} else {
					userNamesList.add(username.toLowerCase());
				}

			} catch (ClassNotFoundException e) {
				System.err.println("No se puede recuperar el nombre de usuario.");

			} catch (IOException e) {
				System.err.println("No se puede establecer comunicación cliente-servidor");
			}

			finalizado = false;
		}

		/**
		 * Inicializa los canales de entrada y salida para la comunicación del
		 * cliente y el servidor.
		 */
		@Override
		public void run() {
			broadcast(new ChatMessage(getClientId(), MessageType.MESSAGE, getUsername() + " se conectó."));

			// Esto es donde le pasamos al cliente, su nuevo id.
			try {
				out.writeObject(new ChatMessage(id, ChatMessage.MessageType.MESSAGE, username));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Acciones a realizar mientras el hilo está escuchando
			while (!finalizado) {
				ChatMessage message = null;
				try {
					message = (ChatMessage) in.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("No se puede recuperar el mensaje");
				}
				switch (message.getType()) {
				case LOGOUT:
					setFinalizado(true);
					remove(getClientId());
					break;
				case MESSAGE:
					if (!banned) {
						broadcast(message, getUsername());
					} else {
						try {
							out.writeObject("Estas baneado, no puede comentar.");
						} catch (IOException e) {
							System.err.println("No se puede enviar mensaje de baneo.");
						}
					}
					break;
				case BAN:
					if (banUnbanUser(message.getMessage(), ChatMessage.MessageType.BAN))
						broadcast(new ChatMessage(getClientId(), MessageType.MESSAGE,
								"# " + getUsername() + " baneó a " + message.getMessage()));
					break;
				case UNBAN:
					if (banUnbanUser(message.getMessage(), ChatMessage.MessageType.UNBAN))
						broadcast(new ChatMessage(getClientId(), MessageType.MESSAGE,
								"# " + getUsername() + " desbaneó a " + message.getMessage()));
					break;
				case SHUTDOWN:
					shutdown();
				}
			}
			finalizarCliente();
		}

		/**
		 * Cambia el valor para el bucle del hilo del cliente.
		 * 
		 * @param finalizado
		 *            true/false
		 */
		public void setFinalizado(boolean finalizado) {
			this.finalizado = finalizado;
		}

		/**
		 * Devuelve el id del cliente.
		 * 
		 * @return client id
		 */
		public int getClientId() {
			return this.id;
		}

		/**
		 * Devuelve el nombre de usuario.
		 * 
		 * @return username
		 */
		public String getUsername() {
			return this.username;
		}

		/**
		 * Banea a un usuario.
		 * 
		 * @param banned
		 *            true/false
		 */
		public void setBanned(boolean banned) {
			this.banned = banned;
		}

		/**
		 * Finaliza el hilo del cliente.
		 */
		public void finalizarCliente() {
			try {
				// Cerramos canales de entrada y salida
				this.out.close();
				this.in.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("No se puede eliminar conexión con el cliente.");
			} finally {
				System.out.println("# Desconectando cliente: " + getUsername() + ".");
				// Cerramos el hilo
				this.interrupt();
			}
		}
	}
}
