package com.staticoyster.worldcupqualificationengine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.staticoyster.worldcupqualificationengine.domain.constants.QualificationConstants;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableStandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.QualificationDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.service.config.DomainDtoConverter;

class QualificationCalculatorTest {

	private static final Team[][] TEAMS_BY_GROUP = {
			{Team.MEXICO, Team.SOUTH_AFRICA, Team.KOREA_REPUBLIC, Team.CZECHIA},
			{Team.CANADA, Team.BOSNIA_AND_HERZEGOVINA, Team.QATAR, Team.SWITZERLAND},
			{Team.BRAZIL, Team.MOROCCO, Team.HAITI, Team.SCOTLAND},
			{Team.USA, Team.PARAGUAY, Team.AUSTRALIA, Team.TURKIYE},
			{Team.GERMANY, Team.CURACAO, Team.COTE_DIVOIRE, Team.ECUADOR},
			{Team.NETHERLANDS, Team.JAPAN, Team.SWEDEN, Team.TUNISIA},
			{Team.BELGIUM, Team.EGYPT, Team.IR_IRAN, Team.NEW_ZEALAND},
			{Team.SPAIN, Team.CABO_VERDE, Team.SAUDI_ARABIA, Team.URUGUAY},
			{Team.FRANCE, Team.SENEGAL, Team.IRAQ, Team.NORWAY},
			{Team.ARGENTINA, Team.ALGERIA, Team.AUSTRIA, Team.JORDAN},
			{Team.PORTUGAL, Team.CONGO_DR, Team.UZBEKISTAN, Team.COLOMBIA},
			{Team.ENGLAND, Team.CROATIA, Team.GHANA, Team.PANAMA}
	};

	private GroupStageStandingsService groupStageStandingsService;
	private QualificationCalculator qualificationCalculator;

	@BeforeEach
	void setUp() {
		groupStageStandingsService = mock(GroupStageStandingsService.class);
		FifaWorldRankingService fifaWorldRankingService = mock(FifaWorldRankingService.class);
		when(fifaWorldRankingService.compare(any(), any())).thenAnswer(invocation -> {
			Team left = invocation.getArgument(0);
			Team right = invocation.getArgument(1);
			int leftRank = left == Team.AUSTRIA ? 1 : 2;
			int rightRank = right == Team.AUSTRIA ? 1 : 2;
			return Integer.compare(leftRank, rightRank);
		});
		qualificationCalculator = new QualificationCalculator(
				groupStageStandingsService,
				new DomainDtoConverter(),
				fifaWorldRankingService);
	}

	@Test
	void takesFirstAndSecondFromEachGroupAsWinnersAndRunnersUp() {
		stubStandings(fullTournamentThirdPoints(4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4));

		QualificationDto qualification = qualificationCalculator.calculateQualification();

		assertEquals(List.of(
				Team.MEXICO, Team.CANADA, Team.BRAZIL, Team.USA, Team.GERMANY, Team.NETHERLANDS,
				Team.BELGIUM, Team.SPAIN, Team.FRANCE, Team.ARGENTINA, Team.PORTUGAL, Team.ENGLAND),
				qualification.getGroupWinners());
		assertEquals(List.of(
				Team.SOUTH_AFRICA, Team.BOSNIA_AND_HERZEGOVINA, Team.MOROCCO, Team.PARAGUAY,
				Team.CURACAO, Team.JAPAN, Team.EGYPT, Team.CABO_VERDE, Team.SENEGAL, Team.ALGERIA,
				Team.CONGO_DR, Team.CROATIA),
				qualification.getRunnersUp());
	}

	@Test
	void advancesOnlyTheEightBestThirdPlacedTeams() {
		stubStandings(fullTournamentThirdPoints(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));

		QualificationDto qualification = qualificationCalculator.calculateQualification();
		List<Team> bestThirds = qualification.getBestThirdPlaceStandings().stream()
				.map(StandingDto::getTeam)
				.toList();

		assertEquals(QualificationConstants.BEST_THIRD_PLACE_SLOTS, bestThirds.size());
		assertEquals(List.of(
				Team.KOREA_REPUBLIC, Team.QATAR, Team.HAITI, Team.AUSTRALIA,
				Team.COTE_DIVOIRE, Team.SWEDEN, Team.IR_IRAN, Team.SAUDI_ARABIA),
				bestThirds);
		assertFalse(bestThirds.contains(Team.IRAQ));
		assertFalse(bestThirds.contains(Team.AUSTRIA));
		assertFalse(bestThirds.contains(Team.UZBEKISTAN));
		assertFalse(bestThirds.contains(Team.GHANA));
		assertEquals(32, qualification.getQualifiedTeams().size());
	}

	@Test
	void ranksAllThirdsNotOnlyTheAdvancingEight() {
		stubStandings(fullTournamentThirdPoints(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));

		List<Team> ranked = qualificationCalculator.getRankedThirdPlaceStandings().stream()
				.map(StandingDto::getTeam)
				.toList();

		assertEquals(12, ranked.size());
		assertEquals(Team.KOREA_REPUBLIC, ranked.get(0));
		assertEquals(Team.GHANA, ranked.get(11));
	}

