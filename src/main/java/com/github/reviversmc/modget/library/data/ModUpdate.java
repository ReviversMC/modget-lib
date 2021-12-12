package com.github.reviversmc.modget.library.data;

import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;

import org.apache.commons.lang3.tuple.Pair;

public class ModUpdate {
	private final InstalledMod installedMod;
	private List<Pair<ManifestRepository, ModVersionVariant>> latestModVersionVariants;


	public ModUpdate(InstalledMod installedMod, List<Pair<ManifestRepository, ModVersionVariant>> latestModVersionVariants) {
		this.installedMod = installedMod;
		this.latestModVersionVariants = latestModVersionVariants;
	}


	public InstalledMod getInstalledMod() {
		return this.installedMod;
	}

	public List<Pair<ManifestRepository,ModVersionVariant>> getLatestModVersionVariants() {
		return this.latestModVersionVariants;
	}

	public void setLatestModVersionVariants(List<Pair<ManifestRepository,ModVersionVariant>> latestModVersionVariants) {
		this.latestModVersionVariants = latestModVersionVariants;
	}


}
