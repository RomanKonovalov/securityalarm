package com.romif.securityalarm.web.managment;

import com.romif.securityalarm.service.SmsTxtlocalService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;

@Component
public class SmsHealthIndicator implements HealthIndicator {

    @Inject
    private SmsTxtlocalService smsTxtlocalService;

    @Override
    public Health health() {
        BigDecimal balance =  smsTxtlocalService.getBalance();

        if (balance == null) {
            return Health.down().withDetail("Balance", "null").build();
        }
        return Health.up().withDetail("Balance", balance).build();
    }

}
