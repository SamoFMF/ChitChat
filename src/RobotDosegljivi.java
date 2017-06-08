import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
			TypeReference<List<Uporabnik>> t = new TypeReference<List<Uporabnik>>() { };
			List<Uporabnik> uporabniki = mapper.readValue(jsonUporabniki, t);
			chat.dosegljivi.setText("");
			for (Uporabnik i : uporabniki) {
				String text = chat.dosegljivi.getText();
				chat.dosegljivi.setText(text + i.getUsername() + '\n');
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
