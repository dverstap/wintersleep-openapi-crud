package org.wintersleep.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.wintersleep.crud.domain.Company;
import org.wintersleep.crud.domain.CompanyRepository;
import org.wintersleep.crud.domain.User;
import org.wintersleep.crud.domain.UserRepository;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
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
