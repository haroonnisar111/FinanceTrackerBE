package com.budgetbuddy.personal_finance_tracker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@EnableJpaRepositories("com.budgetbuddy.personal_finance_tracker.repository")
@EntityScan("com.budgetbuddy.personal_finance_tracker.entity")
public class PersonalFinanceTrackerApplicationTests {
	// Your test methods (or empty class for context loading)
}