	@Test
	void ranksThirdsByPointsBeforeOtherCriteria() {
		stubStandings(twoGroupTables(
				third(Team.KOREA_REPUBLIC, 6, 0, 3, 0),
				third(Team.QATAR, 4, 5, 8, 0)));
		assertEquals(Team.KOREA_REPUBLIC, firstRankedThird());
	}

	@Test
	void ranksThirdsByGoalDifferenceWhenPointsAreEqual() {
		stubStandings(twoGroupTables(
				third(Team.KOREA_REPUBLIC, 4, 3, 3, 0),
				third(Team.QATAR, 4, 1, 8, 0)));
		assertEquals(Team.KOREA_REPUBLIC, firstRankedThird());
	}

	@Test
	void ranksThirdsByGoalsForWhenPointsAndGoalDifferenceAreEqual() {
		stubStandings(twoGroupTables(
				third(Team.KOREA_REPUBLIC, 4, 2, 6, 0),
				third(Team.QATAR, 4, 2, 4, 0)));
		assertEquals(Team.KOREA_REPUBLIC, firstRankedThird());
	}

	@Test
	void ranksThirdsByTeamConductWhenScoringIsEqual() {
		stubStandings(twoGroupTables(
				third(Team.KOREA_REPUBLIC, 4, 2, 4, 0),
				third(Team.QATAR, 4, 2, 4, -3)));
		assertEquals(Team.KOREA_REPUBLIC, firstRankedThird());
	}

	@Test
	void ranksThirdsByFifaRankingWhenAllOtherCriteriaAreEqual() {
		stubStandings(twoGroupTables(
				third(Team.AUSTRIA, 4, 2, 4, 0),
				third(Team.KOREA_REPUBLIC, 4, 2, 4, 0)));
		assertEquals(Team.AUSTRIA, firstRankedThird());
	}

	@Test
	void skipsMissingPlacesWhenAGroupTableIsShort() {
		Map<Group, List<StandingDto>> standings = new EnumMap<>(Group.class);
		standings.put(Group.A, List.of());
		standings.put(Group.B, List.of(standing(Team.CANADA, 3, 1, 2, 0)));
		standings.put(Group.C, List.of(
				standing(Team.BRAZIL, 6, 4, 5, 0),
				standing(Team.MOROCCO, 3, 0, 2, 0)));
		stubStandings(standings);

		QualificationDto qualification = qualificationCalculator.calculateQualification();

		assertEquals(List.of(Team.CANADA, Team.BRAZIL), qualification.getGroupWinners());
		assertEquals(List.of(Team.MOROCCO), qualification.getRunnersUp());
		assertTrue(qualification.getBestThirdPlaceStandings().isEmpty());
		assertEquals(List.of(Team.CANADA, Team.BRAZIL, Team.MOROCCO), qualification.getQualifiedTeams());
	}

	private Team firstRankedThird() {
		return qualificationCalculator.getRankedThirdPlaceStandings().get(0).getTeam();
	}

	private void stubStandings(Map<Group, List<StandingDto>> standings) {
		when(groupStageStandingsService.calculateAllGroupStandingsDto()).thenReturn(standings);
	}

	private static Map<Group, List<StandingDto>> fullTournamentThirdPoints(int... thirdPoints) {
		Map<Group, List<StandingDto>> standings = new EnumMap<>(Group.class);
		Group[] groups = Group.values();
		for (int i = 0; i < groups.length; i++) {
			Team[] teams = TEAMS_BY_GROUP[i];
			standings.put(groups[i], List.of(
					standing(teams[0], 9, 5, 7, 0),
					standing(teams[1], 6, 2, 5, 0),
					third(teams[2], thirdPoints[i], 0, 3, 0),
					standing(teams[3], 0, -7, 1, 0)));
		}
		return standings;
	}

	private static Map<Group, List<StandingDto>> twoGroupTables(StandingDto thirdA, StandingDto thirdB) {
		Map<Group, List<StandingDto>> standings = new EnumMap<>(Group.class);
		standings.put(thirdA.getGroup(), tableWithThird(thirdA));
		standings.put(thirdB.getGroup(), tableWithThird(thirdB));
		return standings;
	}

	private static List<StandingDto> tableWithThird(StandingDto third) {
		Team[] teams = TEAMS_BY_GROUP[third.getGroup().ordinal()];
		return List.of(
				standing(teams[0], 9, 5, 7, 0),
				standing(teams[1], 6, 2, 5, 0),
				third,
				standing(teams[3], 0, -7, 1, 0));
	}

	private static StandingDto third(Team team, int points, int goalDifference, int goalsFor, int teamConductScore) {
		return standing(team, points, goalDifference, goalsFor, teamConductScore);
	}

	private static StandingDto standing(
			Team team,
			int points,
			int goalDifference,
			int goalsFor,
			int teamConductScore) {
		return ImmutableStandingDto.builder()
				.group(team.getGroup())
				.team(team)
				.played(3)
				.won(0)
				.drawn(0)
				.lost(0)
				.goalsFor(goalsFor)
				.goalsAgainst(goalsFor - goalDifference)
				.goalDifference(goalDifference)
				.teamConductScore(teamConductScore)
				.points(points)
				.build();
	}

}
