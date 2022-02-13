package com.github.reviversmc.modget.library.data;

import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter @Setter
public class ModUpdate {
	private final InstalledMod installedMod;
	private List<ModVersionVariant> latestModVersionVariants;


	public ModUpdate(
			@NonNull InstalledMod installedMod,
			@NonNull List<ModVersionVariant> latestModVersionVariants) {
		this.installedMod = installedMod;
		this.latestModVersionVariants = latestModVersionVariants;
	}

}
