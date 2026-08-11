package com.staticoyster.worldcupqualificationengine.api;

import com.staticoyster.worldcupqualificationengine.domain.dto.TeamStatusDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.service.TeamStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class StatusController {

	private final TeamStatusService teamStatusService;

	public StatusController(TeamStatusService teamStatusService) {
		this.teamStatusService = teamStatusService;
	}

	@GetMapping("/teams/{team}")
	public TeamStatusDto getTeamStatus(@PathVariable("team") Team team) {
		return teamStatusService.getTeamStatus(team);
	}

}
