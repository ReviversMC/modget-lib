package com.github.nebelnidas.modgetlib.api.v0.impl.data;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.Package;
import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTableEntry;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ModVersion;

public class PackageImpl implements Package {
	private final LookupTableEntry parentLookupTableEntry;
	private String publisher;
	private String name;
	private String license;
	private String description;
	private String home;
	private String source;
	private String issues;
	private String support;
	private String modType;
	private String side;
	private List<ModVersion> modVersions = new ArrayList<ModVersion>();
	private ModVersion latestCompatibleModVersion;


	public PackageImpl(LookupTableEntry parentLookupTableEntry) {
		this.parentLookupTableEntry = parentLookupTableEntry;
	}


	@Override
	public LookupTableEntry getParentLookupTableEntry() {
		return this.parentLookupTableEntry;
	}

	@Override
	public String getPublisher() {
		return this.publisher;
	}

	@Override
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getLicense() {
		return this.license;
	}

	@Override
	public void setLicense(String license) {
		this.license = license;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getHome() {
		return this.home;
	}

	@Override
	public void setHome(String home) {
		this.home = home;
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String getIssues() {
		return this.issues;
	}

	@Override
	public void setIssues(String issues) {
		this.issues = issues;
	}

	@Override
	public String getSupport() {
		return this.support;
	}

	@Override
	public void setSupport(String support) {
		this.support = support;
	}

	@Override
	public String getModType() {
		return this.modType;
	}

	@Override
	public void setModType(String modType) {
		this.modType = modType;
	}

	@Override
	public String getSide() {
		return this.side;
	}

	@Override
	public void setSide(String side) {
		this.side = side;
	}

	@Override
	public List<ModVersion> getManifestModVersions() {
		return this.modVersions;
	}

	@Override
	public void setManifestModVersions(List<ModVersion> modVersions) {
		this.modVersions = modVersions;
	}

	@Override
	public ModVersion getLatestCompatibleModVersion() {
		return this.latestCompatibleModVersion;
	}

	@Override
	public void setLatestCompatibleModVersion(ModVersion latestCompatibleModVersion) {
		this.latestCompatibleModVersion = latestCompatibleModVersion;
	}

}
