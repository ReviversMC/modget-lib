package com.github.nebelnidas.modget.modget_lib.api.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.nebelnidas.modget.modget_lib.fabricmc.loader.api.VersionParsingException;
import com.github.nebelnidas.modget.modget_lib.util.VersionUtils;

import org.junit.jupiter.api.Test;


public class VersionUtilsImplTest {
	VersionUtils versionUtils = new VersionUtils();

	
	@Test
	void assertVersionsWithTrailing0MatchCounterpartsWithoutTrailing0() {
		try {
			assertTrue(versionUtils.doVersionsMatch("1.17", "1.17.0"));
			assertTrue(versionUtils.doVersionsMatch("1.17", "1.17.0.0"));
			assertTrue(versionUtils.doVersionsMatch("1.17.0", "1.17.0.0"));
		} catch (VersionParsingException e) {
			e.printStackTrace();
		}
	}

	@Test
	void assertVersionsNotMatchingDontMatch() {
		try {
			assertFalse(versionUtils.doVersionsMatch("1.17", "1.17.1"));
			assertFalse(versionUtils.doVersionsMatch("1.17.0", "1.17.2"));
			assertFalse(versionUtils.doVersionsMatch("1.16", "1.17"));
			assertFalse(versionUtils.doVersionsMatch("1.17", "2.17"));
		} catch (VersionParsingException e) {
			e.printStackTrace();
		}
	}



	@Test
	void assertMatchingVersionsArentReportedAsGreater() {
		try {
			assertFalse(versionUtils.isVersionGreaterThan("2.0.0", "2.0.0"));
			assertFalse(versionUtils.isVersionGreaterThan("2.0+1.16", "2.0+1.17"));
			assertFalse(versionUtils.isVersionGreaterThan("2.0+1.17", "2.0+1.16"));
			assertFalse(versionUtils.isVersionGreaterThan("2.0-alpha.1", "2.0-alpha.1"));
			assertFalse(versionUtils.isVersionGreaterThan("1.17.0", "1.17"));
			assertFalse(versionUtils.isVersionGreaterThan("1.17", "1.17.0"));
		} catch (VersionParsingException e) {
			e.printStackTrace();
		}
	}

	@Test
	void assertGreaterVersionsAreReportedAsGreater() {
		try {
			assertTrue(versionUtils.isVersionGreaterThan("2.0.1", "2.0.0"));
			assertTrue(versionUtils.isVersionGreaterThan("2.17+1.16", "2.16+1.17"));
			assertTrue(versionUtils.isVersionGreaterThan("2.2+1.17", "2.1+1.16"));
			assertTrue(versionUtils.isVersionGreaterThan("2.0-alpha.2", "2.0-alpha.1"));
			assertTrue(versionUtils.isVersionGreaterThan("1.17.1", "1.17"));
			assertTrue(versionUtils.isVersionGreaterThan("1.17.1", "1.17.0"));
		} catch (VersionParsingException e) {
			e.printStackTrace();
		}
	}

	@Test
	void assertLesserVersionsArentReportedAsGreater() {
		try {
			assertFalse(versionUtils.isVersionGreaterThan("2.0.0", "2.0.1"));
			assertFalse(versionUtils.isVersionGreaterThan("2.16+1.16", "2.17+1.17"));
			assertFalse(versionUtils.isVersionGreaterThan("2.1+1.17", "2.2+1.16"));
			assertFalse(versionUtils.isVersionGreaterThan("2.0-alpha.1", "2.0-alpha.2"));
			assertFalse(versionUtils.isVersionGreaterThan("1.17", "1.17.1"));
			assertFalse(versionUtils.isVersionGreaterThan("1.17.0", "1.17.1"));
		} catch (VersionParsingException e) {
			e.printStackTrace();
		}
	}

}