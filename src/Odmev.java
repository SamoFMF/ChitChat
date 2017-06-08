import java.util.TimerTask;

public class Odmev extends TimerTask {
	private OdmevRobot robot;
	private String text;
	
	public Odmev(OdmevRobot robot, String text) {
		this.robot = robot;
		this.text = text;
	}
	
	
	@Override
	public void run() {
		robot.chat.addMessage(robot.vzdevek, text, "Others");
	}

}
