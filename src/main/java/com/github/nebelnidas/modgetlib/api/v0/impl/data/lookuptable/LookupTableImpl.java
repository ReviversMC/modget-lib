package com.github.nebelnidas.modgetlib.api.v0.impl.data.lookuptable;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.Repository;
import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTable;
import com.github.nebelnidas.modgetlib.api.v0.def.data.lookuptable.LookupTableEntry;

public class LookupTableImpl implements LookupTable {
	private final Repository parentRepository;
	private final List<LookupTableEntry> lookupTableEntries;

	public LookupTableImpl(Repository parentRepository, List<LookupTableEntry> lookupTableEntries) {
		this.parentRepository = parentRepository;
		this.lookupTableEntries = lookupTableEntries;
	}

	@Override
	public Repository getParentRepository() {
		return this.parentRepository;
	}

	@Override
	public List<LookupTableEntry> getLookupTableEntries() {
		return this.lookupTableEntries;
	}

}
