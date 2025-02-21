package gracefulsoul.storage.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gracefulsoul.storage.exception.StorageException;

@SpringBootTest
class FileSystemStorageServiceTests {

	private String location;
	private StorageService service;

	@BeforeEach
	void init() {
		this.location = "/data/" + Math.abs(new Random().nextLong());
		this.service = new FileStorageService(this.location);
		this.service.init();
	}

	@Test
	void emptyUploadLocation() {
		this.service = null;
		this.location = "";
		assertThrows(StorageException.class, () -> {
			service = new FileStorageService(this.location);
		});
	}

	@Test
	void loadNonExistent() {
		assertThat(this.service.load("foo.txt")).doesNotExist();
	}

	@Test
	void saveAndLoad() {
		this.service.store(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()));
		assertThat(this.service.load("foo.txt")).exists();
	}

	@Test
	void saveRelativePathNotPermitted() {
		assertThrows(StorageException.class, () -> {
			service.store(new MockMultipartFile("foo", "../foo.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()));
		});
	}

	@Test
	void saveAbsolutePathNotPermitted() {
		assertThrows(StorageException.class, () -> {
			service.store(new MockMultipartFile("foo", "/etc/passwd", MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()));
		});
	}

	@Test
	void saveAbsolutePathInFilenamePermitted() {
		String fileName = "\\etc\\passwd";
		this.service.store(new MockMultipartFile(fileName, fileName, MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()));
		assertTrue(Files.exists(Paths.get(this.location).resolve(Paths.get(fileName))));
	}

	@Test
	void savePermitted() {
		this.service.store(new MockMultipartFile("foo", "bar/../foo.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()));
	}

}
