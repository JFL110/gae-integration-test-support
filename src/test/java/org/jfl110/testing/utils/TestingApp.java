package org.jfl110.testing.utils;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The definition of the testing App.
 *
 * @author JFL110
 */
class TestingApp extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ServletModule() {
			@SuppressWarnings("serial")
			@Override
			protected void configureServlets() {

				// Set-up Objectify
				ObjectifyService.register(SomeEntity.class);
				bind(ObjectifyFilter.class).in(Singleton.class);
				filter("/*").through(ObjectifyFilter.class);

				serve("/read-text").with(new HttpServlet() {
					@Override
					protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
						resp.getWriter().print("text-response");
					}
				});

				serve("/insert-entities").with(new HttpServlet() {
					@Override
					protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
						ObjectifyService.ofy().save()
								.entities(new SomeEntity("entity-one"), new SomeEntity("entity-two"), new SomeEntity("entity-three"))
								.now();

						resp.getWriter().print("entities-saved");
					}
				});

				serve("/read-entities").with(new HttpServlet() {
					@Override
					protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
						resp.setContentType(MediaType.APPLICATION_JSON);
						
						new ObjectMapper().writer().writeValue(resp.getWriter(),
								new EntityListResponse(ObjectifyService.ofy().load().type(SomeEntity.class).list()));
					}
				});
			}
		});
	}


	public static class EntityListResponse {
	
		public final List<SomeEntity> entities;

		EntityListResponse(List<SomeEntity> entities) {
			this.entities = entities;
		}
	}


	@Entity
	public static class SomeEntity {
		@Id
		public Long		id;
		public String	name;

		SomeEntity() {}

		SomeEntity(String name) {
			this.name = name;
		}
	}
}
