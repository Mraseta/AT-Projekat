package model;

import java.io.Serializable;

public class FootballMatch implements Serializable {

	private String homeTeam;
	private String awayTeam;
	private int homeGoals;
	private int awayGoals;
	
	public FootballMatch(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
		super();
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.homeGoals = homeGoals;
		this.awayGoals = awayGoals;
	}
	
	public FootballMatch() {
		
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}

	public int getHomeGoals() {
		return homeGoals;
	}

	public void setHomeGoals(int homeGoals) {
		this.homeGoals = homeGoals;
	}

	public int getAwayGoals() {
		return awayGoals;
	}

	public void setAwayGoals(int awayGoals) {
		this.awayGoals = awayGoals;
	}

	@Override
	public String toString() {
		return "" + homeTeam + "-" + awayTeam + "-" + homeGoals+ "-" + awayGoals;
	}
	
	
}
