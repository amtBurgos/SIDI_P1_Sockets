/**
 * 
 */
package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

/**
 * Interfaz para el chat del cliente.
 * 
 * @author Andres Miguel Teran - Francisco Saiz Güemes
 *
 */
public interface ChatClient {

	/**
	 * Arranque del cliente. Inicializacion del socket.
	 * 
	 * @return true/false si ha arrancado o no
	 */
	public boolean start();

	/**
	 * Mandar un mensaje.
	 * 
	 * @param msg
	 *            mensaje a mandar
	 */
	public void sendMessage(ChatMessage msg);

	/**
	 * Desconecta el cliente.
	 */
	public void disconnect();

}
