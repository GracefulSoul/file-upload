package gracefulsoul.storage.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import gracefulsoul.storage.exception.FileNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<FileNotFoundException> handleStorageFileNotFound(FileNotFoundException e) {
		return ResponseEntity.notFound().build();
	}

}
