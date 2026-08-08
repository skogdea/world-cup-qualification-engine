package com.staticoyster.worldcupqualificationengine.ingestion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * FIFA public API settings for the live match + bookings adapter.
 *
 * <p>Stays as a Spring {@link ConfigurationProperties} bean (fields + getters/setters + no-arg ctor).
 * Domain-style Builder/withers are intentionally not used — Boot binds {@code app.fifa.*} via setters.
 */
@Component
@ConfigurationProperties(prefix = "app.fifa")
public class FifaApiProperties {

	/**
	 * FIFA API host, e.g. {@code https://api.fifa.com}.
	 */
	private String baseUrl = "https://api.fifa.com";

	/**
	 * FIFA competition id for the men's World Cup ({@code 17}).
	 */
	private String idCompetition = "17";

	/**
	 * FIFA season id for World Cup 2026 ({@code 285023}).
	 */
	private String idSeason = "285023";

	/**
	 * First-stage (group stage) {@code IdStage} used when bulk-importing calendar matches.
	 */
	private String idStageFirst = "289273";

	public FifaApiProperties() {
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getIdCompetition() {
		return idCompetition;
	}

	public void setIdCompetition(String idCompetition) {
		this.idCompetition = idCompetition;
	}

	public String getIdSeason() {
		return idSeason;
	}

	public void setIdSeason(String idSeason) {
		this.idSeason = idSeason;
	}

	public String getIdStageFirst() {
		return idStageFirst;
	}

	public void setIdStageFirst(String idStageFirst) {
		this.idStageFirst = idStageFirst;
	}

}
