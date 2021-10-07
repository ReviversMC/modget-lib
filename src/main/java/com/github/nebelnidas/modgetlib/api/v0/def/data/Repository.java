package com.github.nebelnidas.modgetlib.api.v0.def.data;

import java.net.UnknownHostException;

import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTable;

public interface Repository {

	public void refresh() throws UnknownHostException, Exception;

	public LookupTable downloadLookupTable() throws Exception;

	public boolean checkForNewVersion();


	public int getId();
	public String getUri();

	public String getUriWithSpec();

	public LookupTable getLookupTable();
	public void setLookupTable(LookupTable lookupTable);

	public boolean isEnabled();
	public void setEnabled(boolean enabled);

	public boolean isOutdated();
}
