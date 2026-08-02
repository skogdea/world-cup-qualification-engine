package com.staticoyster.worldcupqualificationengine.domain.enums;

public enum FairPlayEvent {

	YELLOW_CARD(1),
	SECOND_YELLOW_RED(3),
	DIRECT_RED(4);

	private final int penalty;

	FairPlayEvent(int penalty) {
		this.penalty = penalty;
	}

	public int getPenalty() {
		return penalty;
	}

}
