package cardprocess.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MyViettelAccount")
public class MyViettelAccount {
	@Id
	@Column(name = "id")
	long id;
	String username;
	String password;
	int charged;
	int checked;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getCharged() {
		return charged;
	}
	public void setCharged(int charged) {
		this.charged = charged;
	}
	public int getChecked() {
		return checked;
	}
	public void setChecked(int checked) {
		this.checked = checked;
	}

	
}
