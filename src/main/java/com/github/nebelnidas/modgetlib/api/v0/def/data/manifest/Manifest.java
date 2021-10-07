package com.github.nebelnidas.modgetlib.api.v0.def.data.manifest;

import java.util.List;

public interface Manifest{

	public String getManifestSpecVersion();
	public void setManifestSpecVersion(String manifestSpecVersion);

	public String getPublisher();
	public void setPublisher(String publisher);

	public String getName();
	public void setName(String name);

	public String getId();
	public void setId(String id);

	public ThirdPartyIds getThirdPartyIds();
	public void setThirdPartyIds(ThirdPartyIds thirdPartyIds);

	public String getLicense();
	public void setLicense(String license);

	public String getDescription();
	public void setDescription(String description);

	public String getHome();
	public void setHome(String home);

	public String getSource();
	public void setSource(String source);

	public String getIssues();
	public void setIssues(String issues);

	public String getSupport();
	public void setSupport(String support);

	public String getModType();
	public void setModType(String modType);

	public String getSide();
	public void setSide(String side);

	public List<ModVersion> getDownloads();
	public void setDownloads(List<ModVersion> downloads);

}
