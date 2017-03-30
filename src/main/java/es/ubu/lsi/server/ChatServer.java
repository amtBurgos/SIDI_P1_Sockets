/**
 * 
 */
package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

/**
 * Interfaz para el chat en el servidor.
 * 
 * @author Andres Miguel Teran - Francisco Saiz Güemes
 *
 */
public interface ChatServer {

	/**
	 * Espera y acepta peticiones de los clientes.
	 */
	public void startup();

	/**
	 * Apaga correctamente el servidor.
	 */
	public void shutdown();

	/**
	 * Manda un mensaje a todos los clientes.
	 * 
	 * @param message mensaje a mandar
	 */
	public void broadcast(ChatMessage message);

	/**
	 * Elimina un cliente de la lista.
	 * 
	 * @param id id
	 */
	public void remove(int id);
}
