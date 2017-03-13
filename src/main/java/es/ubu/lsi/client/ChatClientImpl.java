/**
 * 
 */
package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import es.ubu.lsi.common.ChatMessage;

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
	private boolean carryOn = true;

	/**
	 * Id del usuario.
	 */
	private int id;

	/**
	 * Construye un servidor.
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
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Mandar un mensaje.
	 * 
	 * @param msg
	 *            mensaje a mandar
	 */
	public void sendMessage(ChatMessage msg) {
		// TODO Auto-generated method stub

	}

	/**
	 * Desconecta el cliente.
	 */
	public void disconnect() {
		// TODO Auto-generated method stub
	}

	/**
	 * Arranca el hilo principal de ejecucion del cliente.
	 * 
	 * @param args
	 *            argumentos
	 */
	public static void main(String[] args) {

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
			// TODO Inicializar flujos y mostrar mensajes
			try {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				// Todo lo que ha entrado por el canal de entrada
				String inputLine;
				// Lo escribimos

				while ((inputLine = in.readLine()) != null) {
					out.println(inputLine);
					// Escribimos en la consola del servidor lo que hemos
					// recibido
					System.out.println("> " + inputLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
