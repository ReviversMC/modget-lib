package com.github.nebelnidas.modgetlib.api.v0.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modgetlib.ModgetLib;
import com.github.nebelnidas.modgetlib.api.v0.def.ManifestManager;
import com.github.nebelnidas.modgetlib.api.v0.def.RepoManager;
import com.github.nebelnidas.modgetlib.api.v0.def.data.Package;
import com.github.nebelnidas.modgetlib.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.api.v0.def.data.Repository;
import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTable;
import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTableEntry;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ModVersion;
import com.github.nebelnidas.modgetlib.api.v0.impl.data.RecognizedModImpl;
import com.github.nebelnidas.modgetlib.config.ModgetConfig;
import com.github.nebelnidas.modgetlib.fabricmc.loader.api.SemanticVersion;
import com.github.nebelnidas.modgetlib.fabricmc.loader.api.VersionParsingException;

import org.apache.commons.text.WordUtils;

public class ModgetLibManagerImpl {
	public final RepoManager REPO_MANAGER = new RepoManagerImpl();
	public final ManifestManager MANIFEST_MANAGER = new ManifestManagerImpl();

	private List<RecognizedMod> installedMods = new ArrayList<RecognizedMod>();
	private List<RecognizedMod> recognizedMods = new ArrayList<RecognizedMod>();
	private String minecraftVersion;
	private int ignoredModsCount = 0;


	public void init(String minecraftVersion, List<String> repoUris, List<RecognizedMod> installedMods) throws UnknownHostException, Exception {
		this.minecraftVersion = minecraftVersion;
		REPO_MANAGER.init(repoUris);
		reload(installedMods);
	}

	public void reload(List<RecognizedMod> installedMods) throws UnknownHostException, Exception {
		this.installedMods = installedMods;

		for (Repository repo : REPO_MANAGER.getRepos()) {
			repo.refresh();
		}
		recognizedMods = scanMods(installedMods);
		recognizedMods = MANIFEST_MANAGER.downloadManifests(recognizedMods);
		recognizedMods = findLatestVersions(recognizedMods);
		recognizedMods = findUpdates(recognizedMods);
	}

	public List<RecognizedMod> scanMods(List<RecognizedMod> mods) {
		List<Repository> repos = REPO_MANAGER.getRepos();

		for (RecognizedMod mod : installedMods) {
			// If mod is contained in built-in ignored list, skip it
			if (ModgetConfig.IGNORED_MODS.contains(mod.getId())) {
				ignoredModsCount++;
				continue;
			}
			// Otherwise, loop over each repository...
			for (int i = 0; i < repos.size(); i++) {
				if (repos.get(i).getLookupTable() == null) {continue;}

				// ...and each lookup table entry within...
				lookupTableEntryLoop:
				for (LookupTableEntry lookupTableEntry : repos.get(i).getLookupTable().getLookupTableEntries()) {
					// ...to check if the mod IDs match.
					if (lookupTableEntry.getId().equalsIgnoreCase(mod.getId())) {
						// If they match, check if it has already been found before (in a different repo)
						for (RecognizedMod recognizedMod : mods) {
							if (recognizedMod.getId().equals(mod.getId())) {
								// If so, just add the data to the existing recognized mod
								recognizedMod.addLookupTableEntry(lookupTableEntry);
								// ...and skip ahead to the next mod.
								break lookupTableEntryLoop;
							}
						}
						// Otherwise, create a new one
						mods.add(new RecognizedModImpl() {{
							setId(mod.getId());
							setCurrentVersion(mod.getCurrentVersion());
							addLookupTableEntry(lookupTableEntry);
						}});
						// ...and skip ahead to the next mod.
						break lookupTableEntryLoop;
					}
				}
			}
		}
		// Log which mods have been recognized
		int modCount = 0;
		StringBuilder message = new StringBuilder();
		for (RecognizedMod mod : mods) {
			modCount++;
			if (modCount > 1) {
				message.append("; ");
			}
			String modId = WordUtils.capitalize(mod.getId());
			if (!message.toString().contains(modId)) {
				message.append(modId);
			}
		}
		if (message.length() != 0) {message.insert(0, ": ");}
		ModgetLib.logInfo(String.format("Recognized %s out of %s mods%s", modCount, installedMods.size() - ignoredModsCount, message.toString()));

		return mods;
	}


	public ModVersion findModVersionMatchingCurrentMinecraftVersion(Package p) {
		for (ModVersion version : p.getManifestModVersions()) {
			if (version.getMinecraftVersions().contains(minecraftVersion)) {
				return(version);
			}
		}
		return null;
	}

