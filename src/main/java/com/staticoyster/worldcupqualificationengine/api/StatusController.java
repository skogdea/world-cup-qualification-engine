package com.staticoyster.worldcupqualificationengine.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staticoyster.worldcupqualificationengine.domain.dto.TeamStatusDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.service.TeamStatusService;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

	private final TeamStatusService teamStatusService;

	public StatusController(TeamStatusService teamStatusService) {
		this.teamStatusService = teamStatusService;
	}

	/**
	 * Path is a FIFA 3-letter code ({@code IRN}), resolved by {@link Team#fromCode(String)}.
	 * Do not bind {@code @PathVariable Team}: a throwing {@code Converter} falls back to
	 * {@code Enum.valueOf}, so {@code IR_IRAN} would incorrectly return 200.
	 */
	@GetMapping("/teams/{team}")
	public TeamStatusDto getTeamStatus(@PathVariable("team") String team) {
		return teamStatusService.getTeamStatus(Team.fromCode(team));
	}

}
