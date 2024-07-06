package org.wintersleep.crud.web;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.NonNull;
import org.openapitools.model.*;
import org.springframework.stereotype.Component;
import org.wintersleep.crud.domain.Company;
import org.wintersleep.crud.domain.QCompany;
import org.wintersleep.crud.provider.AbstractDataProvider;

@Component
public class CompanyDataProvider extends AbstractDataProvider<Company, Long, CompanyEntryDto, CompanyFilterDto, CompanyCreateDto, CompanyDto, CompanyUpdateDto> {

    public CompanyDataProvider() {
        super("companies", Company.class, QCompany.company);
    }

    @Override
    protected BooleanExpression mapFilter(CompanyFilterDto dto) {
        QCompany company = QCompany.company;
        return Expressions.allOf(
                search(dto.getQ()),
                like(company.name, dto.getName()),
                like(company.externalId, dto.getExternalId()),
                like(company.vatNumber, dto.getVatNumber())
        );
    }

    private BooleanExpression search(String q) {
        if (q == null) {
            return null;
        }
        QCompany company = QCompany.company;
        return Expressions.anyOf(
                like(company.name, q),
                like(company.vatNumber, q),
                like(company.externalId, q)
        );
    }

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

}
