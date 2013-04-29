package redis.clients.johm.models;

import redis.clients.johm.Id;
import redis.clients.johm.Model;

@Model
public class TestingClassWithUnvalidId{
	@Id
	private Boolean id;
	
	public void setId(Boolean id){
		this.id = id;
	}
	public Boolean getId(){
		return this.id;
	}
}
