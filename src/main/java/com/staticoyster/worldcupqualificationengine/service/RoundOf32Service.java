package com.staticoyster.worldcupqualificationengine.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.staticoyster.worldcupqualificationengine.domain.dto.QualificationDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

@Service
public class RoundOf32Service {

	private final QualificationCalculator qualificationCalculator;

	public RoundOf32Service(QualificationCalculator qualificationCalculator) {
		this.qualificationCalculator = qualificationCalculator;
	}

	/**
	 * Dynamically derives the Round of 32 field from current group standings.
	 * While the group stage is unfinished, this list is provisional.
	 */
	public List<Team> getQualifiedTeams() {
		return qualificationCalculator.calculateQualification().getQualifiedTeams();
	}

	public QualificationDto getQualificationSnapshotDto() {
		return qualificationCalculator.calculateQualification();
	}

	public List<StandingDto> getBestThirdPlaceStandingsDtos() {
		return getQualificationSnapshotDto().getBestThirdPlaceStandings();
	}

	/** All third-placed teams ranked by FIFA criteria (may be more than the advancing eight). */
	public List<StandingDto> getRankedThirdPlaceStandingsDtos() {
		return qualificationCalculator.getRankedThirdPlaceStandings();
	}

}
