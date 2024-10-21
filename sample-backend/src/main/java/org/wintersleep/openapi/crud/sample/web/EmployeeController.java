package org.wintersleep.openapi.crud.sample.web;

import org.wintersleep.openapi.crud.sample.api.EmployeesApi;
import org.wintersleep.openapi.crud.sample.api.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<EmployeeDto>> listEmployees(List<Long> ids,
                                                           EmployeeFilterDto filter,
                                                           String search,
                                                           EmployeeSortOrderDto sortOrder,
                                                           SampleStartEndDto startEnd) {
        return dataProvider.list(ids, filter, search, sortOrder, startEnd);
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
