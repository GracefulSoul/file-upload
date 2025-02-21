package gracefulsoul.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import gracefulsoul.storage.service.StorageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileUploadApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private StorageService storageService;

	@LocalServerPort
	private int port;

	@Test
	void shouldUploadFile() {
		ClassPathResource resource = new ClassPathResource("test.txt", getClass());
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("file", resource);
		ResponseEntity<String> response = this.restTemplate.postForEntity("/", map, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		assertThat(response.getHeaders().getLocation().toString()).startsWith("http://localhost:" + this.port + "/");
		then(storageService).should().store(any(MultipartFile.class));
	}

	@Test
	void shouldDownloadFile() {
		ClassPathResource resource = new ClassPathResource("test.txt", getClass());
		given(this.storageService.loadAsResource("test.txt")).willReturn(resource);
		ResponseEntity<String> response = this.restTemplate.getForEntity("/files/{filename}", String.class, "test.txt");
		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo("attachment; filename=\"test.txt\"");
		assertThat(response.getBody()).isEqualTo("Graceful Soul");
	}

}
