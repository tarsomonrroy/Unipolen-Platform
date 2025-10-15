package com.unipolen.webserver;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class R2dbcConfig extends AbstractR2dbcConfiguration  {

	@Override
	@Bean
	@Primary
	public ConnectionFactory connectionFactory() {
		return ConnectionFactoryBuilder.withOptions(
				ConnectionFactoryOptions.builder()
						.option(DRIVER, "postgresql")
						.option(HOST, "localhost")
						.option(PORT, 5432)
						.option(USER, "postgres")
						.option(PASSWORD, "admin")
						.option(DATABASE, "unipolen")).build();
	}

	@Bean
	ConnectionFactoryInitializer initializer(@Autowired ConnectionFactory connectionFactory) {

		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

		return initializer;
	}

	@Bean
	@Scope("prototype")
	public DatabaseClient r2dbcDatabaseClient(@Autowired ConnectionFactory connectionFactory) {
		return DatabaseClient.create(connectionFactory);
	}

	@Bean
	@Scope("prototype")
	public R2dbcEntityTemplate r2dbcEntityTemplate(@Autowired ConnectionFactory connectionFactory) {
		return new R2dbcEntityTemplate(connectionFactory);
	}

}
