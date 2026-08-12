package com.staticoyster.worldcupqualificationengine.ingestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.repository.InMemoryMatchRepository;

class SeedDataBootstrapTest {

	@Test
	void keepsLiveDataAndSkipsSeedWhenLiveImportFailsAfterPersisting() {
		InMemoryMatchRepository repository = new InMemoryMatchRepository();
		repository.save(Match.Builder.newBuilder()
				.withMatchId("400021443")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(2)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.build());

		FifaMatchAndCardsClient failingLive = mock(FifaMatchAndCardsClient.class);
		when(failingLive.importFirstStageResults())
				.thenThrow(new IllegalStateException("later match failed"));

		FifaSeedMatchImporter seedImporter = mock(FifaSeedMatchImporter.class);

		new SeedDataBootstrap(failingLive, seedImporter, repository)
				.run(new DefaultApplicationArguments());

		verify(seedImporter, never()).importDefaultSeed();
		assertTrue(repository.findById("400021443").isPresent());
		assertEquals(1, repository.findAll().size());
	}

	@Test
	void fallsBackToSeedWhenLiveImportFailsWithEmptyRepository() {
		InMemoryMatchRepository repository = new InMemoryMatchRepository();

		FifaMatchAndCardsClient failingLive = mock(FifaMatchAndCardsClient.class);
		when(failingLive.importFirstStageResults())
				.thenThrow(new IllegalStateException("FIFA calendar HTTP request failed"));

		FifaSeedMatchImporter seedImporter = mock(FifaSeedMatchImporter.class);
		when(seedImporter.importDefaultSeed()).thenReturn(72);

		new SeedDataBootstrap(failingLive, seedImporter, repository)
				.run(new DefaultApplicationArguments());

		verify(seedImporter).importDefaultSeed();
	}

}
