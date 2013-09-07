package konfa.server;

/**
 * Wyjątek do rzucania, jesli próbuje się zmienić maila na używany już w bazie
 * -> nie wolno, bo mail musi być unikalny, ponieważ jest identyfikatorem
 * 
 * @author mateusz
 * 
 */
public class EMailArleadyUsedException extends IllegalArgumentException {
	private static final long serialVersionUID = 4654208888281443818L;
}
