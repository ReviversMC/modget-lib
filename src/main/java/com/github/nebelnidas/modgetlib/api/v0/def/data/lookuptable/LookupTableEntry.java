package com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable;

import java.util.List;

public interface LookupTableEntry {

	public LookupTable getParentLookupTable();
	public void setParentLookupTable(LookupTable parentLookupTable);

	public String getId();
	public void setId(String id);

	public List<String> getNames();
	public void setNames(List<String> names);

	public List<String> getPackages();
	public void setPackages(List<String> packages);

	public List<String> getTags();
	public void setTags(List<String> tags);

}
