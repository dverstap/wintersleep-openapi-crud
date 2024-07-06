package org.wintersleep.crud.web;

import org.openapitools.api.CompaniesApi;
import org.openapitools.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wintersleep.crud.provider.OffsetLimit;
import org.wintersleep.crud.provider.SortRequest;

import java.util.List;

@RestController
@RequestMapping("/")
public class CompanyController
        //extends AbstractCrudController<Company, Long, CompanyEntryDto, CompanyFilterDto, CompanyCreateDto, CompanyDto, CompanyUpdateDto>
        implements CompaniesApi {

    private final CompanyDataProvider dataProvider;

    public CompanyController(CompanyDataProvider dataProvider) {
        //super(dataProvider.getResource(), companyRepository);
        this.dataProvider = dataProvider;
    }

    // Not called, because annotations are on the interface
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.registerCustomEditor(CompanyFilterDto.class, new ProductEditor(objectMapper));
//    }

    @Override
    public ResponseEntity<CompanyDto> createCompany(CompanyCreateDto dto) {
        return dataProvider.create(dto);
    }

    @Override
    public ResponseEntity<Void> deleteCompany(Long id) {
//        repository.deleteById(id);
//        return ResponseEntity.ok().build();
        /* TODO return*/
        dataProvider.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<CompanyEntryDto>> listCompany(Integer page, Integer size, CompanyFilterDto filter, String sort, Pageable pageable) {
        //return list(filter, pageable);
        return dataProvider.list(filter, SortRequest.of(sort), OffsetLimit.ofPage(page, size));
    }

    @Override
    public ResponseEntity<CompanyDto> readCompany(Long id) {
        //return read(id);
        return dataProvider.read(id);
    }

    @Override
    public ResponseEntity<CompanyDto> updateCompany(Long id, CompanyUpdateDto dto) {
        //return update(id, dto);
        return dataProvider.update(id, dto);
    }

/*
    @Override
    protected CompanyEntryDto mapEntry(@NonNull Company company) {
        return CompanyEntryDto.builder()
                .id(company.getId())
                .vatNumber(company.getVatNumber())
                .name(company.getName())
                .externalId(company.getExternalId())
                .build();
    }

    @Override
    protected CompanyDto mapRead(@NonNull Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .vatNumber(company.getVatNumber())
                .name(company.getName())
                .externalId(company.getExternalId())
                .url(company.getUrl())
                .build();
    }

    @Override
    protected Company mapCreate(@NonNull CompanyCreateDto dto) {
        return Company.builder()
                .vatNumber(dto.getVatNumber())
                .name(dto.getName())
                .externalId(dto.getExternalId())
                .url(dto.getUrl())
                .build();
    }

    @Override
    protected Company mapUpdate(@NonNull CompanyUpdateDto dto, @NonNull Company company) {
        company.setName(dto.getName());
        company.setUrl(dto.getUrl());
        return company;
    }

*/
}
