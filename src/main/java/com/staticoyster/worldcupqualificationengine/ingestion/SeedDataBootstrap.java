package com.staticoyster.worldcupqualificationengine.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class SeedDataBootstrap implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(SeedDataBootstrap.class);

	private final FifaSeedMatchImporter seedMatchImporter;

	public SeedDataBootstrap(FifaSeedMatchImporter seedMatchImporter) {
		this.seedMatchImporter = seedMatchImporter;
	}

	@Override
	public void run(ApplicationArguments args) {
		int imported = seedMatchImporter.importDefaultSeed();
		log.info("Imported {} matches from FIFA seed data", imported);
	}

}
