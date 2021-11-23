package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.lookuptable.LookupTable;
import com.github.reviversmc.modget.manifests.spec4.api.data.lookuptable.LookupTableEntry;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;
import com.github.reviversmc.modget.manifests.spec4.impl.data.mod.InstalledModImpl;
import com.github.reviversmc.modget.manifests.spec4.impl.data.mod.ModPackageImpl;
import com.github.reviversmc.modget.manifests.spec4.util.ManifestUtils;
import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoCompatibleVersionException;

public class ModgetLibUtils {

	public static ModgetLibUtils create() {
		return new ModgetLibUtils();
	}

	public List<InstalledMod> scanMods(List<InstalledMod> installedMods, List<String> ignoredModIds, List<ManifestRepository> repos) throws Exception {
		List<InstalledMod> recognizedMods = new ArrayList<>();
		StringBuilder logMessage = new StringBuilder();
		int ignoredModsCount = 0;

		for (ManifestRepository repo : repos) {
			if (repo.getLookupTable() == null) {continue;}

			for (LookupTableEntry entry : repo.getLookupTable().getLookupTableEntries()) {
				// If modId is contained in ignore list, skip it
				if (ignoredModIds.contains(entry.getId())) {
					ignoredModsCount++;
					continue;
				}

				// Is the mod from the current lookup table entry even installed?
				isModFromEntryInstalledLoop:
				for (InstalledMod installedMod : installedMods) {
					// Skip if the modIds don't match
					if (!entry.getId().equalsIgnoreCase(installedMod.getId())) {
						continue;
					}

					// If the mod is installed, get all packages defined in the current lookup table entry...
					for (ModPackage currentPackage : entry.getPackages()) {
						InstalledMod recognizedModToAddTo = null;
						ModPackage packToAddTo = null;

						// ... and check if we have already added the package and a corresponding RecognizedMod
						//     earlier (from another repository).
						isModAlreadyRecognizedLoop:
						for (InstalledMod recognizedMod : recognizedMods) {
							for (ModPackage recognizedPackage : recognizedMod.getAvailablePackages()) {
								if (recognizedPackage.getPackageId().equalsIgnoreCase(currentPackage.getPackageId())) {
									// If the package already exists, add the manifest to there...
									packToAddTo = recognizedPackage;
									// ... and if the mod is already recognized, add the package to there
									recognizedModToAddTo = recognizedMod;

									break isModAlreadyRecognizedLoop;
								}
							}
						}
						// Otherwise, create...
						if (recognizedModToAddTo == null) {
							// ... a new package...
							packToAddTo = new ModPackageImpl(currentPackage.getPackageId());

							// ... and a new recognized mod.
							recognizedModToAddTo = new InstalledModImpl(packToAddTo.getModId());
							recognizedModToAddTo.setInstalledVersion(installedMod.getInstalledVersion());

							logMessage.append("; " + installedMod.getId());
						}

						// Then download and add the current manifest to the package
						try {
							packToAddTo.addManifest(ManifestUtils.create().downloadManifest(entry, packToAddTo));
						} catch (Exception e) {
							throw e;
						}

						// Finally, add the package to the recognized mod
						recognizedModToAddTo.addAvailablePackage(packToAddTo);
						recognizedMods.add(recognizedModToAddTo);
					}

					// Now skip ahead to the next lookup table entry
					break isModFromEntryInstalledLoop;
				}
			}
		}

		// Log which mods have been recognized
		if (logMessage.length() != 0) {
			logMessage.replace(0, 2, "");
			logMessage.insert(0, ": ");
		}
		ModgetLib.logInfo(String.format("Recognized %s out of %s mods%s", recognizedMods.size(), installedMods.size() - ignoredModsCount, logMessage.toString()));

		return recognizedMods;
	}



	public List<ModPackage> searchForMods(List<ManifestRepository> repos, String term, int charsNeededForExtensiveSearch, String gameVersion) throws Exception {
		float multiplier = 1;
		List<ModPackage> modsFound;
		List<ModPackage> modsFoundPriority0;
		List<ModPackage> modsFoundPriority1 = null;
		List<ModPackage> modsFoundPriority2 = null;

		if (term.length() >= charsNeededForExtensiveSearch) {
			multiplier += charsNeededForExtensiveSearch / term.length();
			modsFoundPriority1 = new ArrayList<>(Math.round(4 * multiplier));
			modsFoundPriority2 = new ArrayList<>(Math.round(4 * multiplier));
		}
		modsFound = new ArrayList<>(Math.round(8 * multiplier));
		modsFoundPriority0 = new ArrayList<>(Math.round(4 * multiplier));

		for (ManifestRepository repo : repos) {
			LookupTable lookupTable = repo.getLookupTable();

			for (LookupTableEntry entry : lookupTable.getLookupTableEntries()) {
				boolean recognized = false;
				int priority = 0;

				// Does the packageId match?
				for (ModPackage modPackage : entry.getPackages()) {
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
					for (ModPackage modPackage : entry.getPackages()) {
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
					ModPackage foundPackage = null;

					for (ModPackage modPackage : entry.getPackages()) {
						foundPackage = new ModPackageImpl(modPackage.getPackageId()) {{
							setLoaders(modPackage.getLoaders());
						}};
						ModManifest manifest;
						try {
							manifest = ManifestUtils.create().downloadManifest(entry, modPackage);
						} catch (Exception e) {
							throw e;
						}
						foundPackage.addManifest(manifest);
						try {
							ModVersion latestVersion = ModVersionUtils.create().getLatestCompatibleVersion(manifest.getVersions(), gameVersion, false);
							if (latestVersion != null) {
								foundPackage.setVersion(latestVersion.getVersion());
							}
						} catch (NoCompatibleVersionException e) {
							ModgetLib.logInfo(String.format("ModPackage Repo%s.%s has been found, but it's incompatible with the current game version",
								repo.getId(), modPackage.getPackageId()));
							continue;
						}
					}

					switch (priority) {
						case 0:
							modsFoundPriority0.add(foundPackage);
							break;
						case 1:
							modsFoundPriority1.add(foundPackage);
							break;
						case 2:
							modsFoundPriority2.add(foundPackage);
							break;
					}
				}
			}
		}
		modsFound.addAll(modsFoundPriority0);
		if (term.length() >= charsNeededForExtensiveSearch) {
			modsFound.addAll(modsFoundPriority1);
			modsFound.addAll(modsFoundPriority2);
		}

		return modsFound;
	}
}