package com.staticoyster.worldcupqualificationengine.domain.model;

import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus;

import java.util.Objects;

/**
 * Group-stage qualification status for a team. Standing lines stay on {@link Standing};
 * this model only carries rank / best-third / progress fields.
 */
public class TeamStatusModel {

	private Group group;
	private Team team;
	private int currentRank;
	private Integer bestThirdPlaceRank;
	private TeamStatus teamStatus;

	public TeamStatusModel() {
	}

	private TeamStatusModel(Builder builder) {
		this.group = builder.group;
		this.team = builder.team;
		this.currentRank = builder.currentRank;
		this.bestThirdPlaceRank = builder.bestThirdPlaceRank;
		this.teamStatus = builder.teamStatus;
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

	public Integer getBestThirdPlaceRank() {
		return bestThirdPlaceRank;
	}

	public void setBestThirdPlaceRank(Integer bestThirdPlaceRank) {
		this.bestThirdPlaceRank = bestThirdPlaceRank;
	}

	public TeamStatus getTeamStatus() {
		return teamStatus;
	}

	public void setTeamStatus(TeamStatus teamStatus) {
		this.teamStatus = teamStatus;
	}

	public static final class Builder {

		private Group group;
		private Team team;
		private int currentRank;
		private Integer bestThirdPlaceRank;
		private TeamStatus teamStatus;

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

		public Builder withBestThirdPlaceRank(Integer val) {
			bestThirdPlaceRank = val;
			return this;
		}

		public Builder withTeamStatus(TeamStatus val) {
			teamStatus = val;
			return this;
		}

		public TeamStatusModel build() {
			return new TeamStatusModel(this);
		}

	}

	@Override
	public String toString() {
		return "TeamStatusModel{"
				+ "group=" + group
				+ ", team=" + team
				+ ", currentRank=" + currentRank
				+ ", bestThirdPlaceRank=" + bestThirdPlaceRank
				+ ", teamStatus=" + teamStatus
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
		TeamStatusModel that = (TeamStatusModel) object;
		return currentRank == that.currentRank
				&& group == that.group
				&& team == that.team
				&& Objects.equals(bestThirdPlaceRank, that.bestThirdPlaceRank)
				&& teamStatus == that.teamStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(group, team, currentRank, bestThirdPlaceRank, teamStatus);
	}

}
