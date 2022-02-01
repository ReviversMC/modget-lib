package com.github.reviversmc.modget.library.cache;

import java.util.List;

public class ManifestRepositoryCacheMetadata {
	int id;
	final String url;
	List<String> manifestSpecVersions;


	public ManifestRepositoryCacheMetadata(int id, String url, List<String> manifestSpecVersions) {
		this.id = id;
		this.url = url;
		this.manifestSpecVersions = manifestSpecVersions;
	}


	public List<String> getManifestSpecVersions() {
		return this.manifestSpecVersions;
	}

	public void addManifestSpecVersion(String manifestSpecVersion) {
		this.manifestSpecVersions.add(manifestSpecVersion);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return this.url;
	}

}
