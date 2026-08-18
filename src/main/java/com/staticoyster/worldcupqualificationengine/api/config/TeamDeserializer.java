package com.staticoyster.worldcupqualificationengine.api.config;

import java.util.Locale;

import org.springframework.boot.jackson.JacksonComponent;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * JSON {@link Team} input: enum name ({@code MEXICO}) or FIFA code ({@code MEX}).
 * Responses still serialize as enum names via Jackson's default enum handling.
 */
@JacksonComponent(type = Team.class)
public class TeamDeserializer extends ValueDeserializer<Team> {

	@Override
	public Team deserialize(JsonParser parser, DeserializationContext context) {
		String raw = parser.getString();
		if (raw == null) {
			return null;
		}
		String normalized = raw.trim().toUpperCase(Locale.ROOT);
		try {
			return Team.valueOf(normalized);
		}
		catch (IllegalArgumentException ignored) {
			try {
				return Team.fromCode(normalized);
			}
			catch (IllegalArgumentException exception) {
				return (Team) context.handleWeirdStringValue(Team.class, raw, exception.getMessage());
			}
		}
	}

}
