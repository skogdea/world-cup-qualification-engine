package com.staticoyster.worldcupqualificationengine.domain.model;

import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import java.util.Objects;

public class TeamStatus {

	private Group group;
	private Team team;
	private int currentRank;
	private int played;
	private int won;
	private int drawn;
	private int lost;
	private int goalsFor;
	private int goalsAgainst;
	private int goalDifference;
	private int teamConductScore;
	private int points;
	private Integer bestThirdPlaceSlot;
	private com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus status;

	public TeamStatus() {
	}

	private TeamStatus(Builder builder) {
		this.group = builder.group;
		this.team = builder.team;
		this.currentRank = builder.currentRank;
		this.played = builder.played;
		this.won = builder.won;
		this.drawn = builder.drawn;
		this.lost = builder.lost;
		this.goalsFor = builder.goalsFor;
		this.goalsAgainst = builder.goalsAgainst;
		this.goalDifference = builder.goalDifference;
		this.teamConductScore = builder.teamConductScore;
		this.points = builder.points;
		this.bestThirdPlaceSlot = builder.bestThirdPlaceSlot;
		this.status = builder.status;
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

	public int getCurrentRank() {
		return currentRank;
	}

	public void setCurrentRank(int currentRank) {
		this.currentRank = currentRank;
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

	public Integer getBestThirdPlaceSlot() {
		return bestThirdPlaceSlot;
	}

	public void setBestThirdPlaceSlot(Integer bestThirdPlaceSlot) {
		this.bestThirdPlaceSlot = bestThirdPlaceSlot;
	}

	public com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus getStatus() {
		return status;
	}

	public void setStatus(com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus status) {
		this.status = status;
	}

	public static final class Builder {

		private Group group;
		private Team team;
		private int currentRank;
		private int played;
		private int won;
		private int drawn;
		private int lost;
		private int goalsFor;
		private int goalsAgainst;
		private int goalDifference;
		private int teamConductScore;
		private int points;
		private Integer bestThirdPlaceSlot;
		private com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus status;

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

		public Builder withCurrentRank(int val) {
			currentRank = val;
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

		public Builder withBestThirdPlaceSlot(Integer val) {
			bestThirdPlaceSlot = val;
			return this;
		}

		public Builder withStatus(com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus val) {
			status = val;
			return this;
		}

		public TeamStatus build() {
			return new TeamStatus(this);
		}

	}

	@Override
	public String toString() {
		return "TeamStatus{"
				+ "group=" + group
				+ ", team=" + team
				+ ", currentRank=" + currentRank
				+ ", played=" + played
				+ ", won=" + won
				+ ", drawn=" + drawn
				+ ", lost=" + lost
				+ ", goalsFor=" + goalsFor
				+ ", goalsAgainst=" + goalsAgainst
				+ ", goalDifference=" + goalDifference
				+ ", teamConductScore=" + teamConductScore
				+ ", points=" + points
				+ ", bestThirdPlaceSlot=" + bestThirdPlaceSlot
				+ ", status=" + status
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
		TeamStatus that = (TeamStatus) object;
		return currentRank == that.currentRank
				&& played == that.played
				&& won == that.won
				&& drawn == that.drawn
				&& lost == that.lost
				&& goalsFor == that.goalsFor
				&& goalsAgainst == that.goalsAgainst
				&& goalDifference == that.goalDifference
				&& teamConductScore == that.teamConductScore
				&& points == that.points
				&& group == that.group
				&& team == that.team
				&& Objects.equals(bestThirdPlaceSlot, that.bestThirdPlaceSlot)
				&& status == that.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				group, team, currentRank, played, won, drawn, lost,
				goalsFor, goalsAgainst, goalDifference, teamConductScore, points,
				bestThirdPlaceSlot, status);
	}

}
