package com.staticoyster.worldcupqualificationengine.domain.model;

import com.staticoyster.worldcupqualificationengine.domain.enums.FairPlayEvent;

import java.util.Objects;

public class TeamMatchStats {

	private int yellowCards;
	private int secondYellowReds;
	private int directReds;

	public TeamMatchStats() {
	}

	public int getYellowCards() {
		return yellowCards;
	}

	public void setYellowCards(int yellowCards) {
		this.yellowCards = yellowCards;
	}

	public int getSecondYellowReds() {
		return secondYellowReds;
	}

	public void setSecondYellowReds(int secondYellowReds) {
		this.secondYellowReds = secondYellowReds;
	}

	public int getDirectReds() {
		return directReds;
	}

	public void setDirectReds(int directReds) {
		this.directReds = directReds;
	}

	private TeamMatchStats(Builder builder) {
		this.yellowCards = builder.yellowCards;
		this.secondYellowReds = builder.secondYellowReds;
		this.directReds = builder.directReds;
	}

	public static final class Builder {

		private int yellowCards;
		private int secondYellowReds;
		private int directReds;

		private Builder() {
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Builder withYellowCards(int val) {
			yellowCards = val;
			return this;
		}

		public Builder withSecondYellowReds(int val) {
			secondYellowReds = val;
			return this;
		}

		public Builder withDirectReds(int val) {
			directReds = val;
			return this;
		}

		public TeamMatchStats build() {
			return new TeamMatchStats(this);
		}

	}

	/**
	 * FIFA team conduct / fair-play score for this side:
	 * {@code -(yellow * 1 + secondYellowRed * 3 + directRed * 4)}.
	 */
	public int getFairPlayScore() {
		return -(yellowCards * FairPlayEvent.YELLOW_CARD.getPenalty()
				+ secondYellowReds * FairPlayEvent.SECOND_YELLOW_RED.getPenalty()
				+ directReds * FairPlayEvent.DIRECT_RED.getPenalty());
	}

	@Override
	public String toString() {
		return "TeamMatchStats{"
				+ "yellowCards=" + yellowCards
				+ ", secondYellowReds=" + secondYellowReds
				+ ", directReds=" + directReds
				+ ", fairPlayScore=" + getFairPlayScore()
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
		TeamMatchStats that = (TeamMatchStats) object;
		return yellowCards == that.yellowCards
				&& secondYellowReds == that.secondYellowReds
				&& directReds == that.directReds;
	}

	@Override
	public int hashCode() {
		return Objects.hash(yellowCards, secondYellowReds, directReds);
	}

}
