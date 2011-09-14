import java.util.List;

import models.Comment;
import models.Post;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class BasicTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void createAndRetrieveUser() {
		new User("nishinaka.shinya@gmail.com", "1234", "Nishinaka Shinya").save();		
		User user = User.find("byEmail", "nishinaka.shinya@gmail.com").first();
		assertNotNull(user);
		assertEquals("Nishinaka Shinya", user.fullname);
	}

	@Test
	public void tryConnectAsUser() {
		new User("nishinaka.shinya@gmail.com", "1234", "Bob").save();
		
		assertNotNull(User.connect("nishinaka.shinya@gmail.com", "1234"));
		assertNull(User.connect("hoge@gmail.com", "1234"));
		assertNull(User.connect("nishinaka.shinya@gmail.com", "5678"));
	}
	
	@Test
	public void createPost() {
		User user = new User("nishinaka.shinya@gmail.com", "1234", "Nishinaka Shinya").save();
		new Post(user, "MyFirstPost", "Hello!").save();
		assertEquals(1, Post.count());
		
		List<Post> posts = Post.find("byAuthor", user).fetch();
		
		assertEquals(1, posts.size());
		Post firstPost = posts.get(0);
		assertNotNull(firstPost);
		assertEquals(user, firstPost.author);
		assertEquals("MyFirstPost", firstPost.title);
		assertEquals("Hello!", firstPost.content);
		assertNotNull(firstPost.postedAt);
	}
	
	@Test
	public void useTheCommentsRelaton() {
		User user = new User("nishinaka.shinya@gmail.com","1234","Nishinaka Shinya").save();
		Post post = new Post(user, "MyFirstPost", "Hello!").save();
		post.addComment("Jeff", "Nice Post!!");
		post.addComment("Tom", "I knew that!");
		
		assertEquals(1, User.count());
		assertEquals(1, Post.count());
		assertEquals(2, Comment.count());
		
		post = Post.find("byAuthor", user).first();
		assertNotNull(post);
		
		post.delete();
		assertEquals(1, User.count());
		assertEquals(0, Post.count());
		assertEquals(0, Comment.count());
	}
	
	@Test
	public void yamlTest() {
		Fixtures.loadModels("data.yml");
		
		assertEquals(2, User.count());
		assertEquals(3, Post.count());
		assertEquals(3, Comment.count());
	}
}
