package com.github.nebelnidas.modgetlib.api.v0.impl.data.manifest;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.Manifest;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ModVersion;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.ThirdPartyIds;

public class ManifestImpl implements Manifest {
	private String manifestSpecVersion;
	private String publisher;
	private String name;
	private String id;
	private ThirdPartyIds thirdPartyIds;
	private String license;
	private String description;
	private String home;
	private String source;
	private String issues;
	private String support;
	private String modType;
	private String side;
	private List<ModVersion> downloads;

	@Override
	public String getManifestSpecVersion() {
		return this.manifestSpecVersion;
	}

	@Override
	public void setManifestSpecVersion(String manifestSpecVersion) {
		this.manifestSpecVersion = manifestSpecVersion;
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

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public ThirdPartyIds getThirdPartyIds() {
		return this.thirdPartyIds;
	}

	@Override
	public void setThirdPartyIds(ThirdPartyIds thirdPartyIds) {
		this.thirdPartyIds = thirdPartyIds;
	}

	@Override
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
	public List<ModVersion> getDownloads() {
		return this.downloads;
	}

	@Override
	public void setDownloads(List<ModVersion> downloads) {
		this.downloads = downloads;
	}

}

