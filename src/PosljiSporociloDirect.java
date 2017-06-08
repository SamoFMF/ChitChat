import com.fasterxml.jackson.annotation.JsonProperty;

public class PosljiSporociloDirect extends PosljiSporocilo {
	private String prejemnik;
	
	PosljiSporociloDirect() { }
	
	public PosljiSporociloDirect(boolean global, String msg, String prejemnik) {
		super(global, msg);
		this.prejemnik = prejemnik;
	}

	@Override
	public String toString() {
		return "PosljiSporociloDirect [global=" + global + ", msg=" + msg + ", prejemnik=" + prejemnik + "]";
	}

	@JsonProperty("recipient")
	public String getPrejemnik() {
		return prejemnik;
	}

	public void setPrejemnik(String prejemnik) {
		this.prejemnik = prejemnik;
	}
	
	
}
