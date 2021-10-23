package com.github.nebelnidas.modget.modget_lib.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.Package;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.manifest.Manifest;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.manifest.ModVersion;
import com.github.nebelnidas.modget.modget_lib.ModgetLib;
import com.github.nebelnidas.modget.modget_lib.api.def.ModVersionUtils;
import com.github.nebelnidas.modget.modget_lib.api.exception.NoCompatibleVersionException;
import com.github.nebelnidas.modget.modget_lib.fabricmc.loader.api.SemanticVersion;
import com.github.nebelnidas.modget.modget_lib.fabricmc.loader.api.VersionParsingException;

import org.apache.commons.text.WordUtils;

public class ModVersionUtilsImpl implements ModVersionUtils {

	public static ModVersionUtilsImpl create() {
		return new ModVersionUtilsImpl();
	}


	@Override
	public List<ModVersion> getCompatibleVersions(List<ModVersion> allVersions, String gameVersion) {
		if (allVersions == null) {
			return null;
		}
		List<ModVersion> compatibleVersions = new ArrayList<>();

		for (ModVersion version : allVersions) {
			for (String supportedVersion : version.getMinecraftVersions()) {

				try {
					if (VersionUtilsImpl.create().doVersionsMatch(supportedVersion, gameVersion)) {
						compatibleVersions.add(version);
					}
				} catch (VersionParsingException e) {
					ModgetLib.logWarn(String.format("Couldn't check if version %s of package Repo%s.%s.%s is compatible with current game version, because it doesn't respect semantic versioning!",
						supportedVersion, version.getParentManifest().getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
						version.getParentManifest().getParentPackage().getPublisher(), version.getParentManifest().getParentPackage().getId()
					), e.getMessage());
				}

			}
		}
		return compatibleVersions;
	}

	@Override
	public ModVersion getLatestVersion(List<ModVersion> versions) {
		if (versions == null) {
			ModgetLib.logWarn("Cannot look for the latest mod version, because no available versions have been defined!");
			return null;
		}
		ModVersion latestVersion = versions.get(0);

		for (ModVersion version : versions) {
			try {
				if (VersionUtilsImpl.create().isVersionGreaterThan(version.getVersion(), latestVersion.getVersion())) {
					latestVersion = version;
				}
			} catch (VersionParsingException e) {
				ModgetLib.logWarn(String.format("Cannot compare %s, because it doesn't comply with semantic versioning!", version.getVersion()));
			}
		}
		return latestVersion;
	}


	@Override
	public ModVersion getLatestCompatibleVersion(List<ModVersion> allVersions, String gameVersion) throws NoCompatibleVersionException {
		if (allVersions == null) {
			return null;
		}
		List<ModVersion> compatibleVersions = getCompatibleVersions(allVersions, gameVersion);

		if (compatibleVersions.size() == 0) {
			throw new NoCompatibleVersionException();
		}
		return getLatestVersion(compatibleVersions);
	}


	@Override
	public List<RecognizedMod> getModsWithUpdates(List<RecognizedMod> mods, String gameVersion) {
		List<RecognizedMod> modsWithUpdates = new ArrayList<>();

		for (RecognizedMod mod : mods) {
			List<ModVersion> modVersionUpdates = new ArrayList<>();
			mod.resetUpdates();

			if (mod.getAvailablePackages().size() > 1 || mod.getAvailablePackages().get(0).getManifests().size() > 1) {
				ModgetLib.logInfo(String.format("There are multiple packages available for %s", WordUtils.capitalize(mod.getId())));
			}
			for (int j = 0; j < mod.getAvailablePackages().size(); j++) {
				Package pack = mod.getAvailablePackages().get(j);

				for (Manifest manifest : pack.getManifests()) {
					ModVersion latestModVersion;
					try {
						latestModVersion = getLatestCompatibleVersion(manifest.getDownloads(), gameVersion);
					} catch (NoCompatibleVersionException e1) {
						break;
					}

					// Try parsing the semantic manifest and mod versions
					SemanticVersion currentVersionSemantic;
					try {
						currentVersionSemantic = SemanticVersion.parse(mod.getCurrentVersion());
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
					String packageId = String.format("Repo%s.%s.%s",
						manifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
						pack.getPublisher(), manifest.getParentLookupTableEntry().getId());

					if (VersionUtilsImpl.create().isVersionGreaterThan(latestVersionSemantic, currentVersionSemantic)) {
						ModgetLib.logInfo(String.format("Found an update for %s: %s %s", manifest.getName(),
							packageId, latestVersionSemantic.toString()));

						modVersionUpdates.add(latestModVersion);
					} else {
						ModgetLib.logInfo(String.format("No update has been found at %s", packageId));
					}
				}
			}

			if (modVersionUpdates.size() > 0) {
				RecognizedMod modWithUpdate = mod;
				for (ModVersion version : modVersionUpdates) {
					modWithUpdate.addUpdate(version);
				}
				modsWithUpdates.add(modWithUpdate);
			}
		}

		return modsWithUpdates;
	}

}
