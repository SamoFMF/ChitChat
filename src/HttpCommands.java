import java.net.URI;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

public class HttpCommands {
	public static void prijava(String ime) throws Exception {
		URI uri = new URIBuilder("http://chitchat.andrej.com/users")
		          .addParameter("username", ime)
		          .build();

		  String responseBody = Request.Post(uri)
		                               .execute()
		                               .returnContent()
		                               .asString();

		  System.out.println(responseBody);
	}
	
	public static void odjava(String ime) throws Exception {
		URI uri = new URIBuilder("http://chitchat.andrej.com/users")
				.addParameter("username", ime)
				.build();

		String responseBody = Request.Delete(uri)
				.execute()
				.returnContent()
				.asString();

		System.out.println(responseBody);
	}
	
	public static String pridobiSporocila(String ime) throws Exception {
		URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
				.addParameter("username", ime)
				.build();

		String responseBody = Request.Get(uri)
				.execute()
				.returnContent()
				.asString();

		// System.out.println(responseBody);

		return responseBody;
	}
	
	public static void posljiSporocilo(String ime, String jsonNiz) throws Exception {
		URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
				.addParameter("username", ime)
				.build();
		
		String responseBody = Request.Post(uri)
		          .bodyString(jsonNiz, ContentType.APPLICATION_JSON)
		          .execute()
		          .returnContent()
		          .asString();
		
		System.out.println(responseBody);
	}
	
	public static String pridobiUporabnike() throws Exception {
		String responseBody = Request.Get("http://chitchat.andrej.com/users")
				.execute()
				.returnContent()
				.asString();

		// System.out.println(responseBody);
		
		return responseBody;
	}
}
