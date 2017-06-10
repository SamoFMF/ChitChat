import com.fasterxml.jackson.annotation.JsonProperty;

public class PosljiSporocilo {
	private boolean global;
	private String msg;
	
	protected PosljiSporocilo() { }
	
	public PosljiSporocilo(boolean global, String msg) {
		this.global = global;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "PosljiSporocilo [global=" + global + ", msg=" + msg + "]";
	}

	@JsonProperty("global")
	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	@JsonProperty("text")
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
