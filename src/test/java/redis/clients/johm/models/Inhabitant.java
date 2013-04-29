package redis.clients.johm.models;

import java.util.UUID;

import redis.clients.johm.Attribute;
import redis.clients.johm.Id;
import redis.clients.johm.Model;

@Model
public class Inhabitant {

	@Id
	private UUID id;

	@Attribute
	private String name;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
