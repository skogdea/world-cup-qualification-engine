package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.model.Standing;
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

	private static final int BEST_THIRD_PLACE_SLOTS = 8;

	private final GroupStageStandingsService groupStageStandingsService;

	public QualificationCalculator(GroupStageStandingsService groupStageStandingsService) {
		this.groupStageStandingsService = groupStageStandingsService;
	}

	public QualificationResult calculateQualification() {
		Map<Group, List<Standing>> standingsByGroup = groupStageStandingsService.calculateAllGroupStandings();

		List<Team> groupWinners = new ArrayList<>();
		List<Team> runnersUp = new ArrayList<>();
		List<Standing> thirdPlaceStandings = new ArrayList<>();

		for (List<Standing> groupStandings : standingsByGroup.values()) {
			if (groupStandings.size() >= 1) {
				groupWinners.add(groupStandings.get(0).getTeam());
			}
			if (groupStandings.size() >= 2) {
				runnersUp.add(groupStandings.get(1).getTeam());
			}
			if (groupStandings.size() >= 3) {
				thirdPlaceStandings.add(groupStandings.get(2));
			}
		}

		List<Standing> bestThirds = rankThirdPlaceTeams(thirdPlaceStandings).stream()
				.limit(BEST_THIRD_PLACE_SLOTS)
				.toList();

		return new QualificationResult(groupWinners, runnersUp, bestThirds);
	}

	/**
	 * FIFA ranking of third-placed teams across groups:
	 * points → goal difference → goals scored → team conduct score.
	 * FIFA World Ranking is not modeled yet; team FIFA code is the final deterministic fallback.
	 */
	private List<Standing> rankThirdPlaceTeams(List<Standing> thirdPlaceStandings) {
		List<Standing> ranked = new ArrayList<>(thirdPlaceStandings);
		ranked.sort(Comparator
				.comparingInt(Standing::getPoints).reversed()
				.thenComparing(Standing::getGoalDifference, Comparator.reverseOrder())
				.thenComparing(Standing::getGoalsFor, Comparator.reverseOrder())
				.thenComparing(Standing::getTeamConductScore, Comparator.reverseOrder())
				.thenComparing(standing -> standing.getTeam().getCode()));
		return ranked;
	}

}
