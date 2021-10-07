package com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.Repository;

public interface LookupTable {

	public Repository getParentRepository();

	public List<LookupTableEntry> getLookupTableEntries();

}
