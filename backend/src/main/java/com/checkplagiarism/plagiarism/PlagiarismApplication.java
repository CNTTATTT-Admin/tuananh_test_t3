package com.checkplagiarism.plagiarism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlagiarismApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlagiarismApplication.class, args);
	}

}
