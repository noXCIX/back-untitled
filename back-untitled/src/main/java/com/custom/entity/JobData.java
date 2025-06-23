package com.custom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "job_data")
public class JobData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private String timestamp;
    private String employer;
    private String location;
    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "years_at_employer")
    private String yearsAtEmployer;

    @Column(name = "years_of_experience")
    private String yearsOfExperience;

    private String salary;

    @Column(name = "signing_bonus")
    private String signingBonus;

    @Column(name = "annual_bonus")
    private String annualBonus;

    @Column(name = "annual_stock_value")
    private String annualStockValue;

    private String gender;

    @Column(name = "additional_comments")
    private String additionalComments;
}
