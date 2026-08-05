package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableQualificationResultDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.QualificationResultDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.QualificationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoundOf32Service {

	private final QualificationCalculator qualificationCalculator;
	private final GroupStageStandingsService groupStageStandingsService;

	public RoundOf32Service(
			QualificationCalculator qualificationCalculator,
			GroupStageStandingsService groupStageStandingsService) {
		this.qualificationCalculator = qualificationCalculator;
		this.groupStageStandingsService = groupStageStandingsService;
	}

	/**
	 * Dynamically derives the Round of 32 field from current group standings.
	 * While the group stage is unfinished, this list is provisional.
	 */
	public List<Team> getQualifiedTeams() {
		return qualificationCalculator.calculateQualification().getQualifiedTeams();
	}

	public QualificationResult getQualificationSnapshot() {
		return qualificationCalculator.calculateQualification();
	}

	public QualificationResultDto getQualificationSnapshotDto() {
		QualificationResult result = getQualificationSnapshot();
		return ImmutableQualificationResultDto.builder()
				.groupWinners(result.getGroupWinners())
				.runnersUp(result.getRunnersUp())
				.bestThirdPlaceStandings(groupStageStandingsService.toStandingDtos(result.getBestThirdPlaceStandings()))
				.qualifiedTeams(result.getQualifiedTeams())
				.build();
	}

	public List<StandingDto> getBestThirdPlaceStandingsDtos() {
		return groupStageStandingsService.toStandingDtos(getQualificationSnapshot().getBestThirdPlaceStandings());
	}

}
