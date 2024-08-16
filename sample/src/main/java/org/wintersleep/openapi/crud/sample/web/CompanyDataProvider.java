package org.wintersleep.openapi.crud.sample.web;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.NonNull;
import org.openapitools.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wintersleep.openapi.crud.core.domain.BooleanTimestampPair;
import org.wintersleep.openapi.crud.sample.domain.Company;
import org.wintersleep.openapi.crud.core.provider.JpaQueryDslDataProvider;
import org.wintersleep.openapi.crud.sample.domain.QCompany;

@Service
@Transactional
public class CompanyDataProvider extends JpaQueryDslDataProvider<
        Company, Long, CompanyEntryDto,
        CompanySortDto, CompanyFilterDto,
        CompanyCreateDto, CompanyDto, CompanyUpdateDto> {

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
                like(company.vatNumber, dto.getVatNumber()),
                BooleanTimestampPair.filter(company.verifiedTimestampPair, dto.isVerified())
        );
    }

    @Override
    protected Expression<? extends Comparable<?>> mapOrderExpression(CompanySortDto fieldName) {
        final QCompany company = QCompany.company;
        return switch (fieldName) {
            case ID -> company.id;
            case NAME -> company.name;
            case EXTERNAL_ID -> company.externalId;
            case VAT_NUMBER -> company.vatNumber;
            case LAST_VERIFIED_AT -> company.verifiedTimestampPair.lastTrueAt;
            case LAST_UNVERIFIED_AT -> company.verifiedTimestampPair.lastFalseAt;
        };
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
                .verified(company.isVerified())
                .lastVerifiedAt(company.getVerifiedTimestampPair().getLastTrueAt())
                .lastUnverifiedAt(company.getVerifiedTimestampPair().getLastFalseAt())
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
                .verified(company.isVerified())
                .lastVerifiedAt(company.getVerifiedTimestampPair().getLastTrueAt())
                .lastUnverifiedAt(company.getVerifiedTimestampPair().getLastFalseAt())
                .build();
    }

    @Override
    protected Company mapCreate(@NonNull CompanyCreateDto dto) {
        return Company.builder()
                .vatNumber(dto.getVatNumber())
                .name(dto.getName())
                .externalId(dto.getExternalId())
                .url(dto.getUrl())
                .verifiedTimestampPair(BooleanTimestampPair.of(dto.isVerified()))
                .build();
    }

    @Override
    protected Company mapUpdate(@NonNull CompanyUpdateDto dto, @NonNull Company company) {
        company.setName(dto.getName());
        company.setUrl(dto.getUrl());
        company.setVerified(dto.isVerified());
        return company;
    }

}
