/**
 * 
 */
package es.ubu.lsi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;

import es.ubu.lsi.common.ChatMessage;

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
	private int clientId;

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
	 * Construye un cliente.
	 * 
	 * @param port
	 *            puerto para establecer conexion
	 */
	public ChatServerImpl(int port) {
		// TODO Validar si el puerto esgta disponible y sino poner por defecto
		this.port = port;
	}

	/**
	 * Espera y acepta peticiones de los clientes.
	 */
	public void startup() {
		// TODO Auto-generated method stub

	}

	/**
	 * Apaga correctamente el servidor.
	 */
	public void shutdown() {
		// TODO Auto-generated method stub
	}

	/**
	 * Manda un mensaje a todos los clientes.
	 * 
	 * @param message
	 *            mensaje a mandar
	 */
	public void broadcast(ChatMessage message) {
		// TODO Auto-generated method stub

	}

	/**
	 * Elimina un cliente de la lista.
	 * 
	 * @param id
	 */
	public void remove(int id) {
		// TODO Auto-generated method stub

	}

	/**
	 * Arranca el hilo principal de ejecucion del servidor
	 * 
	 * @param args
	 *            argumentos pasados
	 */
	public static void main(String[] args) {
		// TODO Instanciar e inicializar el servidor
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

		public ServerThreadForClient(int id, String username) {
			this.id = id;
			this.username = username;
		}

		/**
		 * Inicializa los canales de entrada y salida y escribe en la consola el
		 * echo del servidor.
		 */
		@Override
		public void run() {
			// try {
			// PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(s.getInputStream()));
			// // Todo lo que ha entrado por el canal de entrada
			// String inputLine;
			// // Lo escribimos
			//
			// while ((inputLine = in.readLine()) != null) {
			// out.println(inputLine);
			// // Escribimos en la consola del servidor lo que hemos
			// // recibido
			// System.out.println(inputLine);
			// }
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

		}
	}
}
