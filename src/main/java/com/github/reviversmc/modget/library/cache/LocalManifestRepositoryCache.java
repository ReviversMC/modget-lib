package com.github.reviversmc.modget.library.cache;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.reviversmc.modget.library.interfaces.ManifestRepositoryCache;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.config.ManifestApiSpec4Config;
import com.github.reviversmc.modget.manifests.spec4.impl.data.BasicManifestRepository;

import org.apache.commons.io.FileUtils;

public class LocalManifestRepositoryCache implements ManifestRepositoryCache {
	private String metadataFileName = "meta.yaml";
	private String cachePath;
	private ManifestRepository cachedRepo;


	public LocalManifestRepositoryCache(String cachePath) {
		setCachePath(cachePath);
	}


	@Override
	public String getCachePath() {
		return cachePath;
	}


	@Override
	public void setCachePath(String cachePath) {
		if (cachePath.endsWith("/")) {
			this.cachePath = cachePath.substring(0, cachePath.length() - 1);
		}
	}


	@Override
	public ManifestRepository getRepository() throws StreamReadException, DatabindException, IOException {
		if (cachedRepo == null) {
			loadRepositoryFromDisk();
		}
		return cachedRepo;
	}

	@Override
	public ManifestRepository getOrDownloadRepository(int id, String url) throws MalformedURLException, IOException {
		try {
			if (getRepository() != null) {
				return cachedRepo;
			}
		} catch (IOException e) {}

		// Repo has to be downloaded
		FileUtils.copyURLToFile(new URL(String.format("%s/v%s/lookup-table.yaml", url, ManifestApiSpec4Config.SUPPORTED_MANIFEST_SPEC)),
				new File(String.format("%s/v%s/lookup-table.yaml", cachePath, ManifestApiSpec4Config.SUPPORTED_MANIFEST_SPEC)));
		return getRepository();
	}


	@Override
	public void loadRepositoryFromDisk() throws StreamReadException, DatabindException, IOException {
		if (!new File(metadataFileName).isFile()) {
			cachedRepo = null;
		} else {
			final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
			mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
			CacheMetadata metadata = mapper.readValue(new File(metadataFileName), CacheMetadata.class);
			cachedRepo = new BasicManifestRepository(metadata.getId(), metadata.getUrl());
		}
	}


	@Override
	public String getFileContent(String relativePath) throws IOException {
		if (new File(metadataFileName).isFile()) {
			File file = new File(cachePath)
			return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} else {
			return null;
		}
	}


	@Override
	public String getOrDownloadFileContent(String relativePath) throws IOException {
		if (getFileContent(file) != null) {
			return getFileContent(file);
		} else {

		}
	}

}
