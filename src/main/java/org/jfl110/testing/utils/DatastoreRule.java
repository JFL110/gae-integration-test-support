package org.jfl110.testing.utils;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;

/**
 * Testing rule to provide access to the GAE DataStore.
 * Supports multithread environments but the results are not guaranteed.
 *
 * @author JFL110
 */
public class DatastoreRule implements TestRule {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					helper.setUp();
					syncDatastoreThreads();
					base.evaluate();
				} finally {
					helper.tearDown();
				}
			}
		};
	}
	
	
	/**
	 * Warning, might have unexpected results
	 */
	private void syncDatastoreThreads(){
		final ApiProxy.Environment testEnv = ApiProxy.getCurrentEnvironment();
		try{
			ApiProxy.setEnvironmentFactory(new ApiProxy.EnvironmentFactory() {
			  @Override
			  public ApiProxy.Environment newEnvironment() { return testEnv; }
			});
		}catch(IllegalStateException e){}
	}
	
	
	public LocalServiceTestHelper helper(){
		return helper;
	}
}