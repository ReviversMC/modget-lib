package com.github.nebelnidas.modgetlib.api.v0.def.data;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTableEntry;

public interface RecognizedMod {

	public String getId();
	public void setId(String id);

	public String getCurrentVersion();
	public void setCurrentVersion(String currentVersion);

	public List<LookupTableEntry> getLookupTableEntries();
	public void addLookupTableEntry(LookupTableEntry lookupTableEntry);

	public List<Package> getAvailablePackages();
	public void addAvailablePackage(Package availablePackage);

	public boolean isUpdateAvailable();
	public void setUpdateAvailable(boolean updateAvailable);

}
