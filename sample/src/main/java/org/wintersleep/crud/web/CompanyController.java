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
public class CompanyController implements CompaniesApi {

    private final CompanyDataProvider dataProvider;

    public CompanyController(CompanyDataProvider dataProvider) {
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
        /* TODO return*/
        dataProvider.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<CompanyEntryDto>> listCompany(Integer page, Integer size, CompanyFilterDto filter, String sort, Pageable pageable) {
        return dataProvider.list(filter, SortRequest.parse(sort, CompanySortDto::fromValue), OffsetLimit.ofPage(page, size));
    }

    // TODO
//    @Override
//    public ResponseEntity<List<CompanyEntryDto>> getManyCompanies(List<Long> ids) {
//    }

    @Override
    public ResponseEntity<CompanyDto> readCompany(Long id) {
        return dataProvider.read(id);
    }

    @Override
    public ResponseEntity<CompanyDto> updateCompany(Long id, CompanyUpdateDto dto) {
        return dataProvider.update(id, dto);
    }

}
