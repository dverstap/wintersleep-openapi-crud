package org.wintersleep.openapi.crud.sample.web;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.NonNull;
import org.wintersleep.openapi.crud.sample.api.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wintersleep.openapi.crud.core.domain.BooleanTimestampPair;
import org.wintersleep.openapi.crud.core.provider.JpaQueryDslDataProvider;
import org.wintersleep.openapi.crud.core.provider.OrderDirection;
import org.wintersleep.openapi.crud.sample.domain.*;

@Service
@Transactional
public class EmployeeDataProvider extends JpaQueryDslDataProvider<
        Employee, Long,
        EmployeeSortPropertyId, SampleOrderDirection, EmployeeFilterDto,
        EmployeeCreateDto, EmployeeDto, EmployeeUpdateDto> {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public EmployeeDataProvider(CompanyRepository companyRepository, UserRepository userRepository) {
        super("employees", Employee.class, QEmployee.employee, QEmployee.employee.id);
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected BooleanExpression mapFilter(EmployeeFilterDto dto) {
        QEmployee employee = QEmployee.employee;
        return Expressions.allOf(
// The type-safety here makes it hard to filter on fields of the related entity
// (because they are not declared in the CRUD definition), but at least these fields are searchable.
//                like(employee.name, dto.getName()),
//                like(employee.externalId, dto.getExternalId()),
//                like(employee.vatNumber, dto.getVatNumber()),
                eq(employee.company.id, dto.getCompanyId()),
                eq(employee.user.id, dto.getUserId()),
                BooleanTimestampPair.filter(employee.activatedTimestampPair, dto.isActive())
        );
    }

    @Override
    protected BooleanExpression mapSearch(String q) {
        QEmployee employee = QEmployee.employee;
        QUser user = employee.user;
        QCompany company = employee.company;
        return Expressions.anyOf(
                like(company.name, q),
                like(company.vatNumber, q),
                like(company.externalId, q),
                like(user.email, q),
                like(user.firstName, q),
                like(user.lastName, q),
                like(user.displayName, q)
        );
    }

    @Override
    protected Expression<? extends Comparable<?>> mapOrderExpression(EmployeeSortPropertyId propertyId, OrderDirection direction) {
        final QEmployee employee = QEmployee.employee;
        return switch (propertyId) {
            case ID -> employee.id;
            case ACTIVE -> BooleanTimestampPair.order(employee.activatedTimestampPair, direction);
            case LAST_ACTIVATED_AT -> employee.activatedTimestampPair.lastSetAt;
            case LAST_DE_ACTIVATED_AT -> employee.activatedTimestampPair.lastUnSetAt;
            case USER_ID -> employee.user.displayName;
            case COMPANY_ID -> employee.company.name;
        };
    }

    protected EmployeeDto mapRead(@NonNull Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .userId(employee.getUser().getId())
                .companyId(employee.getCompany().getId())
                .lastActivatedAt(employee.getActivatedTimestampPair().getLastSetAt())
                .lastDeActivatedAt(employee.getActivatedTimestampPair().getLastUnSetAt())
                .active(employee.isActive())
                .build();
    }

    protected Employee mapCreate(@NonNull EmployeeCreateDto dto) {
        return Employee.builder()
                .company(companyRepository.findById(dto.getCompanyId()).orElseThrow())
                .user(userRepository.findById(dto.getUserId()).orElseThrow())
                .activatedTimestampPair(BooleanTimestampPair.of(dto.isActive()))
                .build();
    }

    protected Employee mapUpdate(@NonNull EmployeeUpdateDto dto, @NonNull Employee employee) {
        employee.setActive(dto.isActive());
        return employee;
    }

}
