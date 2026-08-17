package com.staticoyster.worldcupqualificationengine.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;

/**
 * Dev bootstrap: prefer live FIFA; when FIFA is unavailable, load seed via the manual adapter → MatchService.
 * Never reload seed after a partial live import — that would overwrite fixtures already updated from FIFA.
 */
@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class SeedDataBootstrap implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(SeedDataBootstrap.class);

	private final FifaMatchAndCardsClient fifaMatchAndCardsClient;
	private final FifaSeedMatchImporter seedMatchImporter;
	private final MatchRepository matchRepository;

	public SeedDataBootstrap(
			FifaMatchAndCardsClient fifaMatchAndCardsClient,
			FifaSeedMatchImporter seedMatchImporter,
			MatchRepository matchRepository) {
		this.fifaMatchAndCardsClient = fifaMatchAndCardsClient;
		this.seedMatchImporter = seedMatchImporter;
		this.matchRepository = matchRepository;
	}

	@Override
	public void run(ApplicationArguments args) {
		try {
			int imported = fifaMatchAndCardsClient.importFirstStageResults();
			log.info("Imported {} matches from live FIFA", imported);
		}
		catch (IllegalStateException exception) {
			if (!matchRepository.findAll().isEmpty()) {
				log.warn(
						"Live FIFA import failed after persisting some matches ({}); "
								+ "keeping live data, skipping seed fallback",
						exception.getMessage());
				return;
			}
			log.warn("FIFA unavailable ({}); falling back to seed via manual adapter", exception.getMessage());
			int imported = seedMatchImporter.importDefaultSeed();
			log.info("Imported {} matches from FIFA seed data", imported);
		}
	}

}
