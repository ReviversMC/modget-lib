package com.github.reviversmc.modget.library.data.lookuptable;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.reviversmc.modget.library.interfaces.Cacheable;
import com.github.reviversmc.modget.library.interfaces.ManifestRepositoryCache;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.lookuptable.LookupTableEntry;
import com.github.reviversmc.modget.manifests.spec4.impl.data.lookuptable.BasicLookupTable;
import com.github.reviversmc.modget.manifests.spec4.impl.downloaders.BasicLookupTableDownloader;

public class CachedLookupTable extends BasicLookupTable implements Cacheable {
	private ManifestRepository parentRepository;
	private List<LookupTableEntry> lookupTableEntries;
	private ManifestRepositoryCache cache;
	private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

	public CachedLookupTable(ManifestRepository parentRepository, ManifestRepositoryCache cache) {
		super(parentRepository);
		this.parentRepository = parentRepository;

		this.lookupTableEntries = new ArrayList<>(40);
	}

	@Override
	public ManifestRepository getParentRepository() {
		return parentRepository;
	}

	@Override
	public void setParentRepository(ManifestRepository parentRepository) {
		this.parentRepository = parentRepository;
	}


	@Override
	public List<LookupTableEntry> getEntries() {
		if (lookupTableEntries.isEmpty()) {
			loadFromCache();
        }
		return lookupTableEntries;
	}

	@Override
	public List<LookupTableEntry> getOrDownloadEntries() throws Exception {
		setEntries(getEntries());
        if (lookupTableEntries.isEmpty()) {
            setEntries(BasicLookupTableDownloader.create().downloadLookupTable(parentRepository).getEntries());
        }
		return lookupTableEntries;
	}

	@Override
	public void setEntries(List<LookupTableEntry> lookupTableEntries) {
        if (lookupTableEntries == null) {
            this.lookupTableEntries.clear();
            return;
        }
		this.lookupTableEntries = lookupTableEntries;
	}

	@Override
	public void loadFromCache() {
		// JsonNode jsonNode =
	}

	@Override
	public void writeToCache() {

	}

}
