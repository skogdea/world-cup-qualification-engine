package com.staticoyster.worldcupqualificationengine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.staticoyster.worldcupqualificationengine.domain.constants.QualificationConstants;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableStandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamStatusDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus;
import com.staticoyster.worldcupqualificationengine.service.config.DomainDtoConverter;

class TeamStatusServiceTest {

	private GroupStageStandingsService groupStageStandingsService;
	private RoundOf32Service roundOf32Service;
	private TeamStatusService teamStatusService;

	@BeforeEach
	void setUp() {
		groupStageStandingsService = mock(GroupStageStandingsService.class);
		roundOf32Service = mock(RoundOf32Service.class);
		teamStatusService = new TeamStatusService(
				groupStageStandingsService,
				roundOf32Service,
				new DomainDtoConverter());
		when(groupStageStandingsService.getGroupStandingsDtoInCurrentGroup(Group.G))
				.thenReturn(groupGStandings());
	}

	@Test
	void marksGroupWinnerQualified() {
		TeamStatusDto status = teamStatusService.getTeamStatus(Team.BELGIUM);

		assertEquals(TeamStatus.QUALIFIED, status.getTeamStatus());
		assertEquals(1, status.getCurrentRank());
		assertNull(status.getBestThirdPlaceRank());
	}

	@Test
	void marksRunnerUpQualified() {
		TeamStatusDto status = teamStatusService.getTeamStatus(Team.EGYPT);

		assertEquals(TeamStatus.QUALIFIED, status.getTeamStatus());
		assertEquals(2, status.getCurrentRank());
		assertNull(status.getBestThirdPlaceRank());
	}

	@Test
	void marksFourthPlacedTeamEliminated() {
		TeamStatusDto status = teamStatusService.getTeamStatus(Team.NEW_ZEALAND);

		assertEquals(TeamStatus.ELIMINATED, status.getTeamStatus());
		assertEquals(4, status.getCurrentRank());
		assertNull(status.getBestThirdPlaceRank());
	}

	@Test
	void marksThirdStillAliveWhenInsideBestEightIncludingLastSlot() {
		when(roundOf32Service.getRankedThirdPlaceStandingsDtos())
				.thenReturn(rankedThirdsWithIranAt(QualificationConstants.BEST_THIRD_PLACE_SLOTS));

		TeamStatusDto status = teamStatusService.getTeamStatus(Team.IR_IRAN);

		assertEquals(TeamStatus.STILL_ALIVE, status.getTeamStatus());
		assertEquals(3, status.getCurrentRank());
		assertEquals(QualificationConstants.BEST_THIRD_PLACE_SLOTS, status.getBestThirdPlaceRank());
	}

	@Test
	void marksThirdEliminatedWhenOutsideBestEight() {
		when(roundOf32Service.getRankedThirdPlaceStandingsDtos())
				.thenReturn(rankedThirdsWithIranAt(QualificationConstants.BEST_THIRD_PLACE_SLOTS + 1));

		TeamStatusDto status = teamStatusService.getTeamStatus(Team.IR_IRAN);

		assertEquals(TeamStatus.ELIMINATED, status.getTeamStatus());
		assertEquals(3, status.getCurrentRank());
		assertNull(status.getBestThirdPlaceRank());
	}

	@Test
	void throwsWhenTeamIsMissingFromGroupStandings() {
		when(groupStageStandingsService.getGroupStandingsDtoInCurrentGroup(Group.G))
				.thenReturn(List.of(standing(Team.BELGIUM, 9)));

		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> teamStatusService.getTeamStatus(Team.IR_IRAN));
		assertEquals("No standings row for team: IRN", exception.getMessage());
	}

	private static List<StandingDto> groupGStandings() {
		return List.of(
				standing(Team.BELGIUM, 9),
				standing(Team.EGYPT, 6),
				standing(Team.IR_IRAN, 4),
				standing(Team.NEW_ZEALAND, 0));
	}

	private static List<StandingDto> rankedThirdsWithIranAt(int oneBasedRank) {
		Team[] ahead = {
				Team.KOREA_REPUBLIC, Team.QATAR, Team.HAITI, Team.AUSTRALIA,
				Team.COTE_DIVOIRE, Team.SWEDEN, Team.SAUDI_ARABIA, Team.IRAQ,
				Team.AUSTRIA, Team.UZBEKISTAN, Team.GHANA
		};
		List<StandingDto> ranked = new ArrayList<>();
		for (int i = 0; i < oneBasedRank - 1; i++) {
			ranked.add(standing(ahead[i], 4));
		}
		ranked.add(standing(Team.IR_IRAN, 4));
		return ranked;
	}

	private static StandingDto standing(Team team, int points) {
		return ImmutableStandingDto.builder()
				.group(team.getGroup())
				.team(team)
				.played(3)
				.won(0)
				.drawn(0)
				.lost(0)
				.goalsFor(3)
				.goalsAgainst(3)
				.goalDifference(0)
				.teamConductScore(0)
				.points(points)
				.build();
	}

}
