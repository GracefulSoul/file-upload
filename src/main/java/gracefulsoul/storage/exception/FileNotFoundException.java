package gracefulsoul.storage.exception;

public class FileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 3773438936295791277L;

	public FileNotFoundException() {
		super();
	}

	public FileNotFoundException(String message) {
		super(message);
	}

	public FileNotFoundException(Throwable cause) {
		super(cause);
	}

	public FileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
