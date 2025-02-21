package gracefulsoul.storage.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gracefulsoul.storage.service.FileStorageService;

@Controller
public class FileStorageController {

	private final FileStorageService fileUploadService;

	public FileStorageController(FileStorageService fileUploadService) {
		this.fileUploadService = fileUploadService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) {
		model.addAttribute("files", this.fileUploadService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(FileStorageController.class, "serveFile", path.getFileName().toString())
								.build().toUri().toString())
						.toList());
		return "uploadForm";
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam(name = "file") MultipartFile file, RedirectAttributes redirectAttributes) {
		this.fileUploadService.store(file);
		redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename());
		return "redirect:/";
	}

	@GetMapping("/files/{filename:.+}")
	public ResponseEntity<Resource> serveFile(@PathVariable(name = "filename") String filename) {
		Resource file = this.fileUploadService.loadAsResource(filename);
		if (file == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
					.body(file);
		}
	}

}
