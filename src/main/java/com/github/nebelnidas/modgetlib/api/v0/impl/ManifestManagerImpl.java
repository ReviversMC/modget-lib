package com.github.nebelnidas.modgetlib.api.v0.impl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.nebelnidas.modgetlib.ModgetLib;
import com.github.nebelnidas.modgetlib.api.v0.def.ManifestManager;
import com.github.nebelnidas.modgetlib.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.api.v0.def.data.Repository;
import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTableEntry;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.Manifest;
import com.github.nebelnidas.modgetlib.api.v0.impl.data.PackageImpl;

public class ManifestManagerImpl implements ManifestManager {

	@Override
	public String assembleManifestUri(Repository repo, String publisher, String modId) {
		try {
			String uri = new String(String.format("%s/manifests/%s/%s/%s/%s.%s.yaml", repo.getUriWithSpec(), (""+publisher.charAt(0)).toUpperCase(), publisher, modId, publisher, modId));
			return uri;
		} catch (Exception e) {
			ModgetLib.logWarn(String.format("An error occurred while assembling the Repo%s.%s.%s manifest uri", repo.getId(), publisher, modId), e.getMessage());
			return null;
		}
	}

	@Override
	public Manifest downloadManifest(Repository repo, String publisher, String modId) {
		String packageId = String.format("Repo%s.%s.%s", repo.getId(), publisher, modId);
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String uri = assembleManifestUri(repo, publisher, modId);
		Manifest manifest;

		try {
			manifest = mapper.readValue(new URL(uri), Manifest.class);
		} catch (Exception e) {
			if (e instanceof IOException) {
				ModgetLib.logWarn(String.format("An error occurred while fetching the %s manifest. Please check your Internet connection! %s", packageId, e.getMessage()));
			} else {
				ModgetLib.logWarn(String.format("An error occurred while parsing the %s manifest", packageId), e.getMessage());
			}
			return null;
		}
		ModgetLib.logInfo(String.format("Fetched Manifest: %s", packageId));
		return manifest;
	}

	@Override
	public List<RecognizedMod> downloadManifests(List<RecognizedMod> recognizedMods) {

		for (int i = 0; i < recognizedMods.size(); i++) {
			RecognizedMod mod = recognizedMods.get(i);

			for (LookupTableEntry entry : mod.getLookupTableEntries()) {
				Repository repo = entry.getParentLookupTable().getParentRepository();

				for (int j = 0; j < entry.getPackages().size(); j++) {
					String[] packageIdParts = entry.getPackages().get(j).toString().split("\\.");

					try {
						Manifest manifest = downloadManifest(repo, packageIdParts[0], packageIdParts[1]);
						if (manifest == null) {continue;}

						PackageImpl p = new PackageImpl(entry);
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
