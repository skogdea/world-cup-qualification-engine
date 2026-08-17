package com.staticoyster.worldcupqualificationengine.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;

class InMemoryMatchRepositoryTest {

	private InMemoryMatchRepository repository;

	@BeforeEach
	void setUp() {
		repository = new InMemoryMatchRepository();
	}

	@Test
	void saveReKeysSameInstanceAndDropsStaleId() {
		Match match = Match.Builder.newBuilder()
				.withMatchId("seed-1")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(0)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.build();
		repository.save(match);

		match.setMatchId("400021443");
		match.setHomeScore(2);
		repository.save(match);

		assertTrue(repository.findById("400021443").isPresent());
		assertTrue(repository.findById("seed-1").isEmpty());
		assertEquals(1, repository.findAll().size());
		assertEquals(1, repository.findByGroupAndStatus(Group.A, MatchStatus.PAST).size());
		assertEquals("400021443", repository.findAll().get(0).getMatchId());
	}

}
