package konfa.server;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("javadoc")
public class MyLogger implements Thread.UncaughtExceptionHandler {
	private final Logger logger = Logger.getLogger("global");
	private static final MyLogger instance = new MyLogger();

	public static MyLogger getLogger() {
		return instance;
	}

	private MyLogger() {
		logger.setLevel(Level.ALL);
	}

	public synchronized void info(String msg) {
		logger.info(msg);
	}

	public synchronized void setLevel(Level all) {
		logger.setLevel(Level.ALL);

	}

	public synchronized void addHandler(Handler handler) {
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);

	}

	public synchronized void log(Level level, String msg) {
		logger.log(level, msg);
	}

	public synchronized void log(Level level, String msg, Throwable e) {
		logger.log(level, msg, e);
	}

	public synchronized void config(String msg) {
		logger.config(msg);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		synchronized (this) {
			logger.log(Level.SEVERE, "uncaughtException", e);
		}
	}

	public void turnOff() {
		for (Handler h : logger.getHandlers()) {
			h.close();
		}

	}

}
