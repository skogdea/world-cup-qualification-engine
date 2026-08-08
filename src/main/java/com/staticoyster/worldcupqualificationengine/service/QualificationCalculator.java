package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.QualificationResultDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.QualificationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class QualificationCalculator {

	private final GroupStageStandingsService groupStageStandingsService;
	private final DomainDtoConverter domainDtoConverter;
	private final FifaWorldRankingService fifaWorldRankingService;

	public QualificationCalculator(
			GroupStageStandingsService groupStageStandingsService,
			DomainDtoConverter domainDtoConverter,
			FifaWorldRankingService fifaWorldRankingService) {
		this.groupStageStandingsService = groupStageStandingsService;
		this.domainDtoConverter = domainDtoConverter;
		this.fifaWorldRankingService = fifaWorldRankingService;
	}

	public QualificationResultDto calculateQualification() {
		Map<Group, List<StandingDto>> standingsDtoByGroup = groupStageStandingsService.calculateAllGroupStandingsDto();

		List<Team> groupWinners = new ArrayList<>();
		List<Team> runnersUp = new ArrayList<>();
		List<StandingDto> thirdPlaceStandings = new ArrayList<>();

		for (List<StandingDto> groupStandings : standingsDtoByGroup.values()) {
			if (groupStandings.size() >= 1) { // Todo: don't understand
				groupWinners.add(groupStandings.get(0).getTeam());
			}
			if (groupStandings.size() >= 2) {
				runnersUp.add(groupStandings.get(1).getTeam());
			}
			if (groupStandings.size() >= 3) {
				thirdPlaceStandings.add(groupStandings.get(2));
			}
		}

		List<StandingDto> bestThirds = rankThirdPlaceTeams(thirdPlaceStandings).stream()
				.limit(TournamentConstants.BEST_THIRD_PLACE_SLOTS)
				.toList();

		// QualificationResult (domain model) still holds Standing; convert only at that boundary.
		return domainDtoConverter.toQualificationResultDto(
				new QualificationResult(groupWinners, runnersUp, domainDtoConverter.toStandings(bestThirds)));
	}

	/**
	 * FIFA ranking of third-placed teams across groups:
	 * points → goal difference → goals scored → team conduct score → FIFA world ranking.
	 */
	private List<StandingDto> rankThirdPlaceTeams(List<StandingDto> thirdPlaceStandings) {
		List<StandingDto> ranked = new ArrayList<>(thirdPlaceStandings);
		ranked.sort(Comparator
				.comparingInt(StandingDto::getPoints).reversed()
				.thenComparing(StandingDto::getGoalDifference, Comparator.reverseOrder())
				.thenComparing(StandingDto::getGoalsFor, Comparator.reverseOrder())
				.thenComparing(StandingDto::getTeamConductScore, Comparator.reverseOrder())
				.thenComparing(StandingDto::getTeam, fifaWorldRankingService::compare));
		return ranked;
	}

}
