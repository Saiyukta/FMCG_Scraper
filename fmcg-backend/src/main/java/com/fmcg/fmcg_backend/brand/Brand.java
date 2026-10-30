
package com.fmcg.fmcg_backend.brand;

import com.fmcg.fmcg_backend.company.Company;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_brands_company_name",
                        columnNames = {"company_id", "name"}
                )
        }
)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    @Column(name = "last_scraped_at")
    private LocalDateTime lastScrapedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Brand() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public LocalDateTime getLastScrapedAt() {
        return lastScrapedAt;
    }

    public void setLastScrapedAt(LocalDateTime lastScrapedAt) {
        this.lastScrapedAt = lastScrapedAt;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
