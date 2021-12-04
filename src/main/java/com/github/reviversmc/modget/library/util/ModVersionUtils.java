package com.github.reviversmc.modget.library.util;

import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ModVersionUtils {

	public static ModVersionUtils create() {
		return new ModVersionUtils();
	}


	/**
	 * Gets the {@link ModVersion} with the highest version number.
	 *
	 * @param versions		the {@link List} of {@link ModVersion} objects to be checked
	 * @return ModVersion	the {@link ModVersion} with the highest version number
	 */
	public ModVersion getLatestVersion(@NonNull List<ModVersion> versions) throws VersionParsingException {
		if (versions.size() == 0) {
			ModgetLib.logWarn("Cannot look for the latest mod version, because no available versions have been defined!");
			return null;
		}
		ModVersion latestVersion = versions.get(0);

		for (ModVersion version : versions) {
			try {
				if (VersionUtils.create().isVersionGreaterThan(version.getVersion(), latestVersion.getVersion())) {
					latestVersion = version;
				}
			} catch (VersionParsingException e) {
				ModgetLib.logWarn(String.format("Cannot compare %s, because it doesn't comply with semantic versioning!", version.getVersion()));
				throw e;
			}
		}
		return latestVersion;
	}

}
