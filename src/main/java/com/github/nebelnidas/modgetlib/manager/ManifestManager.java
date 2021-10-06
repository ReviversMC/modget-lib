package com.github.nebelnidas.modgetlib.manager;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.nebelnidas.modgetlib.ModgetLib;
import com.github.nebelnidas.modgetlib.api.ManifestManagerBase;
import com.github.nebelnidas.modgetlib.data.Manifest;
import com.github.nebelnidas.modgetlib.data.Repository;

public class ManifestManager extends ManifestManagerBase {

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

}
