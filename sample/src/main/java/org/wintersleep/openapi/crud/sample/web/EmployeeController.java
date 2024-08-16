package org.wintersleep.openapi.crud.sample.web;

import lombok.NonNull;
import org.openapitools.api.EmployeesApi;
import org.openapitools.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wintersleep.openapi.crud.sample.domain.CompanyRepository;
import org.wintersleep.openapi.crud.sample.domain.Employee;
import org.wintersleep.openapi.crud.sample.domain.EmployeeRepository;
import org.wintersleep.openapi.crud.sample.domain.UserRepository;
import org.wintersleep.openapi.crud.core.util.Now;

import java.util.List;

@RestController
@RequestMapping("/")
@Transactional // Unconventional, but ok for CRUD
public class EmployeeController
        extends AbstractCrudController<Employee, Long, EmployeeEntryDto, EmployeeFilterDto, EmployeeCreateDto, EmployeeDto, EmployeeUpdateDto>
        implements EmployeesApi {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public EmployeeController(EmployeeRepository employeeRepository, UserRepository userRepository, CompanyRepository companyRepository) {
        super("employees", employeeRepository);
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public ResponseEntity<EmployeeDto> createEmployee(EmployeeCreateDto dto) {
        return create(dto);
    }

    @Override
    public ResponseEntity<Void> deleteEmployee(Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<EmployeeEntryDto>> listEmployee(Integer page, Integer size, EmployeeFilterDto filter, String sort, Pageable pageable) {
        return list(filter, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<EmployeeDto> readEmployee(Long id) {
        return read(id);
    }

    @Override
    public ResponseEntity<EmployeeDto> updateEmployee(Long id, EmployeeUpdateDto dto) {
        return update(id, dto);
    }

    protected EmployeeEntryDto mapEntry(@NonNull Employee employee) {
        return EmployeeEntryDto.builder()
                .id(employee.getId())
                .userId(employee.getUser().getId())
                .userDisplayName(employee.getUser().getDisplayName())
                .companyId(employee.getCompany().getId())
                .companyName(employee.getCompany().getName())
                .lastActivatedAt(employee.getLastActivatedAt())
                .lastDeActivatedAt(employee.getLastDeActivatedAt())
                .active(employee.isActive())
                .build();
    }

    protected EmployeeDto mapRead(@NonNull Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .userId(employee.getUser().getId())
                .userDisplayName(employee.getUser().getDisplayName())
                .companyId(employee.getCompany().getId())
                .companyName(employee.getCompany().getName())
                .lastActivatedAt(employee.getLastActivatedAt())
                .lastDeActivatedAt(employee.getLastDeActivatedAt())
                .active(employee.isActive())
                .build();
    }

    protected Employee mapCreate(@NonNull EmployeeCreateDto dto) {
        return Employee.builder()
                .company(companyRepository.findById(dto.getCompanyId()).orElseThrow())
                .user(userRepository.findById(dto.getUserId()).orElseThrow())
                .lastActivatedAt(dto.isActive() ? Now.offsetDateTime() : null)
                .build();
    }

    protected Employee mapUpdate(@NonNull EmployeeUpdateDto dto, @NonNull Employee employee) {
        boolean wasActive = employee.isActive();
        if (!wasActive && dto.isActive()) {
            employee.setLastActivatedAt(Now.offsetDateTime());
        }
        if (wasActive && !dto.isActive()) {
            employee.setLastDeActivatedAt(Now.offsetDateTime());
        }
        return employee;
    }

}
