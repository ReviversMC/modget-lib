package com.github.nebelnidas.modget.modget_lib.api.impl;

import com.github.nebelnidas.modget.modget_lib.api.def.VersionUtils;
import com.github.nebelnidas.modget.modget_lib.fabricmc.loader.api.SemanticVersion;
import com.github.nebelnidas.modget.modget_lib.fabricmc.loader.api.VersionParsingException;

public class VersionUtilsImpl implements VersionUtils {

	public static VersionUtilsImpl create() {
		return new VersionUtilsImpl();
	}

	@Override
	public boolean doVersionsMatch(String version1, String version2) throws VersionParsingException {
		SemanticVersion version1Semantic = SemanticVersion.parse(version1);
		SemanticVersion version2Semantic = SemanticVersion.parse(version2);

		return doVersionsMatch(version1Semantic, version2Semantic);
	}

	@Override
	public boolean doVersionsMatch(SemanticVersion version1, SemanticVersion version2) {
		if (!isVersionGreaterThan(version1, version2) && !isVersionGreaterThan(version2, version1)) {
			return true;
		}
		return false;
	}


	@Override
	public boolean isVersionGreaterThan(String version1, String version2) throws VersionParsingException {
		SemanticVersion version1Semantic = SemanticVersion.parse(version1);
		SemanticVersion version2Semantic = SemanticVersion.parse(version2);

		return isVersionGreaterThan(version1Semantic, version2Semantic);
	}

	@Override
	public boolean isVersionGreaterThan(SemanticVersion version1, SemanticVersion version2) {
		if (version1.compareTo(version2) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
