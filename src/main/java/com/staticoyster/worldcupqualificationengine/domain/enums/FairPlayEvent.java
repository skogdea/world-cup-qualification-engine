package com.staticoyster.worldcupqualificationengine.domain.enums;

/**
 * Fair-play / team-conduct events.
 * {@code cardCode} is the FIFA live {@code Bookings[].Card} value;
 * {@code penalty} is the FIFA fair-play deduction weight.
 */
public enum FairPlayEvent {

	YELLOW_CARD(1, 1),
	DIRECT_RED(2, 4),
	SECOND_YELLOW_RED(3, 3);

	private final int cardCode;
	private final int penalty;

	FairPlayEvent(int cardCode, int penalty) {
		this.cardCode = cardCode;
		this.penalty = penalty;
	}

	/**
	 * FIFA live booking {@code Card} code (1=yellow, 2=direct red, 3=second-yellow red).
	 */
	public int getCardCode() {
		return cardCode;
	}

	public int getPenalty() {
		return penalty;
	}

	public static FairPlayEvent fromFifaCardCode(int cardCode) {
		for (FairPlayEvent event : values()) {
			if (event.cardCode == cardCode) {
				return event;
			}
		}
		throw new IllegalArgumentException("Unknown FIFA card code: " + cardCode);
	}

}
