package com.staticoyster.worldcupqualificationengine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableMatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.repository.InMemoryMatchRepository;
import com.staticoyster.worldcupqualificationengine.service.config.DomainDtoConverter;

class MatchServiceTest {

	private InMemoryMatchRepository matchRepository;
	private MatchService matchService;

	@BeforeEach
	void setUp() {
		matchRepository = new InMemoryMatchRepository();
		matchService = new MatchService(matchRepository, new DomainDtoConverter());
	}

	@Test
	void createsMatchWhenNeitherIdNorFixtureExists() {
		MatchDto saved = matchService.updateMatchResult(result("new-1", Team.MEXICO, Team.SOUTH_AFRICA, 2, 0));

		assertEquals("new-1", saved.getMatchId());
		assertEquals(2, saved.getHomeScore());
		assertTrue(matchRepository.findById("new-1").isPresent());
	}

	@Test
	void updatesExistingMatchByMatchId() {
		matchRepository.save(match("id-1", Team.MEXICO, Team.SOUTH_AFRICA, 0, 0));

		MatchDto saved = matchService.updateMatchResult(result("id-1", Team.MEXICO, Team.SOUTH_AFRICA, 3, 1));

		assertEquals(3, saved.getHomeScore());
		assertEquals(1, saved.getAwayScore());
		assertEquals(1, matchRepository.findAll().size());
	}

	@Test
	void updatesExistingFixtureWhenMatchIdChanges() {
		matchRepository.save(match("seed-1", Team.MEXICO, Team.SOUTH_AFRICA, 0, 0));

		MatchDto saved = matchService.updateMatchResult(result("400021443", Team.MEXICO, Team.SOUTH_AFRICA, 2, 0));

		assertEquals("400021443", saved.getMatchId());
		assertEquals(2, saved.getHomeScore());
		assertTrue(matchRepository.findById("400021443").isPresent());
		assertTrue(matchRepository.findById("seed-1").isEmpty());
		assertEquals(1, matchRepository.findAll().size());
		assertTrue(matchRepository.findByHomeAndAway(Team.MEXICO, Team.SOUTH_AFRICA).isPresent());
		assertEquals(2, matchRepository.findByHomeAndAway(Team.MEXICO, Team.SOUTH_AFRICA).get().getHomeScore());
	}

	private static Match match(String matchId, Team home, Team away, int homeScore, int awayScore) {
		return Match.Builder.newBuilder()
				.withMatchId(matchId)
				.withHome(home)
				.withAway(away)
				.withHomeScore(homeScore)
				.withAwayScore(awayScore)
				.withMatchStatus(MatchStatus.PAST)
				.build();
	}

	private static MatchDto result(String matchId, Team home, Team away, int homeScore, int awayScore) {
		return ImmutableMatchDto.builder()
				.matchId(matchId)
				.home(home)
				.away(away)
				.homeScore(homeScore)
				.awayScore(awayScore)
				.matchStatus(MatchStatus.PAST)
				.build();
	}

}
