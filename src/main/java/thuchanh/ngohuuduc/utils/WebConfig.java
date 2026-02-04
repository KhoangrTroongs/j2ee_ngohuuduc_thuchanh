package thuchanh.ngohuuduc.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userDir = System.getProperty("user.dir");
        Path uploadDir = Paths.get(userDir, "src/main/resources/static/images");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
