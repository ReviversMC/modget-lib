package com.github.reviversmc.modget.library.util;

import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoCompatibleVersionException;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ModVersionVariantUtils {

	public static ModVersionVariantUtils create() {
		return new ModVersionVariantUtils();
	}


	/**
	 * Checks if a {@link ModVersionVariant} is compatible with a given game version.
	 */
	public boolean isModVersionVariantCompatible(@NonNull ModVersionVariant modVersionVariant, @NonNull String gameVersion, @NonNull String modLoader) throws VersionParsingException {
		if (!modVersionVariant.getLoaders().contains(modLoader.toLowerCase())) {
			// Loader not compatible
			return false;
		}
		for (String supportedGameVersion : modVersionVariant.getMinecraftVersions()) {
			try {
				if (VersionUtils.create().doVersionsMatch(supportedGameVersion, gameVersion)) {
					return true;
				}
			} catch (VersionParsingException e) {
				ModgetLib.logWarn(String.format("Couldn't check if version %s of package Repo%s.%s is compatible with current game version, because it doesn't respect semantic versioning!",
					supportedGameVersion, modVersionVariant.getParentVersion().getParentManifest().getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					modVersionVariant.getParentVersion().getParentManifest().getParentPackage().getPackageId()
				), e.getMessage());
				throw e;
			}
		}
		return false;
	}


	/**
	 * Gets the latest {@link ModVersionVariant} compatible with a given game version.
	 */
	public ModVersionVariant getLatestCompatibleVersionVariant(@NonNull List<ModVersionVariant> allModVersionVariants, String gameVersion, String modLoader) throws NoCompatibleVersionException {
		ModVersionVariant latestModVersionVariant = null;

		for (ModVersionVariant modVersionVariant : allModVersionVariants) {
			ModVersion modVersion = modVersionVariant.getParentVersion();
			ModManifest modManifest = modVersion.getParentManifest();
			try {
				if (isModVersionVariantCompatible(modVersionVariant, gameVersion, modLoader)) {
					if (latestModVersionVariant == null) {
						latestModVersionVariant = modVersionVariant;
						continue;
					}
					if (VersionUtils.create().isVersionGreaterThan(modVersion.getVersion(),
							latestModVersionVariant.getParentVersion().getVersion())) {
						latestModVersionVariant = modVersionVariant;
					}
				}
			} catch (VersionParsingException e) {
				ModgetLib.logWarn(String.format("Couldn't check if version %s of package Repo%s.%s is compatible with current game version, because it doesn't respect semantic versioning!",
					modVersion.getVersion(), modManifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					modManifest.getParentPackage().getPackageId()
				), e.getMessage());
			}
		}

		if (latestModVersionVariant == null) {
			throw new NoCompatibleVersionException();
		}
		return latestModVersionVariant;
	}

}
