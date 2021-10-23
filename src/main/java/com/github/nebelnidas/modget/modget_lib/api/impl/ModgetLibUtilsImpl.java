package com.github.nebelnidas.modget.modget_lib.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.Package;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.Repository;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.lookuptable.LookupTable;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.lookuptable.LookupTableEntry;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.manifest.Manifest;
import com.github.nebelnidas.modget.manifest_api.api.v0.impl.ManifestUtilsImpl;
import com.github.nebelnidas.modget.manifest_api.api.v0.impl.data.PackageImpl;
import com.github.nebelnidas.modget.manifest_api.api.v0.impl.data.RecognizedModImpl;
import com.github.nebelnidas.modget.modget_lib.ModgetLib;
import com.github.nebelnidas.modget.modget_lib.api.def.ModgetLibUtils;
import com.github.nebelnidas.modget.modget_lib.api.exception.NoCompatibleVersionException;

import org.apache.commons.text.WordUtils;

public class ModgetLibUtilsImpl implements ModgetLibUtils {

	public static ModgetLibUtilsImpl create() {
		return new ModgetLibUtilsImpl();
	}

	@Override
	public List<RecognizedMod> scanMods(List<RecognizedMod> mods, List<String> ignoredModIds, List<Repository> repos) throws IOException {
		List<RecognizedMod> recognizedMods = new ArrayList<>();
		List<Package> packages = new ArrayList<>();
		int ignoredModsCount = 0;
		StringBuilder logMessage = new StringBuilder();

		for (RecognizedMod mod : mods) {

			// If mod is contained in ignore list, skip it
			if (ignoredModIds.contains(mod.getId())) {
				ignoredModsCount++;
				continue;
			}
			// Otherwise, loop over each repository...
			for (Repository repo : repos) {
				if (repo.getLookupTable() == null) {continue;}

				// ...and each lookup table entry within...
				lookupTableEntryLoop:
				for (LookupTableEntry entry : repo.getLookupTable().getLookupTableEntries()) {

					// ...to check if the mod IDs match.
					if (entry.getId().equalsIgnoreCase(mod.getId())) {
						// If they match, get all packages defined in the lookup table...
						for (String packageId : entry.getPackages()) {
							String[] packageIdParts = packageId.split("\\.");
							boolean alreadyExists = false;

							// ... and check if there already exists a fitting package.
							for (int i = 0; i < packages.size(); i++) {
								if (packages.get(i).getPublisher().equalsIgnoreCase(packageIdParts[0]) &&
									packages.get(i).getId().equalsIgnoreCase(packageIdParts[1]))
								{
									Package newPackage = packages.get(i);
									try {
										newPackage.addManifest(ManifestUtilsImpl.create().downloadManifest(entry, packages.get(i)));
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
								Package newPackage = new PackageImpl(packageIdParts[0], packageIdParts[1]);
								newPackage.addManifest(ManifestUtilsImpl.create().downloadManifest(entry, newPackage));
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

				// Check if there already exists a fitting RecognizedMod
				for (int j = 0; j < recognizedMods.size(); j++) {
					if (recognizedMods.get(j).getId().equalsIgnoreCase(packages.get(i).getId())) {
						RecognizedMod recognizedMod = recognizedMods.get(j);
						recognizedMods.set(j, recognizedMod);

						alreadyExists = true;
						break;
					}
				}
				// Otherwise, create a new RecognizedMod
				if (alreadyExists == false) {
					int index = i;
					recognizedMods.add(new RecognizedModImpl(mod.getId(), mod.getCurrentVersion()) {{
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
		ModgetLib.logInfo(String.format("Recognized %s out of %s mods%s", recognizedMods.size(), mods.size() - ignoredModsCount, logMessage.toString()));

		return recognizedMods;
	}



	@Override
	public List<RecognizedMod> searchForMods(List<Repository> repos, String term, int charsNeededForExtensiveSearch, String gameVersion) throws IOException {
		float multiplier = 1;
		List<RecognizedMod> modsFound;
		List<RecognizedMod> modsFoundPriority0;
		List<RecognizedMod> modsFoundPriority1 = null;
		List<RecognizedMod> modsFoundPriority2 = null;

		if (term.length() >= charsNeededForExtensiveSearch) {
			multiplier += charsNeededForExtensiveSearch / term.length();
			modsFoundPriority1 = new ArrayList<>(Math.round(4 * multiplier));
			modsFoundPriority2 = new ArrayList<>(Math.round(4 * multiplier));
		}
		modsFound = new ArrayList<>(Math.round(8 * multiplier));
		modsFoundPriority0 = new ArrayList<>(Math.round(4 * multiplier));

		for (Repository repo : repos) {
			LookupTable lookupTable = repo.getLookupTable();

			for (LookupTableEntry entry : lookupTable.getLookupTableEntries()) {
				boolean recognized = false;
				int priority = 0;

				for (String packageId : entry.getPackages()) {
					if (packageId.equalsIgnoreCase(term)) {
						recognized = true;
					}
				}
				if (recognized == false && entry.getId().equalsIgnoreCase(term)) {
					recognized = true;
				}
				if (recognized == false) {
					for (String name : entry.getNames()) {
						if (name.equalsIgnoreCase(term)) {
							recognized = true;
						}
					}
				}
				if (recognized == false && term.length() >= charsNeededForExtensiveSearch) {
					for (String pack : entry.getPackages()) {
						if (pack.toLowerCase().contains(term.toLowerCase())) {
							recognized = true;
							priority = 1;
						}
					}
				}
				if (recognized == false && term.length() >= charsNeededForExtensiveSearch) {
					for (String name : entry.getNames()) {
						if (name.toLowerCase().contains(term.toLowerCase())) {
							recognized = true;
							priority = 2;
						}
					}
				}

				if (recognized == true) {
					RecognizedMod mod = new RecognizedModImpl(entry.getId()) {{
						for (String packageId : entry.getPackages()) {
							String[] packageIdParts = packageId.split("\\.");
							Package pack = new PackageImpl(packageIdParts[0], packageIdParts[1]);

							Manifest manifest = ManifestUtilsImpl.create().downloadManifest(entry, pack);
							pack.addManifest(manifest);
							addAvailablePackage(pack);
							try {
								addUpdate(ModVersionUtilsImpl.create().getLatestCompatibleVersion(manifest.getDownloads(), gameVersion));
							} catch (NoCompatibleVersionException e) {
								ModgetLib.logInfo(String.format("Package Repo%s.%s.%s has been found, but it's incompatible with the current game version",
									repo.getId(), pack.getPublisher(), pack.getId()));
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