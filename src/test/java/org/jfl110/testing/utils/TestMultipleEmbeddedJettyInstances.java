package org.jfl110.testing.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Tests multiple Jetty instances in the same app. This does *not* work with
 * multiple GuiceContextListeners.
 */
public class TestMultipleEmbeddedJettyInstances {

	@Rule public final EmbeddedJetty serverOne = EmbeddedJetty.embeddedJetty().withPort(8084)
			.addServlet("/the-path", servingString("ServerOneResponse")).withResourceBasePath("/src/test/").build();
	@Rule public final EmbeddedJetty serverTwo = EmbeddedJetty.embeddedJetty().withPort(8086)
			.addServlet("/the-path", servingString("ServerTwoResponse")).withResourceBasePath("/src/test/").build();
	@Rule public final EmbeddedJetty serverThree = EmbeddedJetty.embeddedJetty().withPort(8088)
			.withContextListener(new GuiceServletContextListener() {
				@Override
				protected Injector getInjector() {
					return Guice.createInjector(new ServletModule() {
						@Override
						protected void configureServlets() {
							serve("/the-path").with(servingString("ServerThreeResponse"));
							
						};
					});
				}
			}).withResourceBasePath("/src/test/").build();


	@Test
	public void test() {
		assertEquals("ServerOneResponse",
				ClientBuilder.newClient().target(serverOne.getBaseUri() + "the-path").request(MediaType.TEXT_PLAIN).get(String.class));
		assertEquals("ServerTwoResponse",
				ClientBuilder.newClient().target(serverTwo.getBaseUri() + "the-path").request(MediaType.TEXT_PLAIN).get(String.class));
		assertEquals("ServerThreeResponse",
				ClientBuilder.newClient().target(serverThree.getBaseUri() + "the-path").request(MediaType.TEXT_PLAIN).get(String.class));
	}


	@SuppressWarnings("serial")
	private HttpServlet servingString(String str) {
		return new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				resp.getWriter().print(str);
			}
		};
	}
}