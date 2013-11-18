package redis.clients.johm.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.johm.Array;
import redis.clients.johm.Attribute;
import redis.clients.johm.CollectionList;
import redis.clients.johm.CollectionMap;
import redis.clients.johm.CollectionSet;
import redis.clients.johm.CollectionSortedSet;
import redis.clients.johm.Id;
import redis.clients.johm.Indexed;
import redis.clients.johm.Model;
import redis.clients.johm.Reference;
import redis.clients.johm.SupportAll;
import redis.clients.johm.utils.ObjectHelper;

@Model
@SupportAll
public class SimpleUser {
    @Id
    private Long id;
    @Attribute
    @Indexed
    private String name;
    private String room;
    @Attribute
    @Indexed
    private int age;
    @Attribute
    private float salary;
    @Attribute
    private char initial;
    @Reference
    @Indexed
    private Country country;
    @CollectionList(of = Item.class)
    @Indexed
    private List<Item> likes;
    @CollectionSet(of = Item.class)
    @Indexed
    private Set<Item> purchases;
    @CollectionMap(key = Integer.class, value = Item.class)
    @Indexed
    private Map<Integer, Item> favoritePurchases;
    @CollectionSortedSet(of = Item.class, by = "price")
    @Indexed
    private Set<Item> orderedPurchases;
    @Array(of = Item.class, length = 3)
    @Indexed
    private Item[] threeLatestPurchases;

    public Long getId() {
        return id;
    }

    public List<Item> getLikes() {
        return likes;
    }

    public Set<Item> getPurchases() {
        return purchases;
    }

    public Set<Item> getOrderedPurchases() {
        return orderedPurchases;
    }

    public Map<Integer, Item> getFavoritePurchases() {
        return favoritePurchases;
    }

    public void setThreeLatestPurchases(Item[] threeLatestPurchases) {
        this.threeLatestPurchases = threeLatestPurchases;
    }

    public Item[] getThreeLatestPurchases() {
        return threeLatestPurchases;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public char getInitial() {
        return initial;
    }

    public void setInitial(char initial) {
        this.initial = initial;
    }

    @Override
    public int hashCode() {
    	Object[] thisFields = new Object[]{
    			age,
    			country,
    			favoritePurchases,
    			initial,
    			likes,
    			name,
    			purchases,
    			room,
    			salary
    	};
    	return ObjectHelper.hashCode(thisFields);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleUser other = (SimpleUser) obj;

    	Object[] thisFields = new Object[]{
    			age,
    			country,
    			favoritePurchases,
    			initial,
    			likes,
    			name,
    			purchases,
    			room,
    			salary
    	};
    	
    	Object[] otherFields = new Object[]{
    			other.age,
    			other.country,
    			other.favoritePurchases,
    			other.initial,
    			other.likes,
    			other.name,
    			other.purchases,
    			other.room,
    			other.salary
    	};
    	return ObjectHelper.equals(thisFields, otherFields, this, obj);
    }
}
