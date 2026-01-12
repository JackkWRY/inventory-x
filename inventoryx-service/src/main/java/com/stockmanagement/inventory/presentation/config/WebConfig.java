package com.stockmanagement.inventory.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig - Web MVC configuration.
 * 
 * CORS CONFIGURATION:
 * =================
 * Allows frontend (Nuxt) to call backend API.
 * 
 * DEVELOPMENT:
 * - Allows localhost:3000 (Nuxt dev server)
 * 
 * PRODUCTION:
 * - Update allowedOrigins to production domain
 * - Consider using environment variables
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Nuxt dev server
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
