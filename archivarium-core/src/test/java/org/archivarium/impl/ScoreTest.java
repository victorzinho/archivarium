package org.archivarium.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.archivarium.Score;
import org.junit.Test;

public class ScoreTest {
	@Test
	public void equals() throws Exception {
		Score s1 = new DefaultScore();
		s1.setGenre("g1");
		s1.setTitle("n1");
		s1.setComposer("a1");

		Score s2 = new DefaultScore();
		s2.setGenre("g1");
		s2.setTitle("n1");
		s2.setComposer("a1");

		assertEquals(s1, s2);
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void notEquals() throws Exception {
		Score s1 = new DefaultScore();
		s1.setGenre("g1");
		s1.setTitle("n1");
		s1.setComposer("a1");

		Score s2 = new DefaultScore();
		s2.setGenre("g1");
		s2.setTitle("n2");
		s2.setComposer("a1");

		assertNotSame(s1, s2);
		assertNotSame(s1.hashCode(), s2.hashCode());
	}
}
