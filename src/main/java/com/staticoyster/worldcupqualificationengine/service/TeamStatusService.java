package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.constants.QualificationConstants;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamStatusDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus;
import com.staticoyster.worldcupqualificationengine.domain.model.TeamStatusModel;
import com.staticoyster.worldcupqualificationengine.service.api.DomainDtoConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamStatusService {

	private final GroupStageStandingsService groupStageStandingsService;
	private final RoundOf32Service roundOf32Service;
	private final DomainDtoConverter domainDtoConverter;

	public TeamStatusService(
			GroupStageStandingsService groupStageStandingsService,
			RoundOf32Service roundOf32Service,
			DomainDtoConverter domainDtoConverter) {
		this.groupStageStandingsService = groupStageStandingsService;
		this.roundOf32Service = roundOf32Service;
		this.domainDtoConverter = domainDtoConverter;
	}

	/**
	 * Group-stage status from current standings ({@link StandingDto} stays the stats source).
	 * Rank 1–2 → QUALIFIED; rank 4 → ELIMINATED; otherwise (typically 3rd) → STILL_ALIVE.
	 */
	public TeamStatusDto getTeamStatus(Team team) {
		List<StandingDto> groupStandings =
				groupStageStandingsService.getGroupStandingsDtoInCurrentGroup(team.getGroup());

		int currentRank = -1;
		StandingDto standing = null;
		for (int i = 0; i < groupStandings.size(); i++) {
			if (groupStandings.get(i).getTeam() == team) {
				currentRank = i + 1;
				standing = groupStandings.get(i);
				break;
			}
		}
		if (standing == null) {
			throw new IllegalStateException("No standings row for team: " + team.getCode());
		}

		TeamStatusModel model = TeamStatusModel.Builder.newBuilder()
				.withGroup(standing.getGroup())
				.withTeam(standing.getTeam())
				.withCurrentRank(currentRank)
				.withBestThirdPlaceSlot(resolveBestThirdPlaceSlot(team, currentRank))
				.withTeamStatus(resolveStatus(currentRank))
				.build();

		return domainDtoConverter.toTeamStatusDto(standing, model);
	}

	private static TeamStatus resolveStatus(int currentRank) {
		if (currentRank == 1 || currentRank == 2) {
			return TeamStatus.QUALIFIED;
		}
		if (currentRank == 4) {
			return TeamStatus.ELIMINATED;
		}
		return TeamStatus.STILL_ALIVE;
	}

	/**
	 * 1-based slot only when the team is 3rd and ranked within
	 * {@link QualificationConstants#BEST_THIRD_PLACE_SLOTS} among third-placed teams.
	 */
	private Integer resolveBestThirdPlaceSlot(Team team, int currentRank) {
		if (currentRank != 3) {
			return null;
		}
		List<StandingDto> rankedThirds = roundOf32Service.getRankedThirdPlaceStandingsDtos();
		for (int i = 0; i < rankedThirds.size(); i++) {
			if (rankedThirds.get(i).getTeam() == team) {
				int slot = i + 1;
				return slot <= QualificationConstants.BEST_THIRD_PLACE_SLOTS ? slot : null;
			}
		}
		return null;
	}

}
