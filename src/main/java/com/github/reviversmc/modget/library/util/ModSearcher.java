package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoCompatibleVersionException;
import com.github.reviversmc.modget.library.fabricmc.loader.api.VersionParsingException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.lookuptable.LookupTable;
import com.github.reviversmc.modget.manifests.spec4.api.data.lookuptable.LookupTableEntry;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ModSearcher {

	public static ModSearcher create() {
		return new ModSearcher();
	}


	public Pair<List<ModVersionVariant>, List<Exception>> searchForCompatibleMods(List<ManifestRepository> repos, String term,
			int charsNeededForExtensiveSearch, String gameVersion, String modLoader) {

		float multiplier = 1;
		List<ModVersionVariant> versionVariantsFound;
		List<ModVersionVariant> versionVariantsFoundPriority0;
		List<ModVersionVariant> versionVariantsFoundPriority1 = null;
		List<ModVersionVariant> versionVariantsFoundPriority2 = null;
		List<Exception> exceptions = new ArrayList<>(10);

		if (term.length() >= charsNeededForExtensiveSearch) {
			multiplier += charsNeededForExtensiveSearch / term.length();
			versionVariantsFoundPriority1 = new ArrayList<>(Math.round(4 * multiplier));
			versionVariantsFoundPriority2 = new ArrayList<>(Math.round(4 * multiplier));
		}
		versionVariantsFound = new ArrayList<>(Math.round(8 * multiplier));
		versionVariantsFoundPriority0 = new ArrayList<>(Math.round(4 * multiplier));


		for (ManifestRepository repo : repos) {
			// Get the lookup table
			LookupTable lookupTable;
			try {
				lookupTable = repo.getOrDownloadLookupTable();
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while downloading the lookup table for Repo%s", repo.getId()), ExceptionUtils.getStackTrace(e));
				exceptions.add(e);
				continue;
			}
			// Get the lookup table entries
			List<LookupTableEntry> lookupTableEntries;
			try {
				lookupTableEntries = lookupTable.getOrDownloadEntries();
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while downloading the lookup table entries for Repo%s", repo.getId()), ExceptionUtils.getStackTrace(e));
				exceptions.add(e);
				continue;
			}

			for (LookupTableEntry entry : lookupTableEntries) {
				// Get the entry's packages
				List<ModPackage> modPackages;
				try {
					modPackages = entry.getOrDownloadPackages();
				} catch (Exception e) {
					ModgetLib.logWarn(String.format("An error occurred while downloading the packages for mod ", entry.getId()), ExceptionUtils.getStackTrace(e));
					exceptions.add(e);
					continue;
				}

				boolean recognized = false;
				int priority = 0;

				// Does the packageId match?
				for (ModPackage modPackage : modPackages) {
					if (modPackage.getPackageId().equalsIgnoreCase(term)) {
						recognized = true;
					}
				}
				// Does the modId match?
				if (recognized == false && entry.getId().equalsIgnoreCase(term)) {
					recognized = true;
				}
				// Does an alternative name match?
				if (recognized == false) {
					for (String name : entry.getAlternativeNames()) {
						if (name.equalsIgnoreCase(term)) {
							recognized = true;
						}
					}
				}
				// Does the term contain parts of the packageId?
				if (recognized == false && term.length() >= charsNeededForExtensiveSearch) {
					for (ModPackage modPackage : modPackages) {
						if (modPackage.getPackageId().toLowerCase().contains(term.toLowerCase())) {
							recognized = true;
							priority = 1;
						}
					}
				}
				// Does the term contain parts of an alternative name?
				if (recognized == false && term.length() >= charsNeededForExtensiveSearch) {
					for (String name : entry.getAlternativeNames()) {
						if (name.toLowerCase().contains(term.toLowerCase())) {
							recognized = true;
							priority = 2;
						}
					}
				}
				// Does the term contain parts of a tag?
				if (recognized == false && term.length() >= charsNeededForExtensiveSearch) {
					for (String tag : entry.getTags()) {
						if (tag.toLowerCase().contains(term.toLowerCase())) {
							recognized = true;
							priority = 2;
						}
					}
				}

				if (recognized == true) {
					ModVersionVariant latestVersionVariant = null;

					for (ModPackage modPackage : modPackages) {
						// Get all manifests
						List<ModManifest> modManifests;
						try {
							modManifests = modPackage.getOrDownloadManifests(Arrays.asList(repo));
						} catch (Exception e) {
							ModgetLib.logWarn(String.format("An error occurred while downloading the manifests for package ", modPackage.getPackageId()), ExceptionUtils.getStackTrace(e));
							exceptions.add(e);
							continue;
						}

						for (ModManifest modManifest : modManifests) {
							String packageIdWithRepo = String.format("Repo%s.%s",
								repo.getId(), modPackage.getPackageId()
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

							for (ModVersion modVersion : modVersions) {
								try {
									if (latestVersionVariant != null &&
											VersionUtils.create().isVersionGreaterThan(
													latestVersionVariant.getParentVersion().getVersion(),
													modVersion.getVersion())) {
										continue;
									}
								} catch (VersionParsingException e) {
									ModgetLib.logWarn(String.format("%s %s doesn't respect semantic versioning, an update check is therefore not possible!", packageIdWithRepo, modVersion.getVersion()), e.getMessage());
									exceptions.add(e);
									continue;
								}

								try {
									latestVersionVariant = ModVersionVariantUtils.create()
											.getLatestCompatibleVersionVariant(modVersion.getVariants(), gameVersion,
													modLoader);
								} catch (NoCompatibleVersionException e) {
									ModgetLib.logInfo(String.format(
											"Repo%s.%s %s has been found, but it's incompatible with the current game version",
											repo.getId(), modPackage.getPackageId(), modVersion.getVersion()));
									continue;
								}
							}
						}
					}
					if (latestVersionVariant == null) {
						continue;
					}

					switch (priority) {
						case 0:
							versionVariantsFoundPriority0.add(latestVersionVariant);
							break;
						case 1:
							versionVariantsFoundPriority1.add(latestVersionVariant);
							break;
						case 2:
							versionVariantsFoundPriority2.add(latestVersionVariant);
							break;
					}
				}
			}
		}
		versionVariantsFound.addAll(versionVariantsFoundPriority0);
		if (term.length() >= charsNeededForExtensiveSearch) {
			versionVariantsFound.addAll(versionVariantsFoundPriority1);
			versionVariantsFound.addAll(versionVariantsFoundPriority2);
		}

		return new ImmutablePair<>(versionVariantsFound, exceptions);
	}

}