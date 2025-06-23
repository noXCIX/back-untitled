package com.custom.service;

import com.custom.dao.JobDataDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobDataService {
    private final JobDataDao jobDataDao;

    public Map<String, Object> getJobData(Map<String, Object> inputOption) {
        String jobTitle = null, gender = null, fields = null, sort = null, sortType = null, orderBy = "";
        BigDecimal salaryEq = null, salaryGte = null, salaryLte = null;
        List<String> fieldList = jobDataDao.getJobDataFields();
        List<String> sortTypeList = List.of("asc", "desc");
        Map<String, Object> param = new HashMap<>();
        for (Map.Entry<String, Object> entry : inputOption.entrySet()) {
            param.put(entry.getKey(), entry.getValue());
        }
        jobTitle = getKeyValue(param, "job_title");
        salaryEq = cleanSalary(getKeyValue(param, "salary[eq]"));
        salaryGte = cleanSalary(getKeyValue(param, "salary[gte]"));
        salaryLte = cleanSalary(getKeyValue(param, "salary[lte]"));
        gender = getKeyValue(param, "gender");
        fields = getKeyValue(param, "fields");
        if (fields != null) {
            fields = fields.toLowerCase();
            if (fields.endsWith(",")) {
                fields = fields.substring(0, fields.length() - 1);
            }
            String[] inputFields = fields.split(",");
            fields = Arrays.stream(inputFields).map(String::trim).filter(fieldList::contains).collect(Collectors.joining(", "));
        }
        sort = getKeyValue(param, "sort");
        if (sort != null && !sort.isEmpty()) {
            orderBy += "order by " + sort;
            sortType = getKeyValue(param, "sort_type");
            if (sortType != null && !sortType.isEmpty() && sortTypeList.contains(sortType.toLowerCase())) {
                orderBy += " " + sortType;
            }
        }

        List<Map<String, Object>> jobData = jobDataDao.getJobData(jobTitle, salaryEq, salaryGte, salaryLte, gender, fields, orderBy);
        Map<String, Object> result = new HashMap<>();
        result.put("job_data", jobData);
        result.put("job_count", jobData.size());

        return result;
    }

    private String getKeyValue(Map<String, Object> param, String key) {
        Object value = param.get(key);
        return value != null ? value.toString().trim() : null;
    }

    public BigDecimal cleanSalary(String salary) {
        if (salary != null && !salary.isEmpty()) {
            salary = salary.replaceAll(",", "");
            if (!salary.matches("^\\d+(\\.\\d+)?$")) {
                throw new IllegalArgumentException("Input contains invalid characters (only digits, comma, and 1 decimal point are allowed)");
            }
        }
        else {
            return null;
        }
        return new BigDecimal(salary);
    }
}
