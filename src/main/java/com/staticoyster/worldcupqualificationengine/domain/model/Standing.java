package com.staticoyster.worldcupqualificationengine.domain.model;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import java.util.Objects;

public class Standing {

	private Team team;
	private int points; // Pts
	private int goalsFor; // GF
	private int goalsAgainst; // GA
	private int goalDifference; // GD
	private int teamConductScore; // TCS

	public Standing() {
	}

	private Standing(Builder builder) {
		this.team = builder.team;
		this.points = builder.points;
		this.goalsFor = builder.goalsFor;
		this.goalsAgainst = builder.goalsAgainst;
		this.goalDifference = builder.goalDifference;
		this.teamConductScore = builder.teamConductScore;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getGoalsFor() {
		return goalsFor;
	}

	public void setGoalsFor(int goalsFor) {
		this.goalsFor = goalsFor;
	}

	public int getGoalsAgainst() {
		return goalsAgainst;
	}

	public void setGoalsAgainst(int goalsAgainst) {
		this.goalsAgainst = goalsAgainst;
	}

	public int getGoalDifference() {
		return goalDifference;
	}

	public void setGoalDifference(int goalDifference) {
		this.goalDifference = goalDifference;
	}

	public int getTeamConductScore() {
		return teamConductScore;
	}

	public void setTeamConductScore(int teamConductScore) {
		this.teamConductScore = teamConductScore;
	}

	public static final class Builder {

		private Team team;
		private int points;
		private int goalsFor;
		private int goalsAgainst;
		private int goalDifference;
		private int teamConductScore;

		private Builder() {
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Builder withTeam(Team val) {
			team = val;
			return this;
		}

		public Builder withPoints(int val) {
			points = val;
			return this;
		}

		public Builder withGoalsFor(int val) {
			goalsFor = val;
			return this;
		}

		public Builder withGoalsAgainst(int val) {
			goalsAgainst = val;
			return this;
		}

		public Builder withGoalDifference(int val) {
			goalDifference = val;
			return this;
		}

		public Builder withTeamConductScore(int val) {
			teamConductScore = val;
			return this;
		}

		public Standing build() {
			return new Standing(this);
		}

	}

	@Override
	public String toString() {
		return "Standing{"
				+ "team=" + team
				+ ", points=" + points
				+ ", goalsFor=" + goalsFor
				+ ", goalsAgainst=" + goalsAgainst
				+ ", goalDifference=" + goalDifference
				+ ", teamConductScore=" + teamConductScore
				+ '}';
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		Standing standing = (Standing) object;
		return points == standing.points
				&& goalsFor == standing.goalsFor
				&& goalsAgainst == standing.goalsAgainst
				&& goalDifference == standing.goalDifference
				&& teamConductScore == standing.teamConductScore
				&& team == standing.team;
	}

	@Override
	public int hashCode() {
		return Objects.hash(team, points, goalsFor, goalsAgainst, goalDifference, teamConductScore);
	}

}
