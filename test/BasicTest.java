import java.util.List;
import java.util.Map;

import models.Comment;
import models.Post;
import models.Tag;
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
	
	@Test
	public void testTags() {
		User bob = new User("bob@gmail.com", "secret", "Bob").save();
		
		Post bobPost = new Post(bob, "My first post", "Hello world").save();
		Post anotherBobPost = new Post(bob, "Hop", "Hello world").save();
		
		assertEquals(0, Post.findTaggedWith("Red").size());
		
		bobPost.tagItWith("Red")
				.tagItWith("Blue").save();
		
		anotherBobPost.tagItWith("Red")
						.tagItWith("Green").save();
		
		assertEquals(2, Post.findTaggedWith("Red").size());
		assertEquals(1, Post.findTaggedWith("Blue").size());
		assertEquals(1, Post.findTaggedWith("Green").size());
		
		List<Map> cloud = Tag.getCloud();
		assertEquals("[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]", cloud.toString());
		
	}
	
	
	
	
}
