package org.wintersleep.openapi.crud.sample.web;

import org.openapitools.api.EmployeesApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wintersleep.openapi.crud.core.provider.GetManyIdentifiers;
import org.wintersleep.openapi.crud.core.provider.OffsetLimit;
import org.wintersleep.openapi.crud.core.provider.SortRequest;

import java.util.List;

@RestController
@RequestMapping("/")
public class EmployeeController implements EmployeesApi {

    private final EmployeeDataProvider dataProvider;

    public EmployeeController(EmployeeDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public ResponseEntity<EmployeeDto> createEmployee(EmployeeCreateDto dto) {
        return dataProvider.create(dto);
    }

    @Override
    public ResponseEntity<Void> deleteEmployee(Long id) {
        dataProvider.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<EmployeeEntryDto>> listEmployees(Integer page, Integer size, EmployeeFilterDto filter, String sort) {
        return dataProvider.list(filter, SortRequest.parse(sort, EmployeeSortDto::fromValue), OffsetLimit.ofPage(page, size));
    }

    @Override
    public ResponseEntity<List<EmployeeDto>> getManyEmployees(List<Long> ids) {
        return dataProvider.getMany(ids);
    }

    @Override
    public ResponseEntity<EmployeeDto> readEmployee(Long id) {
        return dataProvider.read(id);
    }

    @Override
    public ResponseEntity<EmployeeDto> updateEmployee(Long id, EmployeeUpdateDto dto) {
        return dataProvider.update(id, dto);
    }

}
