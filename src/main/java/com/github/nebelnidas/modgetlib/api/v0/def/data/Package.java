package com.github.nebelnidas.modgetlib.api.v0.def.data;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTableEntry;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ModVersion;

public interface Package {

	public LookupTableEntry getParentLookupTableEntry();
	// public void setParentLookupTableEntry(LookupTableEntry parentLookupTableEntry);

	public String getPublisher();
	public void setPublisher(String publisher);

	public String getName();
	public void setName(String name);

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

	public List<ModVersion> getManifestModVersions();
	public void setManifestModVersions(List<ModVersion> modVersions);

	public ModVersion getLatestCompatibleModVersion();
	public void setLatestCompatibleModVersion(ModVersion latestCompatibleModVersion);

}
