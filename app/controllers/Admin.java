package controllers;

import java.util.List;

import models.Post;
import models.Tag;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {
	
	@Before
	static void setConnectedUser() {
		if(Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.fullname);
		}
	}
	
	public static void index() {
		List<Post> posts = Post.find("author.email", Security.connected()).fetch();
		render(posts);
	}
	
	public static void form() {
		render();
	}
	
	public static void save(String title, String content, String tags) {
		
		User author = User.find("byEmail", Security.connected()).first();
		Post post = new Post(author, title, content);
		for(String tag : tags.split("\\s+")) {
			if(tag.trim().length() > 0) {
				post.tags.add(Tag.findOrCreateByName(tag));
			}
		}
		validation.valid(post);
		if(validation.hasErrors()) {
			render("@form", post);
		}
		post.save();
		index();
	}
}
