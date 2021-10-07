package com.github.nebelnidas.modgetlib.api.v0.def.data.manifest;

import java.util.List;

public interface ModVersion {

	public String getVersion();
	public void setVersion(String version);

	public List<String> getMinecraftVersions() ;
	public void setMinecraftVersions(List<String> minecraftVersions);

	public String getMd5();
	public void setMd5(String md5);

	public List<ModVersionDownload> getDownloadPageUrls();
	public void setDownloadPageUrls(List<ModVersionDownload> downloadPageUrls);

	public List<ModVersionDownload> getFileUrls();
	public void setFileUrls(List<ModVersionDownload> fileUrls);

}
