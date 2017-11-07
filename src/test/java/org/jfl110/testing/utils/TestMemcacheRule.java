package org.jfl110.testing.utils;

import static org.junit.Assert.*;

import org.junit.ClassRule;
import org.junit.Test;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Tests MemcacheRule, specifically that it works with multiple threads.
 *
 * @author JFL110
 */
public class TestMemcacheRule {

	@ClassRule
	public static final MemcacheRule memcacheRule = new MemcacheRule();

	@Test
	public void testIncrementAndGet() throws Exception {
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

		memcache.put("LongKeyOne", 1);
		assertEquals(1, memcache.get("LongKeyOne"));

		Long additionResult = memcache.increment("LongKeyOne", 3);
		assertNotNull(additionResult);
		assertEquals(4L, additionResult.longValue());

		Thread threadTwo = new Thread(() -> memcache.increment("LongKeyOne", 5));
		Thread threadThree = new Thread(() -> memcache.increment("LongKeyOne", 10));

		threadTwo.start();
		threadThree.start();
		threadTwo.join();
		threadThree.join();

		assertEquals(19, memcache.get("LongKeyOne"));
	}
}