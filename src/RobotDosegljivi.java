import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class RobotDosegljivi extends TimerTask {
	private ChatFrame chat;
	
	public RobotDosegljivi(ChatFrame chat) {
		this.chat = chat;
	}
	
	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, 1000);
	}

	@Override
	public void run() {
		try {
			String jsonUporabniki = HttpCommands.pridobiUporabnike();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new ISO8601DateFormat());
			TypeReference<List<Uporabnik>> t = new TypeReference<List<Uporabnik>>() { };
			List<Uporabnik> uporabniki = mapper.readValue(jsonUporabniki, t);
			chat.addAllOnlineUsers(uporabniki);
			
			// Dodajmo še možnosti za private msg
			String[] online = new String[uporabniki.size()];
			int st = 0;
			for (Uporabnik i : uporabniki) {
				if (i.getUsername().equals(chat.getImeEditor().getText())) continue;
				online[st] = i.getUsername();
				st++;
			}
			boolean b = new HashSet<String>(Arrays.asList(online)).equals(new HashSet<String>(Arrays.asList(chat.getOnline()))); // Preverimo, èe je kak nov uporabnik prišel online / kater odšel offline
			if (!b) {
				// Seznama uporabnikov nista enaka
				chat.updateWhoMenu(online);
				chat.setOnline(online);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
