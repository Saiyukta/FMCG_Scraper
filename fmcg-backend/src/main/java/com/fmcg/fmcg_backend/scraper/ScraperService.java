
package com.fmcg.fmcg_backend.scraper;

import com.fmcg.fmcg_backend.brand.Brand;
import com.fmcg.fmcg_backend.brand.BrandRepository;
import com.fmcg.fmcg_backend.company.Company;
import com.fmcg.fmcg_backend.company.CompanyRepository;
import com.fmcg.fmcg_backend.product.Product;
import com.fmcg.fmcg_backend.product.ProductRepository;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class ScraperService {

    private static final Logger logger =
            LoggerFactory.getLogger(ScraperService.class);

    private static final String SOURCE_URL =
//    "https://www.hul.co.in/brands/";
    "https://www.dabur.com/our-brand";
//        "https://www.britannia.co.in/";

    private static final String BRAND_SELECTOR =
//            "a.uol-c-card__title-link";
//            "div.views-field-title a";
//    ".OurBrands_brands-carousel__n2Q4r a[href^='/product/']";
    "ul.menu--dabur-corporate-profile > li.first > a";

    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public ScraperService(
            CompanyRepository companyRepository,
            ProductRepository productRepository, BrandRepository brandRepository) {

        this.companyRepository = companyRepository;
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    @Transactional
    public Map<String, Object> scrapeHulBrands() throws IOException {

        // Step 1: Find HUL in the companies table
//        Company company = companyRepository.findAll()
//                .stream()
//                .filter(c ->
//                        "HINDUNILVR".equalsIgnoreCase(c.getTicker()))
//                .findFirst()
//                .orElseThrow(() ->
//                        new IllegalStateException(
//                                "HUL company record not found. " +
//                                        "Check the HINDUNILVR ticker in your database."
//                        ));

        Company company = companyRepository.findAll()
                .stream()
                .filter(c ->
                        "DABUR".equalsIgnoreCase(c.getTicker()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "DABUR company record not found. " +
                                        "Check the DABUR ticker in your database."
                        ));

        // Step 2: Fetch the official HUL brands page
        Path htmlPath = Path.of(
                "C:/Users/Shivu/Downloads/fmcg-backend/fmcg-backend/src/data/dabur-brands1.html"

        );

        if (!Files.exists(htmlPath)) {
            throw new IOException(
                    "HTML file not found at: " + htmlPath.toAbsolutePath()
            );
        }

        Document document = Jsoup.parse(
                htmlPath.toFile(),
                "UTF-8",
                SOURCE_URL
        );

        logger.info("Fetched page: {}", document.title());

        // Step 3: Select brand links using the confirmed CSS selector
        Elements brandLinks = document.select(BRAND_SELECTOR);

        logger.info("Brand links found: {}", brandLinks.size());

        if (brandLinks.isEmpty()) {
            throw new IOException(
                    "No brand links found using selector: "
                            + BRAND_SELECTOR
                            + ". Check the HTML returned by Jsoup."
            );
        }

        // Step 4: Collect unique brand names and URLs
        Map<String, String> brands = new LinkedHashMap<>();

        for (Element link : brandLinks) {
            System.out.println("Link: " + link);

            String brandName = link.text().trim();
            String brandUrl = link.absUrl("href");

            if (brandName.isBlank() || brandUrl.isBlank()) {
                continue;
            }

//            // Accept only URLs from the official HUL website
//            if (!brandUrl.startsWith("https://www.hul.co.in/")) {
//                continue;
//            }

//            if (!brandUrl.startsWith("https://www.britannia.co.in/")) {
//                continue;
//            }

                        if (!brandUrl.startsWith("https://www.dabur.com/")) {
                continue;
            }


            String key = brandName.toLowerCase(Locale.ROOT);

            brands.putIfAbsent(key, brandUrl);
        }

        // Step 4: Collect unique product names and URLs
//        Map<String, String> brands = new LinkedHashMap<>();
//
//        for (Element link : brandLinks) {
//
//            String brandName = link.attr("title").trim();
//
//            if (brandName.isBlank()) {
//                brandName = link.text().trim();
//            }
//
//            if (brandName.isBlank()) {
//                Element image = link.selectFirst("img[alt]");
//                if (image != null) {
//                    brandName = image.attr("alt").trim();
//                }
//            }
//
//            String rawUrl = link.attr("href").trim();
//
//            // Only accept Britannia product links
//            if (brandName.isBlank() || !rawUrl.startsWith("/product/")) {
//                continue;
//            }
//
//            String productUrl = "https://www.britannia.co.in" + rawUrl;
//
//            String key = brandName.toLowerCase(Locale.ROOT);
//
//            brands.putIfAbsent(key, productUrl);
//
//        }

        logger.info("Unique brands collected: {}", brands.size());

        // Step 5: Save brands to the products table
        int inserted = 0;
        int skippedDuplicates = 0;

        for (Map.Entry<String, String> entry : brands.entrySet()) {

            final String normalizedName = entry.getKey();

            String brandName = brandLinks.stream()
                    .map(link -> link.text().trim())
                    .filter(name ->
                            name.equalsIgnoreCase(normalizedName))
                    .findFirst()
                    .orElse(normalizedName);

            String brandUrl = entry.getValue();

            boolean alreadyExists =
                    productRepository
                            .existsByCompany_IdAndNameIgnoreCase(
                                    company.getId(),
                                    brandName
                            );

            boolean alreadyExistsBrand =
                    brandRepository
                            .existsByCompanyIdAndName(
                                    company.getId(),
                                    brandName
                            );

            if (alreadyExists) {
                skippedDuplicates++;
                continue;
            }

            if (alreadyExistsBrand) {
                skippedDuplicates++;
                continue;
            }

//            Product product = new Product();
//            product.setCompany(company);
//            product.setName(
//                    brandName.substring(0, 1).toUpperCase() + brandName.substring(1)
//            );
//            product.setCategory("Brand");
//            product.setSourceUrl(brandUrl);
//            product.setLastScrapedAt(LocalDateTime.now());
//
//            productRepository.save(product);

            Brand brand = new Brand();
            brand.setCompany(company);
            brand.setName(brandName.substring(0, 1).toUpperCase() + brandName.substring(1));
            brand.setCategory(brandName);
            brand.setSourceUrl(brandUrl);
            brand.setLastScrapedAt(LocalDateTime.now());
            brandRepository.save(brand);

            inserted++;

            logger.info("Saved brand: {} -> {}", brandName, brandUrl);
        }

        // Step 6: Return the scraping summary
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("company", company.getName());
        result.put("sourceUrl", SOURCE_URL);
        result.put("pageTitle", document.title());
        result.put("brandLinksFound", brandLinks.size());
        result.put("uniqueBrandsFound", brands.size());
        result.put("inserted", inserted);
        result.put("skippedDuplicates", skippedDuplicates);

        logger.info(
                "Scraping completed. Inserted: {}, duplicates skipped: {}",
                inserted,
                skippedDuplicates
        );

        return result;
    }

}
