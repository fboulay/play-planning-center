import java.util.List;

import javax.persistence.EntityManager;

import models.Person;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.db.jpa.JPA;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

/**
 * Test the page that manages the list of employees.
 * 
 * @author florian
 * 
 */
public class EmployeesTest extends FunctionalTest {

	@Before
	public void setUp() {
		Logger.debug("Reloading all data");
		Fixtures.deleteAll();
		Fixtures.load("data.yml");
	}

	@Test
	public void testBullshit() {
		Request request = Http.Request.current();
		Logger.debug("current HTTP request %s", request.toString());
		EntityManager em = JPA.em();
		Logger.debug("current em %s", em.toString());
		HttpResponse response = WS.url(
				"http://www.w3schools.com/webservices/tempconvert.asmx?op=FahrenheitToCelsius")
				.get();
		List<Person> findAll = Person.findAll();
		long count = Person.count();
		assertEquals(3, count);
		List<Person> allAntos = Person.find("byFirstNameLike", "anto%").fetch();
		assertEquals(2, allAntos.size());
		Logger.debug("All antonios %s", allAntos.toString());

	}

	@Test
	public void testStartState() throws Exception {
		Response response = GET("/");
		// test the 200 status
		assertIsOk(response);
		String httpBody = getContent(response);
		assertTrue(httpBody.length() > 0);
		// test that 3 <li> are returned
		assertContentMatch("(?s)<ul>\\s*" +
							"<li>Florian Boulay.*" +
							"<li>Antonio Goncalves.*" +
							"<li>Antoine Ramponi.*" +
							"</ul>", response);
	}

	@Test
	public void testOnCallPeople() throws Exception {
		// make Antonio Goncalves on call
		Person antonio = Person.find("byFirstName", "Antonio").<Person> first();
		Response response = PUT("/person/onCall/" + antonio.id, "text/html", "");
		// test the 302 response
		assertStatus(302, response);
		String httpBody = getContent(response);
		assertTrue(httpBody.length() == 0);
		java.net.URL locationHeader = new java.net.URL(response.getHeader("location"));
		response = GET(locationHeader.getPath());
		// test the 200 response
		httpBody = getContent(response);
		assertTrue(httpBody.length() > 0);
		assertContentMatch("(?s)<ul>\\s*" +
							"<li>Florian Boulay.*" +
							"<li>Antoine Ramponi.*" +
							"</ul>", response);
		assertContentMatch("(?s)<ul>\\s*" +
							"<li>Antonio Goncalves.*" +
							"</ul>", response);
	}

}
