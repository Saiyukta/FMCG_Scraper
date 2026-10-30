package com.fmcg.fmcg_backend.brand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findByCompanyId(Long companyId);

    Optional<Brand> findByCompanyIdAndName(
            Long companyId,
            String name
    );

    boolean existsByCompanyIdAndName(
            Long companyId,
            String name
    );
}