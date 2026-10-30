package com.fmcg.fmcg_backend.product;

import com.fmcg.fmcg_backend.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/companies/{companyId}/products")
    public ResponseEntity<?> getProductsByCompany(
            @PathVariable Long companyId) {

        try {
            List<ProductResponse> products =
                    productService.getProductsByCompany(companyId);

            return ResponseEntity.ok(products);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        }
    }

    @GetMapping("/api/products/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest().build();
        }

        List<String> allowedSortFields = List.of(
                "id", "name", "category", "lastScrapedAt");

        if (!allowedSortFields.contains(sortBy)) {
            return ResponseEntity.badRequest().build();
        }

        Sort.Direction sortDirection;

        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        } else {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy));

        return ResponseEntity.ok(
                productService.searchProducts(
                        keyword, companyId, pageable));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    productService.getProductById(id));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        }
    }
}
