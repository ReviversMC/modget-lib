package com.github.reviversmc.modget.library.data;

import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;

public class ModUpdate {
	private final InstalledMod installedMod;
	private List<ModVersionVariant> latestModVersionVariants;


	public ModUpdate(InstalledMod installedMod, List<ModVersionVariant> latestModVersionVariants) {
		this.installedMod = installedMod;
		this.latestModVersionVariants = latestModVersionVariants;
	}


	public InstalledMod getInstalledMod() {
		return this.installedMod;
	}

	public List<ModVersionVariant> getLatestModVersionVariants() {
		return this.latestModVersionVariants;
	}

	public void setLatestModVersionVariants(List<ModVersionVariant> latestModVersionVariants) {
		this.latestModVersionVariants = latestModVersionVariants;
	}

}
