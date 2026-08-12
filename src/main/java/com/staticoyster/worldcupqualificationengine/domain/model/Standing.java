package com.staticoyster.worldcupqualificationengine.domain.model;

import java.util.Objects;

import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

public class Standing {

	private Group group;
	private Team team;
	private int played; // P
	private int won; // W
	private int drawn; // D
	private int lost; // L
	private int goalsFor; // GF
	private int goalsAgainst; // GA
	private int goalDifference; // GD
	private int teamConductScore; // TCS
	private int points; // Pts

	public Standing() {
	}

	private Standing(Builder builder) {
		this.group = builder.group;
		this.team = builder.team;
		this.played = builder.played;
		this.won = builder.won;
		this.drawn = builder.drawn;
		this.lost = builder.lost;
		this.goalsFor = builder.goalsFor;
		this.goalsAgainst = builder.goalsAgainst;
		this.goalDifference = builder.goalDifference;
		this.teamConductScore = builder.teamConductScore;
		this.points = builder.points;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getPlayed() {
		return played;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public int getWon() {
		return won;
	}

	public void setWon(int won) {
		this.won = won;
	}

	public int getDrawn() {
		return drawn;
	}

	public void setDrawn(int drawn) {
		this.drawn = drawn;
	}

	public int getLost() {
		return lost;
	}

	public void setLost(int lost) {
		this.lost = lost;
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

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public static final class Builder {

		private Group group;
		private Team team;
		private int played;
		private int won;
		private int drawn;
		private int lost;
		private int goalsFor;
		private int goalsAgainst;
		private int goalDifference;
		private int teamConductScore;
		private int points;

		private Builder() {
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Builder withGroup(Group val) {
			group = val;
			return this;
		}

		public Builder withTeam(Team val) {
			team = val;
			return this;
		}

		public Builder withPlayed(int val) {
			played = val;
			return this;
		}

		public Builder withWon(int val) {
			won = val;
			return this;
		}

		public Builder withDrawn(int val) {
			drawn = val;
			return this;
		}

		public Builder withLost(int val) {
			lost = val;
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

		public Builder withPoints(int val) {
			points = val;
			return this;
		}

		public Standing build() {
			return new Standing(this);
		}

	}

	@Override
	public String toString() {
		return "Standing{"
				+ "group=" + group
				+ ", team=" + team
				+ ", played=" + played
				+ ", won=" + won
				+ ", drawn=" + drawn
				+ ", lost=" + lost
				+ ", goalsFor=" + goalsFor
				+ ", goalsAgainst=" + goalsAgainst
				+ ", goalDifference=" + goalDifference
				+ ", teamConductScore=" + teamConductScore
				+ ", points=" + points
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
		return played == standing.played
				&& won == standing.won
				&& drawn == standing.drawn
				&& lost == standing.lost
				&& goalsFor == standing.goalsFor
				&& goalsAgainst == standing.goalsAgainst
				&& goalDifference == standing.goalDifference
				&& teamConductScore == standing.teamConductScore
				&& points == standing.points
				&& group == standing.group
				&& team == standing.team;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				group, team, played, won, drawn, lost,
				goalsFor, goalsAgainst, goalDifference, teamConductScore, points);
	}

}
