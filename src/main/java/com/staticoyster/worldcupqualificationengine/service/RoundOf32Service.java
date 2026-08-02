package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.QualificationResult;
import org.springframework.stereotype.Service;

import java.util.List;

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

	public QualificationResult getQualificationSnapshot() {
		return qualificationCalculator.calculateQualification();
	}

}
