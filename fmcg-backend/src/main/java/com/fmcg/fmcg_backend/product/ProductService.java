package com.fmcg.fmcg_backend.product;

import com.fmcg.fmcg_backend.product.dto.ProductResponse;
import com.fmcg.fmcg_backend.company.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

//This service checks that the company exists before retrieving its products.
// An existing company with no products returns an empty list.

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    public ProductService(
            ProductRepository productRepository,
            CompanyRepository companyRepository) {
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
    }

    public List<ProductResponse> getProductsByCompany(Long companyId) {

        if (!companyRepository.existsById(companyId)) {
            throw new IllegalArgumentException(
                    "Company not found: " + companyId);
        }

        return productRepository.findByCompany_Id(companyId)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }


    public Page<ProductResponse> searchProducts(
            String keyword,
            Long companyId,
            Pageable pageable) {

        return productRepository
                .searchProducts(keyword, companyId, pageable)
                .map(this::toProductResponse);
    }

    public ProductResponse getProductById(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found: " + productId));

        return toProductResponse(product);
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getSourceUrl(),
                product.getLastScrapedAt(),
                product.getCompany().getId(),
                product.getCompany().getName(),
                product.getCompany().getTicker()
        );
    }
}
