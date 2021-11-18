package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.lookuptable.LookupTable;
import com.github.reviversmc.modget.manifests.spec4.api.data.lookuptable.LookupTableEntry;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;
import com.github.reviversmc.modget.manifests.spec4.impl.data.mod.InstalledModImpl;
import com.github.reviversmc.modget.manifests.spec4.impl.data.mod.ModPackageImpl;
import com.github.reviversmc.modget.manifests.spec4.util.ManifestUtils;
import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoCompatibleVersionException;

import org.apache.commons.text.WordUtils;

public class ModgetLibUtils {

	public static ModgetLibUtils create() {
		return new ModgetLibUtils();
	}

	public List<InstalledMod> scanMods(List<InstalledMod> mods, List<String> ignoredModIds, List<ManifestRepository> repos) throws Exception {
		List<InstalledMod> installedMods = new ArrayList<>();
		List<ModPackage> packages = new ArrayList<>();
		int ignoredModsCount = 0;
		StringBuilder logMessage = new StringBuilder();

		for (InstalledMod mod : mods) {

			// If mod is contained in ignore list, skip it
			if (ignoredModIds.contains(mod.getId())) {
				ignoredModsCount++;
				continue;
			}
			// Otherwise, loop over each repository...
			for (ManifestRepository repo : repos) {
				if (repo.getLookupTable() == null) {continue;}

				// ...and each lookup table entry within...
				lookupTableEntryLoop:
				for (LookupTableEntry entry : repo.getLookupTable().getLookupTableEntries()) {

					// ...to check if the mod IDs match.
					if (entry.getId().equalsIgnoreCase(mod.getId())) {
						// If they match, get all packages defined in the lookup table...
						for (ModPackage modPackage : entry.getPackages()) {
							boolean alreadyExists = false;

							// ... and check if there already exists a fitting package.
							for (int i = 0; i < packages.size(); i++) {
								if (packages.get(i).getPublisher().equalsIgnoreCase(modPackage.getPublisher()) &&
									packages.get(i).getModId().equalsIgnoreCase(modPackage.getModId()))
								{
									ModPackage newPackage = packages.get(i);
									try {
										newPackage.addManifest(ManifestUtils.create().downloadManifest(entry, packages.get(i)));
									} catch (Exception e) {
										throw e;
									}
									packages.set(i, newPackage);

									alreadyExists = true;
									break;
								}
							}
							// Otherwise, create a new package
							if (alreadyExists == false) {
								ModPackage newPackage = new ModPackageImpl(modPackage.getPackageId());
								try {
									newPackage.addManifest(ManifestUtils.create().downloadManifest(entry, newPackage));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								packages.add(newPackage);
							}
						}
						// Skip ahead to the next mod
						break lookupTableEntryLoop;
					}
				}
			}
			// Add to list with all the recognized mods
			for (int i = 0; i < packages.size(); i++) {
				boolean alreadyExists = false;

				// Check if there already exists a fitting InstalledMod
				for (int j = 0; j < installedMods.size(); j++) {
					if (installedMods.get(j).getId().equalsIgnoreCase(packages.get(i).getModId())) {
						InstalledMod installedMod = installedMods.get(j);
						installedMods.set(j, installedMod);

						alreadyExists = true;
						break;
					}
				}
				// Otherwise, create a new InstalledMod
				if (alreadyExists == false) {
					int index = i;
					installedMods.add(new InstalledModImpl(mod.getId()) {{
						setInstalledVersion(mod.getInstalledVersion());
						addAvailablePackage(packages.get(index));
					}});
					
					// Append mod ID to log message
					if (i > 0) {
						logMessage.append("; ");
					}
					String modId = WordUtils.capitalize(mod.getId());
					if (!logMessage.toString().contains(modId)) {
						logMessage.append(modId);
					}
				}
			}
		}
		// Log which mods have been recognized
		if (logMessage.length() != 0) {logMessage.insert(0, ": ");}
		ModgetLib.logInfo(String.format("Recognized %s out of %s mods%s", installedMods.size(), mods.size() - ignoredModsCount, logMessage.toString()));

		return installedMods;
	}



	public List<InstalledMod> searchForMods(List<ManifestRepository> repos, String term, int charsNeededForExtensiveSearch, String gameVersion) throws Exception {
		float multiplier = 1;
		List<InstalledMod> modsFound;
		List<InstalledMod> modsFoundPriority0;
		List<InstalledMod> modsFoundPriority1 = null;
		List<InstalledMod> modsFoundPriority2 = null;

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

				for (ModPackage modPackage : entry.getPackages()) {
					if (modPackage.getPackageId().equalsIgnoreCase(term)) {
						recognized = true;
					}
				}
				if (recognized == false && entry.getId().equalsIgnoreCase(term)) {
					recognized = true;
				}
				if (recognized == false) {
					for (String name : entry.getAlternativeNames()) {
						if (name.equalsIgnoreCase(term)) {
							recognized = true;
						}
					}
				}
				if (recognized == false && term.length() >= charsNeededForExtensiveSearch) {
					for (ModPackage modPackage : entry.getPackages()) {
						if (modPackage.getPackageId().toLowerCase().contains(term.toLowerCase())) {
							recognized = true;
							priority = 1;
						}
					}
				}
				if (recognized == false && term.length() >= charsNeededForExtensiveSearch) {
					for (String name : entry.getAlternativeNames()) {
						if (name.toLowerCase().contains(term.toLowerCase())) {
							recognized = true;
							priority = 2;
						}
					}
				}

				if (recognized == true) {
					InstalledMod mod = new InstalledModImpl(entry.getId()) {{
						for (ModPackage modPackage : entry.getPackages()) {
							ModManifest manifest;
							try {
								manifest = ManifestUtils.create().downloadManifest(entry, modPackage);
							} catch (Exception e) {
								throw e;
							}
							modPackage.addManifest(manifest);
							addAvailablePackage(modPackage);
							try {
								modPackage.setVersion(ModVersionUtils.create().getLatestCompatibleVersion(manifest.getVersions(), gameVersion, false));
							} catch (NoCompatibleVersionException e) {
								ModgetLib.logInfo(String.format("ModPackage Repo%s.%s has been found, but it's incompatible with the current game version",
									repo.getId(), modPackage.getPackageId()));
							}
						}
					}};

					switch (priority) {
						case 0:
							modsFoundPriority0.add(mod);
							break;
						case 1:
							modsFoundPriority1.add(mod);
							break;
						case 2:
							modsFoundPriority2.add(mod);
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