package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoCompatibleVersionException;
import com.github.reviversmc.modget.library.fabricmc.loader.api.SemanticVersion;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ModUpdateChecker {

	public static ModUpdateChecker create() {
		return new ModUpdateChecker();
	}


	/**
	 * Gets the latest available {@link ModVersionVariant} with updates from a {@link InstalledMod} for a given game version and mod loader.
	 */
	public Pair<ModVersionVariant, List<Exception>> searchForModUpdate(InstalledMod mod, List<ManifestRepository> repos, String gameVersion, String modLoader) throws Exception {
		List<ModVersionVariant> updatedModVersionVariants = new ArrayList<>(15);
		List<Exception> exceptions = new ArrayList<>(10);

		// If the mod's version doesn't follow SemVer, we can't compare it
		SemanticVersion installedVersionSemantic;
		try {
			installedVersionSemantic = SemanticVersion.parse(mod.getInstalledVersion());
		} catch (VersionParsingException e) {
			ModgetLib.logWarn(String.format("%s doesn't respect semantic versioning, an update check is therefore not possible! %s", mod.getId(), e.getMessage()));
			throw e;
		}

		// TODO: the following block can be removed when `isEnabled` is removed from the API
		List<ManifestRepository> enabledRepos = repos;
		for (ManifestRepository repo : repos) {
			if (repo.isEnabled() == false) {
				enabledRepos.remove(repo);
			}
		}
		repos = enabledRepos;
		// --------------------------------------------

		// Get all packages
		List<ModPackage> modPackages;
		try {
			modPackages = mod.getOrDownloadAvailablePackages(repos);
		} catch (Exception e) {
			ModgetLib.logWarn(String.format("An error occurred while downloading the packages for mod ", mod.getId()), ExceptionUtils.getStackTrace(e));
			throw e;
		}

		// Loop over them
		for (ModPackage modPackage : modPackages) {
			// Get all manifests
			List<ModManifest> modManifests;
			try {
				modManifests = modPackage.getOrDownloadManifests(repos);
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while downloading the manifests for package ", modPackage.getPackageId()), ExceptionUtils.getStackTrace(e));
				exceptions.add(e);
				continue;
			}

			// Loop over them
			for (ModManifest modManifest : modManifests) {
				String packageIdWithRepo = String.format("Repo%s.%s",
					modManifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					modPackage.getPackageId()
				);
				// Get all mod versions
				List<ModVersion> modVersions;
				try {
					modVersions = modManifest.getOrDownloadVersions();
				} catch (Exception e) {
					ModgetLib.logWarn(String.format("An error occurred while downloading the versions", packageIdWithRepo), ExceptionUtils.getStackTrace(e));
					exceptions.add(e);
					continue;
				}

				// Loop over them
				for (ModVersion modVersion : modVersions) {
					ModVersionVariant modVersionVariant;

					try {
						modVersionVariant = ModVersionVariantUtils.create().getLatestCompatibleVersionVariant(modVersion.getVariants(), gameVersion, modLoader);
					} catch (NoCompatibleVersionException e) {
						ModgetLib.logInfo(String.format("No update has been found at %s", packageIdWithRepo));
						continue;
					}

					// Try to parse the semantic modVersion version
					SemanticVersion currentVersionSemantic;
					try {
						currentVersionSemantic = SemanticVersion.parse(modVersion.getVersion());
					} catch (VersionParsingException e) {
						ModgetLib.logWarn(String.format("%s %s doesn't respect semantic versioning, an update check is therefore not possible!", packageIdWithRepo, modVersion.getVersion()), e.getMessage());
						exceptions.add(e);
						continue;
					}

					// Check for updates
					if (VersionUtils.create().isVersionGreaterThan(currentVersionSemantic, installedVersionSemantic)) {
						ModgetLib.logInfo(String.format("Found an update for %s: %s %s", modManifest.getName(),
							packageIdWithRepo, currentVersionSemantic.getFriendlyString()));

						updatedModVersionVariants.add(modVersionVariant);
					} else {
						ModgetLib.logInfo(String.format("No update has been found at %s", packageIdWithRepo));
					}
				}
			}
		}

		ModVersionVariant latestModVersionVariant;
		try {
			latestModVersionVariant = ModVersionVariantUtils.create().getLatestCompatibleVersionVariant(updatedModVersionVariants, gameVersion, modLoader);
		} catch (NoCompatibleVersionException e) {
			// Shouldn't happen, because we only add compatible versions to the list
			latestModVersionVariant = null;
		}
		return new ImmutablePair<>(latestModVersionVariant, exceptions);
	}

}
