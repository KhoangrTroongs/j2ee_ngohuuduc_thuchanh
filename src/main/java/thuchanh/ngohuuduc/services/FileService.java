package thuchanh.ngohuuduc.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService {
    private final String uploadDir = "src/main/resources/static/images";

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // Create directory if not exists
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            log.debug("Uploading file to: {}", uploadPath);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            log.debug("Saving file to: {}", filePath);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path for web access
            return "/images/" + fileName;
        } catch (IOException e) {
            log.error("Failed to save file", e);
            throw e;
        }
    }
}
