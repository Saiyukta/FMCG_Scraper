package com.fmcg.fmcg_backend.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    boolean existsByCompany_IdAndNameIgnoreCase(
            Long companyId, String name);

    List<Product> findByCompany_Id(Long companyId);

    @Query("""
        SELECT p FROM Product p
        WHERE (:keyword IS NULL OR :keyword = ''
               OR LOWER(p.name) LIKE
               LOWER(CONCAT('%', :keyword, '%')))
        AND (:companyId IS NULL OR p.company.id = :companyId)
        """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("companyId") Long companyId,
            Pageable pageable);
}



//Spring Data JPA interprets the method name and generates a query based on the company relationship and its id.
//You don't need to write SQL manually.