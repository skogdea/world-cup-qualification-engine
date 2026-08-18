package com.staticoyster.worldcupqualificationengine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.model.TeamMatchStats;
import com.staticoyster.worldcupqualificationengine.repository.InMemoryMatchRepository;
import com.staticoyster.worldcupqualificationengine.service.config.DomainDtoConverter;

import tools.jackson.databind.json.JsonMapper;

class StandingCalculatorTest {

	private InMemoryMatchRepository matchRepository;
	private StandingCalculator standingCalculator;

	@BeforeEach
	void setUp() {
		matchRepository = new InMemoryMatchRepository();
		standingCalculator = new StandingCalculator(
				matchRepository,
				new DomainDtoConverter(),
				new FifaWorldRankingService(JsonMapper.builder().build()));
	}

	@Test
	void doesNotDoubleCountWhenSavedMatchIsReKeyed() {
		Match match = Match.Builder.newBuilder()
				.withMatchId("seed-1")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(2)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.build();
		matchRepository.save(match);
		match.setMatchId("400021443");
		matchRepository.save(match);

		Map<Team, StandingDto> byTeam = standingsByTeam(Group.A);

		assertEquals(1, byTeam.get(Team.MEXICO).getPlayed());
		assertEquals(3, byTeam.get(Team.MEXICO).getPoints());
		assertEquals(1, byTeam.get(Team.SOUTH_AFRICA).getPlayed());
		assertEquals(0, byTeam.get(Team.SOUTH_AFRICA).getPoints());
	}

	@Test
	void accumulatesTeamConductScoreFromMatchHomeAndAwayStats() {
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-1")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(1)
				.withAwayScore(1)
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(stats(1, 0, 0)) // fair play -1
				.withAwayStats(stats(0, 0, 1)) // fair play -4
				.build());

		Map<Team, StandingDto> byTeam = standingsByTeam(Group.A);

		assertEquals(-1, byTeam.get(Team.MEXICO).getTeamConductScore());
		assertEquals(-4, byTeam.get(Team.SOUTH_AFRICA).getTeamConductScore());
		assertEquals(0, byTeam.get(Team.KOREA_REPUBLIC).getTeamConductScore());
		assertEquals(0, byTeam.get(Team.CZECHIA).getTeamConductScore());
	}

	@Test
	void treatsMissingMatchStatsAsZeroTeamConductContribution() {
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-2")
				.withHome(Team.MEXICO)
				.withAway(Team.KOREA_REPUBLIC)
				.withHomeScore(2)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.build());

		Map<Team, StandingDto> byTeam = standingsByTeam(Group.A);

		assertEquals(0, byTeam.get(Team.MEXICO).getTeamConductScore());
		assertEquals(0, byTeam.get(Team.KOREA_REPUBLIC).getTeamConductScore());
	}

	@Test
	void usesHigherTeamConductScoreAsTieBreakerWhenOtherCriteriaEqual() {
		// Two teams, one match each against the same opponents with identical scorelines,
		// differing only in cards → same Pts/GD/GF/H2H, TCS decides order.
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-mex-rsa")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(1)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(stats(2, 0, 0)) // TCS -2
				.withAwayStats(stats(0, 0, 0))
				.build());
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-cze-rsa")
				.withHome(Team.CZECHIA)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(1)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(stats(0, 0, 0)) // TCS 0
				.withAwayStats(stats(0, 0, 0))
				.build());

		List<StandingDto> standings = standingCalculator.calculateStandingsDtoInCurrentGroup(Group.A);

		int mexicoIndex = indexOf(standings, Team.MEXICO);
		int czechiaIndex = indexOf(standings, Team.CZECHIA);
		assertTrue(czechiaIndex < mexicoIndex, "better TCS (0 > -2) should rank Czechia above Mexico");
		assertEquals(0, standings.get(czechiaIndex).getTeamConductScore());
		assertEquals(-2, standings.get(mexicoIndex).getTeamConductScore());
	}

	private Map<Team, StandingDto> standingsByTeam(Group group) {
		return standingCalculator.calculateStandingsDtoInCurrentGroup(group).stream()
				.collect(Collectors.toMap(StandingDto::getTeam, Function.identity()));
	}

	private static int indexOf(List<StandingDto> standings, Team team) {
		for (int i = 0; i < standings.size(); i++) {
			if (standings.get(i).getTeam() == team) {
				return i;
			}
		}
		throw new AssertionError("team not found: " + team);
	}

	private static TeamMatchStats stats(int yellowCards, int secondYellowReds, int directReds) {
		return TeamMatchStats.Builder.newBuilder()
				.withYellowCards(yellowCards)
				.withSecondYellowReds(secondYellowReds)
				.withDirectReds(directReds)
				.build();
	}

}
