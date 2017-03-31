/**
 * 
 */
package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Clase para el chat del cliente.
 * 
 * @author Andres Miguel Teran - Francisco Saiz Güemes
 *
 */
public class ChatClientImpl implements ChatClient {

	/**
	 * Servidor al que conectarse.
	 */
	private String server;

	/**
	 * Nombre de usuario.
	 */
	private String username;

	/**
	 * Puerto de conexion.
	 */
	private int port;

	/**
	 * Condicionante para dejar de enviar.
	 */
	private static boolean carryOn = true;

	/**
	 * Id del usuario.
	 */
	private static int id;

	/**
	 * Canal de entrada.
	 */
	private ObjectInputStream in;

	/**
	 * Canal de salida.
	 */
	private ObjectOutputStream out;

	/**
	 * Construye un cliente.
	 * 
	 * @param server
	 *            ip del servidor
	 * @param port
	 *            puerto
	 * @param username
	 *            nombre de usuario
	 */
	public ChatClientImpl(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/**
	 * Arranque del cliente. Inicializacion del socket.
	 * 
	 * @return true/false si ha arrancado o no
	 */
	@SuppressWarnings("resource")
	public boolean start() {
		// Creamos socket del cliente
		Socket clientSocket = null;

		try {
			clientSocket = new Socket(server, port);

			// Inicializar canales de entrada y salida
			this.out = new ObjectOutputStream(clientSocket.getOutputStream());
			this.in = new ObjectInputStream(clientSocket.getInputStream());

			// Lanzamos el hilo del cliente
			new Thread(new ChatClientListener()).start();

			// Indicamos el nombre de usuario
			out.writeObject(username);

			// Informamos de la conexion
			// System.out.println("Conexión satisfactoria");
		} catch (UnknownHostException e) {
			System.err.println("No se puede conectar con el servidor: " + server);
			return false;
		} catch (IOException e) {
			System.err.println("No se puede establecer conexción entrada/salida con:" + server);
			return false;
		}
		return true;
	}

	/**
	 * Mandar un mensaje.
	 * 
	 * @param msg
	 *            mensaje a mandar
	 */
	public void sendMessage(ChatMessage msg) {
		try {
			this.out.reset();
			this.out.writeObject(msg);
		} catch (IOException e) {
			System.out.println("No se puede enviar el mensaje");
		}
	}

	/**
	 * Desconecta el cliente.
	 */
	public void disconnect() {
		try {
			if (this.in != null) {
				this.in.close();
			}
			if (this.out != null) {
				this.out.close();
			}
		} catch (IOException e) {
			System.err.println("Error al desconectar cliente '" + this.username + "'.");
		}
	}

	/**
	 * Arranca el hilo principal de ejecucion del cliente.
	 * 
	 * @param args
	 *            argumentos
	 */
	public static void main(String[] args) {
		// Variables para la conexión
		String hostName = "localhost";
		int portNumber = 1500;
		String username = null;
		Scanner scan = new Scanner(System.in);

		// Manejo de argumentos
		if (args.equals(null) || args.length != 2) {
			System.out.println("Introduce un nombre de usuario: ");
			username = scan.nextLine();

		} else if (args.length == 1) {
			username = args[0];

		} else if (args.length == 2) {
			hostName = args[0];
			username = args[1];
		}

		// Listener de mensajes para el cliente
		ChatClientImpl cliente = new ChatClientImpl(hostName, portNumber, username);

		if (cliente.start()) {
			System.out.println("\n\n            · BIENVENIDO ·\n");
			System.out.println("Te has logeado correctamente en el chat.\n");
			System.out.println("Tu nombre de usuario es: " + username);
			System.out.println("Los comandos disponibles son: \n");
			System.out.println("    - BAN ");
			System.out.println("    - UNBN ");
			System.out.println("    - LOGOUT ");
			System.out.println("________________________________________\n");

			Scanner scan2 = new Scanner(System.in);

			while (carryOn) {
				System.out.print("> ");
				String message = scan2.nextLine();
				if (message.equalsIgnoreCase("LOGOUT")) {
					cliente.sendMessage(new ChatMessage(id, ChatMessage.MessageType.LOGOUT, ""));
					carryOn = false;
				} else if (message.toLowerCase().matches("^\\s*ban\\s+\\S+\\s*")) {
					cliente.sendMessage(banManager(message, ChatMessage.MessageType.BAN));
				} else if (message.toLowerCase().matches("^\\s*unban\\s+\\S+\\s*")) {
					cliente.sendMessage(banManager(message, ChatMessage.MessageType.UNBAN));
				} else if (message.toLowerCase().matches("^\\s*shutdown\\s*")) {
					cliente.sendMessage(banManager(message, ChatMessage.MessageType.SHUTDOWN));
				} else {
					cliente.sendMessage(new ChatMessage(id, ChatMessage.MessageType.MESSAGE, message));
				}
			}
			System.out.println("Desconectando. Pulsa intro tecla para cerrar...");
			scan.nextLine();
			scan.close();
			scan2.close();
		} else {
			System.err.println("No se puede arrancar el cliente.");
		}
		cliente.disconnect();
		System.exit(1);
	}

	/**
	 * Crea el tipo de mensaje a enviar con el nombre de usuario a banear o
	 * desbanear.
	 * 
	 * @param message
	 *            mensaje con el comando
	 * @param banType
	 *            tipo de baneo
	 * @return objeto ChatMessage con los datos
	 */
	private static ChatMessage banManager(String message, MessageType banType) {
		String[] command = message.split("\\s");
		String username = null;
		if (command.length >= 2) {
			int counter = 0;
			for (String word : command) {
				if (!word.equals("")) {
					counter++;
					if (counter == 2) {
						username = word;
						break;
					}
				}
			}
		}
		return new ChatMessage(id, banType, username);
	}

	/**
	 * Hilo de escucha de mensajes en el servidor.
	 * 
	 * @author Andres Miguel Teran - Francisco Saiz Güemes
	 *
	 */
	private class ChatClientListener implements Runnable {

		/**
		 * Ejecucion del hilo.
		 */
		public void run() {
			String message;

			try {
				// Aqui establecemos el nuevo id del mensaje que nos ha devuelto
				// el hilo del server.
				id = ((ChatMessage) in.readObject()).getId();
				while ((message = (String) in.readObject()) != null) {
					// Imprimimos lo que recibimos del servidor
					System.out.println(message);
					System.out.println("> ");
				}
			} catch (ClassNotFoundException e) {
				System.err.println("Error al recibir mensaje.");
			} catch (IOException e) {
				System.err.println("Te has desconectado del servidor " + server + ".");
				carryOn = false;
			}
		}
	}
}
