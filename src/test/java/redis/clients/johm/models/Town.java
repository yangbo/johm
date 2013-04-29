package redis.clients.johm.models;

import redis.clients.johm.Attribute;
import redis.clients.johm.Id;
import redis.clients.johm.Model;

@Model
public class Town {
	@Id
	private String townId;
	@Attribute
	private String name;

	public String getTownId() {
		return townId;
	}

	public void setTownId(String townId) {
		this.townId = townId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
