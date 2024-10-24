package org.wintersleep.openapi.crud.sample.web;

import org.wintersleep.openapi.crud.sample.api.CompaniesApi;
import org.wintersleep.openapi.crud.sample.api.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<CompanyDto>> listCompanies(List<Long> ids,
                                                          CompanyFilterDto filter,
                                                          String search,
                                                          CompanySortOrderDto sortOrder,
                                                          SampleStartEndDto startEnd) {
        return dataProvider.list(ids, filter, search, sortOrder, startEnd);
    }

    @Override
    public ResponseEntity<CompanyDto> readCompany(Long id) {
        return dataProvider.read(id);
    }

    @Override
    public ResponseEntity<CompanyDto> updateCompany(Long id, CompanyUpdateDto dto) {
        return dataProvider.update(id, dto);
    }

}
