package agents;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.ejb.Stateful;

import data.Data;
import model.ACLMessage;
import model.Agent;
import model.FootballMatch;
import model.Performative;

@Stateful
public class Predictor extends Agent {

	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.PREDICT)) {
			System.out.println("Usao u predict");
			ArrayList<FootballMatch> matches = new ArrayList<FootballMatch>();
			
			String[] res = message.getContent().split("\n");
			
			String team1 = res[0].split("-")[0];
			String team2 = res[0].split("-")[1];
			int team1wins = 0;
			int draws = 0;
			int team2wins = 0;
			
			for(String s : res) {
				String h = s.split("-")[0];
				String a = s.split("-")[1];
				int hg = Integer.parseInt(s.split("-")[2]);
				int ag = Integer.parseInt(s.split("-")[3]);
				
				matches.add(new FootballMatch(h,a,hg,ag));
			}
			
			for(FootballMatch fm : matches) {
				if(fm.getHomeGoals() == fm.getAwayGoals()) {
					draws++;
					continue;
				} else if(fm.getHomeTeam().equals(team1) && fm.getHomeGoals() > fm.getAwayGoals()) {
					team1wins++;
					continue;
				} else if(fm.getHomeTeam().equals(team2) && fm.getHomeGoals() < fm.getAwayGoals()) {
					team1wins++;
					continue;
				} else {
					team2wins++;
				}
			}
			
			System.out.println(team1wins + "   " + draws + "   " + team2wins);
			
			ACLMessage mess = new ACLMessage();
			mess.setPerformative(Performative.INFORM);
			
			if(team1wins >= draws && team1wins >= team2wins) {
				mess.setContent("" + team1 + " is predicted to win.");
			}
			
			if(team1wins < draws && draws > team2wins) {
				mess.setContent("Draw is expected.");
			}
			
			if(team2wins > team1wins && team2wins >= draws) {
				mess.setContent("" + team2 + " is predicted to win.");
			}
			
			InetAddress ip = null;
			try {
				ip = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String h = Data.getMyAddress();
			
			for(Agent a : Data.getAgents()) {
				if(a.getId().getHost().getAddress().equals(h) && a.getId().getType().getName().equals("master")) {
					a.handleMessage(mess);
					break;
				}
			}
		}
	}
}
