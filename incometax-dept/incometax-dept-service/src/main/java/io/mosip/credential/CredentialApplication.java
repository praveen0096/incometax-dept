package io.mosip.credential;


import io.mosip.credential.service.impl.CbeffImpl;
import io.mosip.credential.spi.CbeffUtil;
import io.mosip.vercred.CredentialsVerifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@ComponentScan(basePackages = {"io.mosip.credential", "io.mosip.kernel"})
@ComponentScan("io.mosip.credential.util")
@EnableJpaRepositories(basePackages = "io.mosip.credential.repository")
@EntityScan(basePackages = "io.mosip.credential.entity")
@EnableScheduling
@SpringBootApplication(scanBasePackages = { "io.mosip.credential.*", "${mosip.auth.adapter.impl.basepackage}"  })
public class CredentialApplication {
	@Bean
	@Primary
	public CbeffUtil getCbeffUtil() {
		return new CbeffImpl();
	}
	@Bean
	public CredentialsVerifier credentialsVerifier() {
		return new CredentialsVerifier();
	}
	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		return threadPoolTaskScheduler;
	}

	public static void main(String[] args) {
		SpringApplication.run(CredentialApplication.class, args);
	}

}
