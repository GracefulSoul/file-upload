package gracefulsoul.storage.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import gracefulsoul.storage.exception.FileNotFoundException;
import gracefulsoul.storage.exception.StorageException;

@Service
public class FileStorageService implements StorageService {

	private Path location;

	public FileStorageService(@Value("${spring.servlet.multipart.location}") String location) {
        if(!StringUtils.hasText(location)){
            throw new StorageException("File upload location can not be Empty."); 
        }
		this.location = Paths.get(location);
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.location, 1).filter(path -> !path.equals(this.location)).map(this.location::relativize);
		} catch (IOException e) {
			throw new FileNotFoundException("Failed to read stored files", e);
		}
	}

	@Override
	public Path load(String filename) {
		return this.location.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new FileNotFoundException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new FileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new FileNotFoundException("Failed to store empty file.");
			}
			Path destinationFile = this.location.resolve(Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.location.toAbsolutePath())) {
				throw new FileNotFoundException("Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new FileNotFoundException("Failed to store file.", e);
		}
	}

	@Override
	public void init() {
		FileSystemUtils.deleteRecursively(this.location.toFile());
	}

	@Override
	public void deleteAll() {
		try {
			Files.createDirectories(this.location);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

}
