package konfa;

/**
 * Klasa składająca do kupy większość stałych istotnych w programie
 * 
 * @author mateusz
 */
public final class Consts {

	/**
	 * Port na którym ServiceRequestThread nasłuchuje na nowe połączenia
	 */
	public static final int portToListenOnService = 9000;
	/**
	 * Port na którym KServer nasłuchuje na nowe połącznenia od klientów
	 */
	public static final int portToListenOnClientsConnections = 7171;
	/**
	 * Port na którym LogServer nasłuchuje na nowe połączenia do otrzymywania
	 * logów
	 */
	public static final int portToListenOnLogsClients = 4569;
	/**
	 * ilość milisekund, co ile wszystkie operacje blokujące się na read mają
	 * przerwę by odetchnąć i spradzić czy wątek nie zostal perzerwany
	 */
	public static final int timoutToNonBlockListenOnConnection = 10000;
}
