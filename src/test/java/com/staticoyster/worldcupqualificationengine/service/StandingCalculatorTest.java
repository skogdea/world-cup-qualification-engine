package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.model.TeamMatchStats;
import com.staticoyster.worldcupqualificationengine.repository.InMemoryMatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandingCalculatorTest {

	private InMemoryMatchRepository matchRepository;
	private StandingCalculator standingCalculator;

	@BeforeEach
	void setUp() {
		matchRepository = new InMemoryMatchRepository();
		DomainDtoConverter converter = new DomainDtoConverter();
		FifaWorldRankingService rankingService = new FifaWorldRankingService(JsonMapper.builder().build());
		standingCalculator = new StandingCalculator(matchRepository, converter, rankingService);
	}

	@Test
	void accumulatesTeamConductScoreFromMatchStats() {
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-1")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(2)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(stats(1, 0, 1)) // fair-play -(1 + 4) = -5
				.withAwayStats(stats(2, 0, 2)) // fair-play -(2 + 8) = -10
				.build());

		Map<Team, StandingDto> byTeam = standingsByTeam(Group.A);

		assertEquals(-5, byTeam.get(Team.MEXICO).getTeamConductScore());
		assertEquals(-10, byTeam.get(Team.SOUTH_AFRICA).getTeamConductScore());
		assertEquals(0, byTeam.get(Team.KOREA_REPUBLIC).getTeamConductScore());
	}

	@Test
	void nullMatchStatsLeaveTeamConductScoreAtZero() {
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-2")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(1)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.build());

		Map<Team, StandingDto> byTeam = standingsByTeam(Group.A);

		assertEquals(0, byTeam.get(Team.MEXICO).getTeamConductScore());
		assertEquals(0, byTeam.get(Team.SOUTH_AFRICA).getTeamConductScore());
	}

	@Test
	void betterTeamConductScoreBreaksOtherwiseEqualTie() {
		// Drawn H2H with identical GF/GD/points; only TCS differs.
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-3")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(1)
				.withAwayScore(1)
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(stats(0, 0, 0)) // TCS 0
				.withAwayStats(stats(1, 0, 0)) // TCS -1
				.build());

		List<StandingDto> standings = standingCalculator.calculateStandingsDtoInCurrentGroup(Group.A);

		int mexicoIndex = indexOf(standings, Team.MEXICO);
		int southAfricaIndex = indexOf(standings, Team.SOUTH_AFRICA);
		assertTrue(mexicoIndex < southAfricaIndex,
				"Mexico (better TCS) should rank ahead of South Africa");
		assertEquals(0, standings.get(mexicoIndex).getTeamConductScore());
		assertEquals(-1, standings.get(southAfricaIndex).getTeamConductScore());
	}

	@Test
	void sumsTeamConductScoreAcrossMultipleMatches() {
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-4")
				.withHome(Team.MEXICO)
				.withAway(Team.SOUTH_AFRICA)
				.withHomeScore(1)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(stats(1, 0, 0)) // -1
				.withAwayStats(stats(0, 0, 0))
				.build());
		matchRepository.save(Match.Builder.newBuilder()
				.withMatchId("g-a-5")
				.withHome(Team.KOREA_REPUBLIC)
				.withAway(Team.MEXICO)
				.withHomeScore(0)
				.withAwayScore(0)
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(stats(0, 0, 0))
				.withAwayStats(stats(0, 1, 0)) // -3
				.build());

		Map<Team, StandingDto> byTeam = standingsByTeam(Group.A);

		assertEquals(-4, byTeam.get(Team.MEXICO).getTeamConductScore());
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
		throw new AssertionError("Team not found in standings: " + team);
	}

	private static TeamMatchStats stats(int yellowCards, int secondYellowReds, int directReds) {
		return TeamMatchStats.Builder.newBuilder()
				.withYellowCards(yellowCards)
				.withSecondYellowReds(secondYellowReds)
				.withDirectReds(directReds)
				.build();
	}

}
