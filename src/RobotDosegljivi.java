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
			System.out.println(jsonUporabniki);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new ISO8601DateFormat());
			TypeReference<List<Uporabnik>> t = new TypeReference<List<Uporabnik>>() { };
			List<Uporabnik> uporabniki = mapper.readValue(jsonUporabniki, t);
			chat.addAllOnlineUsers(uporabniki);
			chat.dosegljivi.setText("");
			String[] online = new String[uporabniki.size()];
			int st = 0;
			for (Uporabnik i : uporabniki) {
				String text = chat.dosegljivi.getText();
				chat.dosegljivi.setText(text + i.getUsername() + '\n');
				// Dodamo še v online
				if (i.getUsername().equals(chat.imeEditor.getText())) continue;
				online[st] = i.getUsername();
				st++;
			}
			boolean b = new HashSet<String>(Arrays.asList(online)).equals(new HashSet<String>(Arrays.asList(chat.getOnline()))); // Preverimo, èe sta seznama enaka
			if (!b) {
				// Èe sta razlièna, posodobimo
				chat.testBox(online);
				chat.setOnline(online);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
