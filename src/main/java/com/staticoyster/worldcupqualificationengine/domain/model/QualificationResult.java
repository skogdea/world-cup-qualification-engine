package com.staticoyster.worldcupqualificationengine.domain.model;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import java.util.ArrayList;
import java.util.List;

public class QualificationResult {

	private final List<Team> groupWinners;
	private final List<Team> runnersUp;
	private final List<Standing> bestThirdPlaceStandings;
	private final List<Team> qualifiedTeams;

	public QualificationResult(
			List<Team> groupWinners,
			List<Team> runnersUp,
			List<Standing> bestThirdPlaceStandings) {
		this.groupWinners = List.copyOf(groupWinners);
		this.runnersUp = List.copyOf(runnersUp);
		this.bestThirdPlaceStandings = List.copyOf(bestThirdPlaceStandings);
		List<Team> qualified = new ArrayList<>();
		qualified.addAll(this.groupWinners);
		qualified.addAll(this.runnersUp);
		this.bestThirdPlaceStandings.stream()
				.map(Standing::getTeam)
				.forEach(qualified::add);
		this.qualifiedTeams = List.copyOf(qualified);
	}

	public List<Team> getGroupWinners() {
		return groupWinners;
	}

	public List<Team> getRunnersUp() {
		return runnersUp;
	}

	public List<Standing> getBestThirdPlaceStandings() {
		return bestThirdPlaceStandings;
	}

	public List<Team> getQualifiedTeams() {
		return qualifiedTeams;
	}

}
