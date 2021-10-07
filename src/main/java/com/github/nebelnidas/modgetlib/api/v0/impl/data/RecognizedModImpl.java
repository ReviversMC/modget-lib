package com.github.nebelnidas.modgetlib.api.v0.impl.data;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.Package;
import com.github.nebelnidas.modgetlib.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTableEntry;

public class RecognizedModImpl implements RecognizedMod {
	private String id;
	private String currentVersion;
	private List<LookupTableEntry> lookupTableEntries = new ArrayList<LookupTableEntry>();
	private List<Package> availablePackages = new ArrayList<Package>();
	private boolean updateAvailable = false;


	public RecognizedModImpl() {
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
	public String getCurrentVersion() {
		return this.currentVersion;
	}

	@Override
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	@Override
	public List<LookupTableEntry> getLookupTableEntries() {
		return this.lookupTableEntries;
	}

	@Override
	public void addLookupTableEntry(LookupTableEntry lookupTableEntry) {
		this.lookupTableEntries.add(lookupTableEntry);
	}

	@Override
	public List<Package> getAvailablePackages() {
		return this.availablePackages;
	}

	@Override
	public void addAvailablePackage(Package availablePackage) {
		this.availablePackages.add(availablePackage);
	}

	@Override
	public boolean isUpdateAvailable() {
		return this.updateAvailable;
	}

	@Override
	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

}
