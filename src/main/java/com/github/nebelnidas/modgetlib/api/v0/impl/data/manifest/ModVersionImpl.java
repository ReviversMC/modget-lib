package com.github.nebelnidas.modgetlib.api.v0.impl.data.manifest;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ModVersion;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ModVersionDownload;

public class ModVersionImpl implements ModVersion {
	private String version;
	private List<String> minecraftVersions;
	private String md5;
	private List<ModVersionDownload> downloadPageUrls;
	private List<ModVersionDownload> fileUrls;

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public List<String> getMinecraftVersions() {
		return this.minecraftVersions;
	}

	@Override
	public void setMinecraftVersions(List<String> minecraftVersions) {
		this.minecraftVersions = minecraftVersions;
	}

	@Override
	public String getMd5() {
		return this.md5;
	}

	@Override
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public List<ModVersionDownload> getDownloadPageUrls() {
		return this.downloadPageUrls;
	}

	@Override
	public void setDownloadPageUrls(List<ModVersionDownload> downloadPageUrls) {
		this.downloadPageUrls = downloadPageUrls;
	}

	@Override
	public List<ModVersionDownload> getFileUrls() {
		return this.fileUrls;
	}

	@Override
	public void setFileUrls(List<ModVersionDownload> fileUrls) {
		this.fileUrls = fileUrls;
	}

}
