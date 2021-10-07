package com.github.nebelnidas.modgetlib.api.v0.impl.data.manifest;

import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ModVersionDownload;

public class ModVersionDownloadImpl implements ModVersionDownload {
	private String name;
	private String url;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}
}
