import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sporocilo {
	private boolean global;
	private String posiljatelj;
	private String prejemnik;
	private String msg;
	private Date sentAt;
	
	private Sporocilo() { }
	
	private Sporocilo(boolean global, String posiljatelj, String prejemnik, String msg, Date sentAt) {
		this.global = global;
		this.posiljatelj = posiljatelj;
		this.prejemnik = prejemnik;
		this.msg = msg;
		this.sentAt = sentAt;
	}

	@JsonProperty("sent_at")
	public Date getSentAt() {
		return sentAt;
	}

	public void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}

	@JsonProperty("global")
	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	@JsonProperty("sender")
	public String getPosiljatelj() {
		return posiljatelj;
	}

	public void setPosiljatelj(String posiljatelj) {
		this.posiljatelj = posiljatelj;
	}

	@JsonProperty("recipient")
	public String getPrejemnik() {
		return prejemnik;
	}

	public void setPrejemnik(String prejemnik) {
		this.prejemnik = prejemnik;
	}

	@JsonProperty("text")
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
