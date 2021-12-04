package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;

public class ModScanner {

	public static ModScanner create() {
		return new ModScanner();
	}

	
	public List<InstalledMod> scanMods(List<InstalledMod> installedMods, List<String> ignoredModIds, List<ManifestRepository> repos) throws Exception {
		List<InstalledMod> recognizedMods = new ArrayList<>();
		StringBuilder logMessage = new StringBuilder();
		int ignoredModsCount = 0;


		for (InstalledMod installedMod : installedMods) {
			// If modId is contained in ignore list, skip it
			if (ignoredModIds.contains(installedMod.getId())) {
				ignoredModsCount++;
				continue;
			}

			// If there are packages available, add it to the recognized mods
			if (installedMod.getOrDownloadAvailablePackages(repos).size() > 0) {
				recognizedMods.add(installedMod);
				logMessage.append("; " + installedMod.getId());
			}
		}

		// Log which mods have been recognized
		if (logMessage.length() != 0) {
			logMessage.replace(0, 2, "");
			logMessage.insert(0, ": ");
		}
		ModgetLib.logInfo(String.format("Recognized %s out of %s mod IDs%s", recognizedMods.size(), installedMods.size() - ignoredModsCount, logMessage.toString()));

		return recognizedMods;
	}

}