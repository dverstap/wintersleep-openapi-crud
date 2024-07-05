package org.wintersleep.crud.web;

import lombok.NonNull;
import org.openapitools.api.EmployeesApi;
import org.openapitools.model.EmployeeCreateDto;
import org.openapitools.model.EmployeeDto;
import org.openapitools.model.EmployeeEntryDto;
import org.openapitools.model.EmployeeUpdateDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wintersleep.crud.domain.CompanyRepository;
import org.wintersleep.crud.domain.Employee;
import org.wintersleep.crud.domain.EmployeeRepository;
import org.wintersleep.crud.domain.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/")
public class EmployeeController
        extends AbstractCrudController<Employee, Long, EmployeeEntryDto, EmployeeCreateDto, EmployeeDto, EmployeeUpdateDto>
        implements EmployeesApi {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public EmployeeController(EmployeeRepository employeeRepository, UserRepository userRepository, CompanyRepository companyRepository) {
        super(employeeRepository);
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

    @Override
    public ResponseEntity<List<EmployeeEntryDto>> listEmployee(Integer page, Integer size, String sort, Pageable pageable) {
        return list(pageable);
    }

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
                .lastActivated(employee.getLastActivatedAt())
                .lastDeActivated(employee.getLastDeActivatedAt())
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
                .lastActivated(employee.getLastActivatedAt())
                .lastDeActivated(employee.getLastDeActivatedAt())
                .active(employee.isActive())
                .build();
    }

    protected Employee mapCreate(@NonNull EmployeeCreateDto dto) {
        return Employee.builder()
                .company(companyRepository.findById(dto.getCompanyId()).orElseThrow())
                .user(userRepository.findById(dto.getUserId()).orElseThrow())
                .lastActivatedAt(dto.getActive() == null ? null : OffsetDateTime.now())
                .build();
    }

    protected Employee mapUpdate(@NonNull EmployeeUpdateDto dto, @NonNull Employee employee) {
        boolean wasActive = employee.isActive();
        if (!wasActive && dto.getActive()) {
            employee.setLastActivatedAt(OffsetDateTime.now());
        }
        if (wasActive && !dto.getActive()) {
            employee.setLastDeActivatedAt(OffsetDateTime.now());
        }
        return employee;
    }

}
