
package com.fmcg.fmcg_backend.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
@CrossOrigin(origins = "http://localhost:5173")
public class ScraperController {

    private static final Logger logger =
            LoggerFactory.getLogger(ScraperController.class);

    private final ScraperService scraperService;

    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @PostMapping("/hul")
    public ResponseEntity<Map<String, Object>> scrapeHul() {

        try {
            Map<String, Object> result =
                    scraperService.scrapeHulBrands();

            return ResponseEntity.ok(result);

        } catch (IOException e) {

            logger.error("Failed to fetch or parse the brands page", e);

            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Unable to scrape the brands page");
            error.put("details", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(error);

        } catch (IllegalStateException e) {

            logger.error("Company record was not found", e);

            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error);

        } catch (Exception e) {

            logger.error("Unexpected error while scraping brands", e);

            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "An unexpected error occurred during scraping");
            error.put("details", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
}
