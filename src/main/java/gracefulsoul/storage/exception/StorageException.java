package gracefulsoul.storage.exception;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = -6372669441245194307L;

	public StorageException() {
		super();
	}

	public StorageException(String message) {
		super(message);
	}

	public StorageException(Throwable cause) {
		super(cause);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

}
