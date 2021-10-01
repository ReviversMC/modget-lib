package com.github.nebelnidas.modgetlib.api;

import java.util.ArrayList;

import com.github.nebelnidas.modgetlib.ModgetLib;
import com.github.nebelnidas.modgetlib.data.LookupTableEntry;
import com.github.nebelnidas.modgetlib.data.Manifest;
import com.github.nebelnidas.modgetlib.data.Package;
import com.github.nebelnidas.modgetlib.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.data.Repository;

public abstract class ManifestManagerBase {

	public String assembleManifestUri(Repository repo, String publisher, String modId) {
		try {
			return new String(String.format("%s/manifests/%s/%s/%s/%s.%s.yaml", repo.getUri(), (""+publisher.charAt(0)).toUpperCase(), publisher, modId, publisher, modId));
		} catch (Exception e) {
			ModgetLib.logWarn(String.format("An error occurred while assembling the Repo%s.%s.%s manifest uri", repo.getId(), publisher, modId), e.getMessage());
			return null;
		}
	}

	public abstract Manifest downloadManifest(Repository repo, String modId, String packageIdParts);

	public ArrayList<RecognizedMod> downloadManifests(ArrayList<RecognizedMod> recognizedMods) {

		for (int i = 0; i < recognizedMods.size(); i++) {
			RecognizedMod mod = recognizedMods.get(i);

			for (LookupTableEntry entry : mod.getLookupTableEntries()) {
				Repository repo = entry.getParentLookupTable().getParentRepository();

				for (int j = 0; j < entry.getPackages().size(); j++) {
					String[] packageIdParts = entry.getPackages().get(j).toString().split("\\.");

					try {
						Manifest manifest = downloadManifest(repo, packageIdParts[0], packageIdParts[1]);
						if (manifest == null) {continue;}

						Package p = new Package(entry);
							p.setPublisher(manifest.getPublisher());
							p.setName(manifest.getName());
							p.setLicense(manifest.getLicense());
							p.setDescription(manifest.getDescription());
							p.setHome(manifest.getHome());
							p.setSource(manifest.getSource());
							p.setIssues(manifest.getIssues());
							p.setSupport(manifest.getSupport());
							p.setModType(manifest.getModType());
							p.setSide(manifest.getSide());
							p.setManifestModVersions(manifest.getDownloads());
						recognizedMods.get(i).addAvailablePackage(p);

					} catch (Exception e) {
						ModgetLib.logWarn(String.format("An error occurred while parsing the Repo%s.%s.%s manifest", repo.getId(), packageIdParts[0], packageIdParts[1]), e.getMessage());
					}
				}
			}
		}
		return recognizedMods;
	}

}
