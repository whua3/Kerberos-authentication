package Databean;

public class User {
	private String name = null;
	private long id;
	
	public User(long id, String name){
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "{ \"id\":"+id+", \"name\":"+name+" }";
	}
}
