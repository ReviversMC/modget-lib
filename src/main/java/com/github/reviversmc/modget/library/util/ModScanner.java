package com.github.reviversmc.modget.library.util;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ModScanner {

	public static ModScanner create() {
		return new ModScanner();
	}


	public <T extends InstalledMod> List<T> scanMods(
			@NonNull List<T> installedMods,
			@NonNull List<String> ignoredModIds,
			@NonNull List<ManifestRepository> repos
	) throws Exception {
		List<T> recognizedMods = new ArrayList<>();
		StringBuilder logMessage = new StringBuilder();
		int ignoredModsCount = 0;

		for (T installedMod : installedMods) {
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