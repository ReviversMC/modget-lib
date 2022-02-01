package com.github.reviversmc.modget.library.interfaces;

import java.io.IOException;
import java.net.MalformedURLException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;

public interface ManifestRepositoryCache {
	public String getCachePath();
	public void setCachePath(String cachePath);

	public ManifestRepository getRepository() throws StreamReadException, DatabindException, IOException;
	public ManifestRepository getOrDownloadRepository(int id, String url) throws MalformedURLException, IOException;
	public void loadRepositoryFromDisk() throws StreamReadException, DatabindException, IOException;
	public String getFileContent(String relativePath) throws IOException;
	public String getOrDownloadFileContent(String relativePath) throws IOException;
}
