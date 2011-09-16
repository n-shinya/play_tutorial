package models;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Tag extends Model implements Comparable<Tag> {

	public String name;
	
	private Tag(String name) {
		this.name = name;
	}
	
	public static Tag findOrCreateByName(String name) {
		Tag tag = Tag.find("byName", name).first();
		if(tag == null) {
			tag = new Tag(name);
		}
		return tag;
	}
	
	public static List<Map> getCloud() {
		
		List<Map> result = Tag.find(
				"select new map(t.name as tag, count(p.id) as pound) from Post p join p.tags as t group by t.name order by t.name"
	    ).fetch();
		return result;
	}
	
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int compareTo(Tag other) {
		return name.compareTo(other.name);
	}

}