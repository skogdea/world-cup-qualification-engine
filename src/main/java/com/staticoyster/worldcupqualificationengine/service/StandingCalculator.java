package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.model.Standing;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchPoints;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StandingCalculator {

	private final MatchRepository matchRepository;

	public StandingCalculator(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}

	public List<Match> retrieveAllPastMatchesInCurrentGroup(Group group) {
		return matchRepository.findByGroupAndStatus(group, MatchStatus.PAST);
	}

	public List<Standing> calculateStandingsInCurrentGroup(Group group) {
		List<Match> pastMatches = retrieveAllPastMatchesInCurrentGroup(group);
		Map<Team, Standing> standingsByTeam = initializeStandings(group);

		for (Match match : pastMatches) {
			applyMatchResult(standingsByTeam, match);
		}

		for (Standing standing : standingsByTeam.values()) {
			standing.setGoalDifference(standing.getGoalsFor() - standing.getGoalsAgainst());
			// TCS stays 0 until card events can be sourced; Match is the only persisted entity.
		}

		return rankStandings(new ArrayList<>(standingsByTeam.values()), pastMatches);
	}

	private Map<Team, Standing> initializeStandings(Group group) {
		Map<Team, Standing> standingsByTeam = new EnumMap<>(Team.class);
		for (Team team : teamsInGroup(group)) {
			standingsByTeam.put(team, Standing.Builder.newBuilder()
					.withTeam(team)
					.withPoints(0)
					.withGoalsFor(0)
					.withGoalsAgainst(0)
					.withGoalDifference(0)
					.withTeamConductScore(0)
					.build());
		}
		return standingsByTeam;
	}

	private void applyMatchResult(Map<Team, Standing> standingsByTeam, Match match) {
		Standing home = standingsByTeam.get(match.getHome());
		Standing away = standingsByTeam.get(match.getAway());
		if (home == null || away == null) {
			return;
		}

		// Accumulate goals across all past matches in the group (running totals).
		home.setGoalsFor(home.getGoalsFor() + match.getHomeScore());
		home.setGoalsAgainst(home.getGoalsAgainst() + match.getAwayScore());
		away.setGoalsFor(away.getGoalsFor() + match.getAwayScore());
		away.setGoalsAgainst(away.getGoalsAgainst() + match.getHomeScore());

		if (match.getHomeScore() > match.getAwayScore()) {
			home.setPoints(home.getPoints() + MatchPoints.WIN.getValue());
		}
		else if (match.getHomeScore() < match.getAwayScore()) {
			away.setPoints(away.getPoints() + MatchPoints.WIN.getValue());
		}
		else {
			home.setPoints(home.getPoints() + MatchPoints.DRAW.getValue());
			away.setPoints(away.getPoints() + MatchPoints.DRAW.getValue());
		}
	}

	private List<Standing> rankStandings(List<Standing> standings, List<Match> pastMatches) {
		Map<Integer, List<Standing>> byPoints = standings.stream()
				.sorted(Comparator.comparingInt(Standing::getPoints).reversed())
				.collect(Collectors.groupingBy(
						Standing::getPoints,
						LinkedHashMap::new,
						Collectors.toCollection(ArrayList::new)));

		List<Standing> ranked = new ArrayList<>();
		for (List<Standing> tiedOnPoints : byPoints.values()) {
			ranked.addAll(breakTies(tiedOnPoints, pastMatches));
		}
		return ranked;
	}

	private List<Standing> breakTies(List<Standing> tied, List<Match> pastMatches) {
		if (tied.size() <= 1) {
			return tied;
		}

		List<Team> tiedTeams = tied.stream().map(Standing::getTeam).toList();
		List<Match> miniMatches = pastMatches.stream()
				.filter(match -> tiedTeams.contains(match.getHome()) && tiedTeams.contains(match.getAway()))
				.toList();

		Map<Team, int[]> h2h = new EnumMap<>(Team.class);
		for (Team team : tiedTeams) {
			h2h.put(team, new int[] {0, 0, 0}); // points, gd, gf
		}
		for (Match match : miniMatches) {
			int[] homeStats = h2h.get(match.getHome());
			int[] awayStats = h2h.get(match.getAway());
			homeStats[2] += match.getHomeScore();
			awayStats[2] += match.getAwayScore();
			homeStats[1] += match.getHomeScore() - match.getAwayScore();
			awayStats[1] += match.getAwayScore() - match.getHomeScore();
			if (match.getHomeScore() > match.getAwayScore()) {
				homeStats[0] += MatchPoints.WIN.getValue();
			}
			else if (match.getHomeScore() < match.getAwayScore()) {
				awayStats[0] += MatchPoints.WIN.getValue();
			}
			else {
				homeStats[0] += MatchPoints.DRAW.getValue();
				awayStats[0] += MatchPoints.DRAW.getValue();
			}
		}

		// FIFA in-group tie-breakers when equal on overall points:
		// H2H Pts → H2H GD → H2H GF → overall GD → overall GF → TCS → FIFA ranking
		// https://www.fifa.com/en/tournaments/mens/worldcup/canadamexicousa2026/articles/groups-how-teams-qualify-tie-breakers
		List<Standing> sorted = new ArrayList<>(tied);
		sorted.sort((left, right) -> {
			int[] leftH2h = h2h.get(left.getTeam());
			int[] rightH2h = h2h.get(right.getTeam());
			int cmp = Integer.compare(rightH2h[0], leftH2h[0]);
			if (cmp != 0) {
				return cmp;
			}
			cmp = Integer.compare(rightH2h[1], leftH2h[1]);
			if (cmp != 0) {
				return cmp;
			}
			cmp = Integer.compare(rightH2h[2], leftH2h[2]);
			if (cmp != 0) {
				return cmp;
			}
			cmp = Integer.compare(right.getGoalDifference(), left.getGoalDifference());
			if (cmp != 0) {
				return cmp;
			}
			cmp = Integer.compare(right.getGoalsFor(), left.getGoalsFor());
			if (cmp != 0) {
				return cmp;
			}
			cmp = Integer.compare(right.getTeamConductScore(), left.getTeamConductScore());
			if (cmp != 0) {
				return cmp;
			}
			// FIFA ranking not modeled yet; deterministic fallback by team code.
			return left.getTeam().getCode().compareTo(right.getTeam().getCode());
		});
		return sorted;
	}

	private List<Team> teamsInGroup(Group group) {
		return Arrays.stream(Team.values())
				.filter(team -> team.getGroup() == group)
				.toList();
	}

}
