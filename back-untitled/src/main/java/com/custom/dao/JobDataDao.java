package com.custom.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JobDataDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<String> getJobDataFields() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from job_data limit 0", new HashMap<>());

        SqlRowSetMetaData meta = rowSet.getMetaData();

        List<String> result = new ArrayList<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            result.add(meta.getColumnName(i).toLowerCase());
        }

        return result;
    }

    public List<Map<String, Object>> getJobData(String jobTitle, BigDecimal salaryEq, BigDecimal salaryGte, BigDecimal salaryLte, String gender, String fields, String orderBy) {
        Map<String, Object> param = new HashMap<>();
        param.put("jobTitle", jobTitle);
        param.put("salary", salaryEq);
        param.put("salaryGte", salaryGte);
        param.put("salaryLte", salaryLte);
        param.put("gender", gender);

        StringBuilder sql1 = new StringBuilder();
        sql1.append(" select ");
        if (fields != null && !fields.isEmpty()) {
            sql1.append(fields);
        }
        else {
            sql1.append(" * ");
        }
        sql1.append(" from job_data jd ");
        sql1.append(" where 1=1 ");
        if (jobTitle != null && !jobTitle.isEmpty()) {
            sql1.append(" and upper(jd.job_title) like '%' || upper(:jobTitle) || '%' ");
        }
        if (gender != null && !gender.isEmpty()) {
            sql1.append(" and upper(jd.gender) like '%' || upper(:gender) || '%' ");
        }
        if (salaryEq != null || salaryGte != null || salaryLte != null) {
            sql1.append(" and replace(jd.salary, ',', '') regexp '^[0-9]+(\\.[0-9]+)?$' ");
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            sql1.append(orderBy);
        }

        if (salaryEq != null || salaryGte != null || salaryLte != null) {
            StringBuilder sql2 = new StringBuilder();
            sql2.append(" select ");
            if (fields != null && !fields.isEmpty()) {
                sql2.append(fields);
            }
            else {
                sql2.append(" * ");
            }
            sql2.append(" from ( ");
            sql2.append(sql1);
            sql2.append(" ) jd ");
            sql2.append(" where 1=1 ");
            if (salaryEq != null) {
                sql2.append(" and cast(replace(jd.salary, ',', '') as decimal) = :salary ");
            }
            if (salaryGte != null) {
                sql2.append(" and cast(replace(jd.salary, ',', '') as decimal) >= :salaryGte ");
            }
            if (salaryLte != null) {
                sql2.append(" and cast(replace(jd.salary, ',', '') as decimal) <= :salaryLte ");
            }
            return jdbcTemplate.queryForList(sql2.toString(), param);
        }

        return jdbcTemplate.queryForList(sql1.toString(), param);
    }
}
