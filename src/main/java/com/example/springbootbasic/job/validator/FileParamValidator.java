package com.example.springbootbasic.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class FileParamValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String filename = parameters.getString("fileName");

        if(!StringUtils.endsWithIgnoreCase(filename, ".csv")) {
            throw new JobParametersInvalidException(filename + " is not csv file");
        } else {
            System.out.println(filename + " is csv file");
        }
    }
}
