package cz.cuni.mff.kocaro.comm_app.comm_app_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CommAppBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommAppBackendApplication.class, args);
	}

	/**
	 * Allows requests from my own machine
	 * @return WebMvcConfigurer: Configurer with added CORS mapping
	 */
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") 
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

}
