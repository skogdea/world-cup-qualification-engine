package com.staticoyster.worldcupqualificationengine.api;

import com.staticoyster.worldcupqualificationengine.domain.dto.QualificationResultDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.service.RoundOf32Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qualification")
public class QualificationController {

	private final RoundOf32Service roundOf32Service;

	public QualificationController(RoundOf32Service roundOf32Service) {
		this.roundOf32Service = roundOf32Service;
	}

	@GetMapping
	public QualificationResultDto getQualificationSnapshot() {
		return roundOf32Service.getQualificationSnapshotDto();
	}

	@GetMapping("/round-of-32")
	public List<Team> getRoundOf32Teams() {
		return roundOf32Service.getQualifiedTeams();
	}

	@GetMapping("/best-third-place")
	public List<StandingDto> getBestThirdPlaceStandings() {
		return roundOf32Service.getBestThirdPlaceStandingsDtos();
	}

}
