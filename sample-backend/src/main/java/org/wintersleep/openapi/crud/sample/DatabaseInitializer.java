package org.wintersleep.openapi.crud.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.wintersleep.openapi.crud.sample.domain.Company;
import org.wintersleep.openapi.crud.sample.domain.CompanyRepository;
import org.wintersleep.openapi.crud.sample.domain.User;
import org.wintersleep.openapi.crud.sample.domain.UserRepository;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            for (int i = 0; i < 10; i++) {
                User user = User.builder()
                        .email(format("first-%s.last-%s@mailinator.com", i, i))
                        .displayName(format("First %s Last %s", i, i))
                        .build();
                userRepository.save(user);
            }
        }
        if (companyRepository.count() == 0) {
            for (int i = 0; i < 10; i++) {
                Company company = Company.builder()
                        .vatNumber(format("VAT%s", i))
                        .name(format("Company %s", i))
                        .url(format("https://example.com/company-%s", i))
                        .build();
                companyRepository.save(company);
            }
        }
    }

}
