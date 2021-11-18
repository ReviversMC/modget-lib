package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoCompatibleVersionException;
import com.github.reviversmc.modget.library.fabricmc.loader.api.SemanticVersion;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;

import org.apache.commons.text.WordUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ModVersionUtils {

	public static ModVersionUtils create() {
		return new ModVersionUtils();
	}



	/**
	 * Checks if a {@link ModVersion} is compatible with a given game version.
	 *
	 * @param modVersion	the {@link ModVersion} object to be checked
	 * @param gameVersion	String representation of the current game version
	 * @return boolean
	 * @throws VersionParsingException
	 */
	public boolean isModVersionCompatible(@NonNull ModVersion modVersion, @NonNull String gameVersion) throws VersionParsingException {
		for (String supportedGameVersion : modVersion.getMinecraftVersions()) {
			try {
				if (VersionUtils.create().doVersionsMatch(supportedGameVersion, gameVersion)) {
					return true;
				}
			} catch (VersionParsingException e) {
				ModgetLib.logWarn(String.format("Couldn't check if version %s of package Repo%s.%s is compatible with current game version, because it doesn't respect semantic versioning!",
					supportedGameVersion, modVersion.getParentManifest().getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					modVersion.getParentManifest().getParentPackage().getPackageId()
				), e.getMessage());
				throw e;
			}

		}
		return false;
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



	/**
	 * Gets the latest {@link ModVersion} compatible with a given game version.
	 *
	 * @param allModVersions				the {@link List} of {@link ModVersion} objects to be checked
	 * @param gameVersion					String representation of the current game version
	 * @param stopOnVersionParsingException	whether or not to throw an exception if a version number couldn't be parsed
	 * @return ModVersion
	 * @throws NoCompatibleVersionException
	 * @throws VersionParsingException
	 */
	public ModVersion getLatestCompatibleVersion(@NonNull List<ModVersion> allModVersions, String gameVersion, boolean stopOnVersionParsingException) throws NoCompatibleVersionException, VersionParsingException {
		List<ModVersion> compatibleVersions = new ArrayList<>();
		for (ModVersion modVersion : allModVersions) {
			try {
				if (isModVersionCompatible(modVersion, gameVersion)) {
					compatibleVersions.add(modVersion);
				}
			} catch (VersionParsingException e) {
				ModgetLib.logWarn(String.format("Couldn't check if version %s of package Repo%s.%s is compatible with current game version, because it doesn't respect semantic versioning!",
					gameVersion, modVersion.getParentManifest().getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					modVersion.getParentManifest().getParentPackage().getPackageId()
				), e.getMessage());

				if (stopOnVersionParsingException == true) {
					throw e;
				}
			}
		}

		if (compatibleVersions.size() == 0) {
			throw new NoCompatibleVersionException();
		}
		return getLatestVersion(compatibleVersions);
	}



	/**
	 * Gets all available updates of a given {@link InstalledMod} for a given game version.
	 *
	 * @param mod			the {@link InstalledMod} to be checked
	 * @param gameVersion	String representation of the current game version
	 * @return List<ModVersion>
	 */
	public List<ModVersion> getModUpdates(InstalledMod mod, String gameVersion, boolean stopOnVersionParsingException) {
		List<ModVersion> modVersionUpdates = new ArrayList<>();

		if (mod.getAvailablePackages().size() > 1 || mod.getAvailablePackages().get(0).getManifests().size() > 1) {
			ModgetLib.logInfo(String.format("There are multiple packages available for %s", WordUtils.capitalize(mod.getId())));
		}
		for (int j = 0; j < mod.getAvailablePackages().size(); j++) {
			ModPackage pack = mod.getAvailablePackages().get(j);

			for (ModManifest manifest : pack.getManifests()) {
				String packageId = String.format("Repo%s.%s",
					manifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					pack.getPackageId()
				);

				ModVersion latestModVersion = null;;
				try {
					latestModVersion = getLatestCompatibleVersion(manifest.getVersions(), gameVersion, false);
				} catch (NoCompatibleVersionException e) {
					ModgetLib.logInfo(String.format("No update has been found at %s", packageId));
					break;
				} catch (VersionParsingException e) {
					// Save to ignore because we already defined above that this error shouldn't be thrown
				}

				// Try parsing the semantic manifest and mod versions
				SemanticVersion currentVersionSemantic;
				try {
					currentVersionSemantic = SemanticVersion.parse(mod.getInstalledVersion());
				} catch (VersionParsingException e) {
					ModgetLib.logWarn(String.format("%s doesn't respect semantic versioning, an update check is therefore not possible! %s", manifest.getName(), e.getMessage()));
					break;
				}

				SemanticVersion latestVersionSemantic;
				try {
					latestVersionSemantic = SemanticVersion.parse(latestModVersion.getVersion());
				} catch (VersionParsingException e) {
					ModgetLib.logWarn(String.format("The %s manifest doesn't respect semantic versioning, an update check is therefore not possible!", manifest.getName()), e.getMessage());
					continue;
				}

				// Check for updates
				if (VersionUtils.create().isVersionGreaterThan(latestVersionSemantic, currentVersionSemantic)) {
					ModgetLib.logInfo(String.format("Found an update for %s: %s %s", manifest.getName(),
						packageId, latestVersionSemantic.toString()));

					modVersionUpdates.add(latestModVersion);
				} else {
					ModgetLib.logInfo(String.format("No update has been found at %s", packageId));
				}
			}
		}

		return modVersionUpdates;
	}

}
