package redis.clients.johm.utils;

import static junit.framework.Assert.*;
import org.junit.Test;

import redis.clients.johm.models.Country;
import redis.clients.johm.models.SimpleUser;

public class ObjectHelperTest {

	@Test
	public void hashCodeNotEquals() {
		SimpleUser user = new SimpleUser();
		user.setName("yangbo");
		user.setAge(30);
		Country country = new Country();
		user.setCountry(country);
		
		SimpleUser other = new SimpleUser();
		other.setName("Rain Man");
		other.setAge(21);
		Country country2 = new Country();
		other.setCountry(country2);
		
		int hash1 = user.hashCode();
		int hash2 = other.hashCode();
		//System.out.println("hash1: " + hash1 + ", hash2: " + hash2);
		assertNotSame(hash1, hash2);
	}
	
	@Test
	public void hashCodeEquals() {
		SimpleUser user = new SimpleUser();
		user.setName("yangbo");
		user.setAge(30);
		Country country = new Country();
		user.setCountry(country);
		
		SimpleUser other = new SimpleUser();
		other.setName("yangbo");
		other.setAge(30);
		other.setCountry(country);
		
		int hash1 = user.hashCode();
		int hash2 = other.hashCode();
		//System.out.println("hash1: " + hash1 + ", hash2: " + hash2);
		assertEquals(hash1, hash2);
	}

	@Test
	public void shouldEquals() {
		SimpleUser user = new SimpleUser();
		user.setName("yangbo");
		user.setAge(30);
		Country country = new Country();
		user.setCountry(country);
		user.setSalary(0.0f);
		
		SimpleUser other = new SimpleUser();
		other.setName("yangbo");
		other.setAge(30);
		other.setCountry(country);
		other.setSalary(0.000f);
		
		assertEquals(user, other);
	}
	
	@Test
	public void shouldNotEqualsForFloat() {
		SimpleUser user = new SimpleUser();
		user.setName("yangbo");
		user.setAge(30);
		Country country = new Country();
		user.setCountry(country);
		user.setSalary(10000.123f);
		
		SimpleUser other = new SimpleUser();
		other.setName("yangbo");
		other.setAge(30);
		other.setCountry(country);
		other.setSalary(10000.120f);
		
		assertFalse("Float should be different.", user.equals(other));
	}
}
