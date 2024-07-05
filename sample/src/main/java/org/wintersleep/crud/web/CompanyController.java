package org.wintersleep.crud.web;

import lombok.NonNull;
import org.openapitools.api.CompaniesApi;
import org.openapitools.model.CompanyCreateDto;
import org.openapitools.model.CompanyDto;
import org.openapitools.model.CompanyEntryDto;
import org.openapitools.model.CompanyUpdateDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wintersleep.crud.domain.Company;
import org.wintersleep.crud.domain.CompanyRepository;

import java.util.List;

@RestController
@RequestMapping("/")
public class CompanyController
        extends AbstractCrudController<Company, Long, CompanyEntryDto, CompanyCreateDto, CompanyDto, CompanyUpdateDto>
        implements CompaniesApi {

    public CompanyController(CompanyRepository companyRepository) {
        super("companies", companyRepository);
    }

    @Override
    public ResponseEntity<CompanyDto> createCompany(CompanyCreateDto dto) {
        return create(dto);
    }

    @Override
    public ResponseEntity<Void> deleteCompany(Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<CompanyEntryDto>> listCompany(Integer page, Integer size, String sort, Pageable pageable) {
        return list(pageable);
    }

    @Override
    public ResponseEntity<CompanyDto> readCompany(Long id) {
        return read(id);
    }

    @Override
    public ResponseEntity<CompanyDto> updateCompany(Long id, CompanyUpdateDto dto) {
        return update(id, dto);
    }

    protected CompanyEntryDto mapEntry(@NonNull Company company) {
        return CompanyEntryDto.builder()
                .id(company.getId())
                .vatNumber(company.getVatNumber())
                .name(company.getName())
                .externalId(company.getExternalId())
                .build();
    }

    protected CompanyDto mapRead(@NonNull Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .vatNumber(company.getVatNumber())
                .name(company.getName())
                .externalId(company.getExternalId())
                .url(company.getUrl())
                .build();
    }

    protected Company mapCreate(@NonNull CompanyCreateDto dto) {
        return Company.builder()
                .vatNumber(dto.getVatNumber())
                .name(dto.getName())
                .externalId(dto.getExternalId())
                .url(dto.getUrl())
                .build();
    }

    protected Company mapUpdate(@NonNull CompanyUpdateDto dto, @NonNull Company company) {
        company.setName(dto.getName());
        company.setUrl(dto.getUrl());
        return company;
    }

}
