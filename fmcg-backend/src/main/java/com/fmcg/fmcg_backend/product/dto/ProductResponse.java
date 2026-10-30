
package com.fmcg.fmcg_backend.product.dto;
//Why a DTO? It defines the exact JSON fields returned by your API.
//Notice that we return the company's ID, name, and ticker instead of embedding the entire Company entity.

import java.time.LocalDateTime;

public class ProductResponse {

    private Long id;
    private String name;
    private String category;
    private String sourceUrl;
    private LocalDateTime lastScrapedAt;
    private Long companyId;
    private String companyName;
    private String companyTicker;

    public ProductResponse(
            Long id,
            String name,
            String category,
            String sourceUrl,
            LocalDateTime lastScrapedAt,
            Long companyId,
            String companyName,
            String companyTicker) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.sourceUrl = sourceUrl;
        this.lastScrapedAt = lastScrapedAt;
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyTicker = companyTicker;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public LocalDateTime getLastScrapedAt() {
        return lastScrapedAt;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyTicker() {
        return companyTicker;
    }
}
