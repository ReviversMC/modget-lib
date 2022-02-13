package com.github.reviversmc.modget.library.util;

import javax.annotation.Nonnull;

import com.github.reviversmc.modget.library.fabricmc.loader.api.SemanticVersion;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class VersionUtils {

	public static VersionUtils create() {
		return new VersionUtils();
	}


	public boolean doVersionsMatch(
			@NonNull @Nonnull String version1,
			@NonNull @Nonnull String version2
	) throws VersionParsingException {
		SemanticVersion version1Semantic = SemanticVersion.parse(version1);
		SemanticVersion version2Semantic = SemanticVersion.parse(version2);

		return doVersionsMatch(version1Semantic, version2Semantic);
	}


	public boolean doVersionsMatch(
		@NonNull @Nonnull SemanticVersion version1,
		@NonNull @Nonnull SemanticVersion version2
	) {
		if (!isVersionGreaterThan(version1, version2) && !isVersionGreaterThan(version2, version1)) {
			return true;
		}
		return false;
	}


	public boolean isVersionGreaterThan(
			@NonNull @Nonnull String version1,
			@NonNull @Nonnull String version2
	) throws VersionParsingException {
		SemanticVersion version1Semantic = SemanticVersion.parse(version1);
		SemanticVersion version2Semantic = SemanticVersion.parse(version2);

		return isVersionGreaterThan(version1Semantic, version2Semantic);
	}


	public boolean isVersionGreaterThan(
			@NonNull @Nonnull SemanticVersion version1,
			@NonNull @Nonnull SemanticVersion version2
	) {
		if (version1.compareTo(version2) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
}
