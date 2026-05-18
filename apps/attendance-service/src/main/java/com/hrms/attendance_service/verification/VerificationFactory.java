package com.hrms.attendance_service.verification;

import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.common.exception.BadRequestException;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VerificationFactory {

    private final Map<VerificationMethod, VerificationStrategy> strategyMap;

    public VerificationFactory(
            List<VerificationStrategy> strategies
    ) {

        this.strategyMap =
                strategies.stream()
                        .collect(Collectors.toMap(
                                VerificationStrategy::getMethod,
                                strategy -> strategy
                        ));
    }

    public VerificationStrategy getStrategy(
            VerificationMethod method
    ) {

        VerificationStrategy strategy =
                strategyMap.get(method);

        if (strategy == null) {

            throw new BadRequestException(
                    "Unsupported verification method"
            );
        }

        return strategy;
    }
}

