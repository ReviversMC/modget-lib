package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.data.ModUpdate;
import com.github.reviversmc.modget.library.exception.NoCompatibleVersionException;
import com.github.reviversmc.modget.library.fabricmc.loader.api.SemanticVersion;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModLoader;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ModUpdateChecker {

	public static ModUpdateChecker create() {
		return new ModUpdateChecker();
	}


	/**
	 * Gets the latest available {@link ModVersionVariant} with updates from a
	 * {@link InstalledMod} for a given game version and mod loader.
	 */
	public Pair<ModUpdate, List<Exception>> searchForModUpdate(
			@NonNull @Nonnull InstalledMod installedMod,
			@NonNull @Nonnull List<ManifestRepository> repos,
			@NonNull @Nonnull String gameVersion,
			@NonNull @Nonnull ModLoader modLoader
	) throws Exception {
		List<Pair<ManifestRepository, List<ModVersionVariant>>> updatedModVersionVariants = new ArrayList<>(15);
		List<Exception> exceptions = new ArrayList<>(10);

		// If the mod's version doesn't follow SemVer, we can't compare it
		SemanticVersion installedVersionSemantic;
		try {
			installedVersionSemantic = SemanticVersion.parse(installedMod.getInstalledVersion());
		} catch (VersionParsingException e) {
			ModgetLib.logWarn(String.format(
					"%s doesn't respect semantic versioning, an update check is therefore not possible! %s",
					installedMod.getId(), e.getMessage()));
			throw e;
		}

		// Get all packages
		List<ModPackage> modPackages;
		try {
			modPackages = installedMod.getOrDownloadAvailablePackages(repos);
		} catch (Exception e) {
			ModgetLib.logWarn(
					String.format("An error occurred while downloading the packages for mod ", installedMod.getId()),
					ExceptionUtils.getStackTrace(e));
			throw e;
		}

		// Loop over them
		for (ModPackage modPackage : modPackages) {
			// Get all manifests
			List<ModManifest> modManifests;
			try {
				modManifests = modPackage.getOrDownloadManifests(repos);
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while downloading the manifests for package ",
						modPackage.getPackageId()), ExceptionUtils.getStackTrace(e));
				exceptions.add(e);
				continue;
			}

			// Loop over them
			for (ModManifest modManifest : modManifests) {
				String packageIdWithRepo = String.format("Repo%s.%s",
						modManifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
						modPackage.getPackageId());
				// Get all mod versions
				List<ModVersion> modVersions;
				try {
					modVersions = modManifest.getOrDownloadVersions();
				} catch (Exception e) {
					ModgetLib.logWarn(
							String.format("An error occurred while downloading the versions", packageIdWithRepo),
							ExceptionUtils.getStackTrace(e));
					exceptions.add(e);
					continue;
				}

				// Loop over them
				for (ModVersion modVersion : modVersions) {
					ModVersionVariant modVersionVariant;

					try {
						modVersionVariant = ModVersionVariantUtils.create().getLatestCompatibleVersionVariant(
								modVersion.getVariants(), Optional.of(gameVersion), Optional.of(modLoader));
					} catch (NoCompatibleVersionException e) {
						ModgetLib.logInfo(String.format("No update has been found at %s", packageIdWithRepo));
						continue;
					}

					// Try to parse the semantic modVersion version
					SemanticVersion currentVersionSemantic;
					try {
						currentVersionSemantic = SemanticVersion.parse(modVersion.getVersion());
					} catch (VersionParsingException e) {
						ModgetLib.logWarn(String.format(
								"%s %s doesn't respect semantic versioning, an update check is therefore not possible!",
								packageIdWithRepo, modVersion.getVersion()), e.getMessage());
						exceptions.add(e);
						continue;
					}

					// Check for updates
					if (VersionUtils.create().isVersionGreaterThan(currentVersionSemantic, installedVersionSemantic)) {
						ModgetLib.logInfo(String.format("Found an update for %s: %s %s", modManifest.getName(),
								packageIdWithRepo, currentVersionSemantic.getFriendlyString()));

						ManifestRepository repo = modManifest.getParentLookupTableEntry().getParentLookupTable()
								.getParentRepository();
						boolean added = false;
						for (Pair<ManifestRepository, List<ModVersionVariant>> pair : updatedModVersionVariants) {
							if (pair.getLeft().getId() == repo.getId()) {
								pair.getRight().add(modVersionVariant);
								added = true;
								break;
							}
						}
						if (added == false) {
							Pair<ManifestRepository, List<ModVersionVariant>> pair = new MutablePair<>(repo,
									new ArrayList<>(10));
							pair.getRight().add(modVersionVariant);
							updatedModVersionVariants.add(pair);
						}
					} else {
						ModgetLib.logInfo(String.format("No update has been found at %s", packageIdWithRepo));
					}
				}
			}
		}

		List<ModVersionVariant> latestUpdatedModVersionVariants = new ArrayList<>(updatedModVersionVariants.size());
		for (Pair<ManifestRepository, List<ModVersionVariant>> pair : updatedModVersionVariants) {
			ModVersionVariant latestModVersionVariantOfThisRepo;
			try {
				latestModVersionVariantOfThisRepo = ModVersionVariantUtils.create().getLatestCompatibleVersionVariant(
						pair.getRight(), Optional.of(gameVersion), Optional.of(modLoader));
			} catch (NoCompatibleVersionException e) {
				// Shouldn't happen, because we only add compatible versions to the list
				latestModVersionVariantOfThisRepo = null;
			}
			latestUpdatedModVersionVariants.add(latestModVersionVariantOfThisRepo);
		}
		return new ImmutablePair<>(new ModUpdate(installedMod, latestUpdatedModVersionVariants), exceptions);
	}

}