	public List<RecognizedMod> findLatestVersions(List<RecognizedMod> mods) {
		RecognizedMod mod;
		ModVersion latestManifestModVersion;

		for (int i = 0; i < mods.size(); i++) {
			mod = mods.get(i);
			mod.setUpdateAvailable(false);

			if (mod.getAvailablePackages().size() > 1) {
				ModgetLib.logInfo(String.format("There are multiple packages available for %s", WordUtils.capitalize(mod.getId())));
			}
			for (int j = 0; j < mod.getAvailablePackages().size(); j++) {
				Package p = mod.getAvailablePackages().get(j);

				latestManifestModVersion = findModVersionMatchingCurrentMinecraftVersion(p);
				if (latestManifestModVersion == null) {continue;}
				p.setLatestCompatibleModVersion(latestManifestModVersion);
			}
		}

		return mods;
	}

	public List<RecognizedMod> findUpdates(List<RecognizedMod> mods) {
		RecognizedMod mod;
		ModVersion latestManifestModVersion;

		for (int i = 0; i < mods.size(); i++) {
			mod = mods.get(i);
			mod.setUpdateAvailable(false);

			if (mod.getAvailablePackages().size() > 1) {
				ModgetLib.logInfo(String.format("There are multiple packages available for %s", WordUtils.capitalize(mod.getId())));
			}
			for (int j = 0; j < mod.getAvailablePackages().size(); j++) {
				Package p = mod.getAvailablePackages().get(j);
				latestManifestModVersion = p.getLatestCompatibleModVersion();

				// Try parsing the semantic manifest and mod versions
				SemanticVersion currentVersion;
				try {
					currentVersion = SemanticVersion.parse(mod.getCurrentVersion());
				} catch (VersionParsingException e) {
					ModgetLib.logWarn(String.format("%s doesn't respect semantic versioning, an update check is therefore not possible! %s", p.getName(), e.getMessage()));
					break;
				}

				SemanticVersion latestVersion;
				try {
					latestVersion = SemanticVersion.parse(latestManifestModVersion.getVersion());
				} catch (VersionParsingException e) {
					ModgetLib.logWarn(String.format("The %s manifest doesn't respect semantic versioning, an update check is therefore not possible!", p.getName()), e.getMessage());
					continue;
				}

				// Check for updates
				String packageId = String.format("Repo%s.%s.%s",
					p.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					p.getPublisher(), p.getParentLookupTableEntry().getId());

				if (latestManifestModVersion != null && latestVersion.compareTo(currentVersion) > 0) {
					ModgetLib.logInfo(String.format("Found an update for %s: %s %s", p.getName(),
						packageId, latestVersion.toString()));
					mod.setUpdateAvailable(true);
				} else {
					ModgetLib.logInfo(String.format("No update has been found at %s", packageId));
				}
			}
		}

		return mods;
	}

	public List<RecognizedMod> getRecognizedMods() {
		return this.recognizedMods;
	}

	public List<RecognizedMod> getModsWithUpdates() {
		List<RecognizedMod> modsWithUpdates = new ArrayList<RecognizedMod>();
		for (RecognizedMod mod : recognizedMods) {
			if (mod.isUpdateAvailable() == true) {
				modsWithUpdates.add(mod);
			}
		}
		return modsWithUpdates;
	}

	public List<RecognizedMod> searchForMods(String term, int charsNeededForExtensiveSearch) {
		List<RecognizedMod> modsFound = new ArrayList<RecognizedMod>();
		List<RecognizedMod> modsFoundPriority0 = new ArrayList<RecognizedMod>();
		List<RecognizedMod> modsFoundPriority1 = new ArrayList<RecognizedMod>();
		List<RecognizedMod> modsFoundPriority2 = new ArrayList<RecognizedMod>();

		for (Repository repo : REPO_MANAGER.getRepos()) {
			LookupTable lookupTable = repo.getLookupTable();

			for (LookupTableEntry entry : lookupTable.getLookupTableEntries()) {
				boolean recognized = false;
				int priority = 0;

				if (entry.getId().equalsIgnoreCase(term)) {
					recognized = true;
				}
				if (recognized == false) {
					for (String pack : entry.getPackages()) {
						if (pack.equalsIgnoreCase(term)) {
							recognized = true;
						}
					}
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
					RecognizedMod mod = new RecognizedModImpl() {{
						setId(entry.getId());
						addLookupTableEntry(entry);
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
		modsFound.addAll(modsFoundPriority1);
		modsFound.addAll(modsFoundPriority2);

		modsFound = MANIFEST_MANAGER.downloadManifests(modsFound);
		modsFound = this.findLatestVersions(modsFound);
		return modsFound;
	}
}