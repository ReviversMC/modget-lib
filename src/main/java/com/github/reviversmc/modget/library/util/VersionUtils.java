package com.github.reviversmc.modget.library.util;

import com.github.reviversmc.modget.library.fabricmc.loader.api.SemanticVersion;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;

public class VersionUtils {

	public static VersionUtils create() {
		return new VersionUtils();
	}


	public boolean doVersionsMatch(String version1, String version2) throws VersionParsingException {
		SemanticVersion version1Semantic = SemanticVersion.parse(version1);
		SemanticVersion version2Semantic = SemanticVersion.parse(version2);

		return doVersionsMatch(version1Semantic, version2Semantic);
	}

	public boolean doVersionsMatch(SemanticVersion version1, SemanticVersion version2) {
		if (!isVersionGreaterThan(version1, version2) && !isVersionGreaterThan(version2, version1)) {
			return true;
		}
		return false;
	}


	public boolean isVersionGreaterThan(String version1, String version2) throws VersionParsingException {
		SemanticVersion version1Semantic = SemanticVersion.parse(version1);
		SemanticVersion version2Semantic = SemanticVersion.parse(version2);

		return isVersionGreaterThan(version1Semantic, version2Semantic);
	}

	public boolean isVersionGreaterThan(SemanticVersion version1, SemanticVersion version2) {
		if (version1.compareTo(version2) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
}
