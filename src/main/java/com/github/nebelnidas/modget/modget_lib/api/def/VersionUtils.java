package com.github.nebelnidas.modget.modget_lib.api.def;

import com.github.nebelnidas.modget.modget_lib.fabricmc.loader.api.SemanticVersion;
import com.github.nebelnidas.modget.modget_lib.fabricmc.loader.api.VersionParsingException;

public interface VersionUtils {

	public boolean doVersionsMatch(String version1, String version2) throws VersionParsingException;
	public boolean doVersionsMatch(SemanticVersion version1, SemanticVersion version2);

	public boolean isVersionGreaterThan(String version1, String version2) throws VersionParsingException;
	public boolean isVersionGreaterThan(SemanticVersion version1, SemanticVersion version2);

}
