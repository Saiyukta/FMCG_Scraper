package com.fmcg.fmcg_backend.config;

import com.fmcg.fmcg_backend.company.Company;
import com.fmcg.fmcg_backend.company.CompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedCompanies(
            CompanyRepository companyRepository) {

        return args -> {
            if (companyRepository.count() > 0) {
                System.out.println(
                        "Company data already exists. Skipping seed.");
                return;
            }

            List<Company> companies = List.of(
                    createCompany(
                            "Hindustan Unilever Limited",
                            "https://www.hul.co.in/",
                            "HINDUNILVR", "NSE", true),

                    createCompany(
                            "Dabur India Limited",
                            "https://www.dabur.com/",
                            "DABUR", "NSE", true),

                    createCompany(
                            "Britannia Industries Limited",
                            "https://www.britannia.co.in/",
                            "BRITANNIA", "NSE", true),

                    createCompany(
                            "ITC Limited",
                            "https://www.itcportal.com/",
                            "ITC", "NSE", true),

                    createCompany(
                            "Marico Limited",
                            "https://marico.com/",
                            "MARICO", "NSE", true)
            );

            companyRepository.saveAll(companies);

            System.out.println(
                    "Seeded " + companies.size() + " companies.");
        };
    }

    private static Company createCompany(
            String name,
            String website,
            String ticker,
            String exchange,
            boolean listed) {

        Company company = new Company();
        company.setName(name);
        company.setWebsite(website);
        company.setTicker(ticker);
        company.setExchange(exchange);
        company.setListed(listed);

        return company;
    }
}