package es.ubu.lsi.common;

/**
 * Message type.
 * 
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 * @author Andres Miguel Teran - Francisco Saiz Güemes
 */
public enum MessageType {
	/** Message. */
	MESSAGE,
	/** Shutdown server. */
	SHUTDOWN,		
	/** Logout client. */
	LOGOUT,
	/**Ban user.*/
	BAN,
	/**Unban user. */
	UNBAN;		
}
