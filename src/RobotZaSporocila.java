import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RobotZaSporocila extends TimerTask {
	private ChatFrame chat;
	
	public RobotZaSporocila(ChatFrame chat) {
		this.chat = chat;
	}
	
	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, 100);
	}

	@Override
	public void run() {
		// Posljemo strezniku zahtevo za sporocila
		try {
			String jsonSporocila = HttpCommands.pridobiSporocila(chat.imeEditor.getText());
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<Sporocilo>> t = new TypeReference<List<Sporocilo>>() { };
			List<Sporocilo> sporocila = mapper.readValue(jsonSporocila, t);
			
			for (Sporocilo i : sporocila) {
//				chat.addMessage(i.getPosiljatelj(), i.getMsg());
				if (i.isGlobal()) {
					chat.addMessage(i.getPosiljatelj(), i.getMsg(), "Others");
				} else {
					chat.addMessage(i.getPosiljatelj(), i.getPrejemnik(), i.getMsg(), "Others");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
