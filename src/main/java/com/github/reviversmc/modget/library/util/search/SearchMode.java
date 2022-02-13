package com.github.reviversmc.modget.library.util.search;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class SearchMode {
	private final boolean extensiveSearch;
	private final int charsNeededForExtensiveSearch;
	private final boolean packageIdSearch;
	private final boolean modIdSearch;
	private final boolean publisherSearch;
	private final boolean alternativeNameSearch;
	private final boolean tagSearch;

	public SearchMode(
			boolean extensiveSearch,
			int charsNeededForExtensiveSearch,
			boolean packageIdSearch,
			boolean modIdSearch,
			boolean publisherSearch,
			boolean alternativeNameSearch,
			boolean tagSearch
	) {
		this.extensiveSearch = extensiveSearch;
		this.charsNeededForExtensiveSearch = charsNeededForExtensiveSearch;
		this.packageIdSearch = packageIdSearch;
		this.modIdSearch = modIdSearch;
		this.publisherSearch = publisherSearch;
		this.alternativeNameSearch = alternativeNameSearch;
		this.tagSearch = tagSearch;
	}


	public boolean doesExtensiveSearch() {
		return this.extensiveSearch;
	}


	public int getCharsNeededForExtensiveSearch() {
		return this.charsNeededForExtensiveSearch;
	}


	public boolean doesPackageIdSearch() {
		return this.packageIdSearch;
	}


	public boolean doesModIdSearch() {
		return this.modIdSearch;
	}


	public boolean doesPublisherSearch() {
		return this.publisherSearch;
	}


	public boolean doesAlternativeNameSearch() {
		return this.alternativeNameSearch;
	}


	public boolean doesTagSearch() {
		return this.tagSearch;
	}

}
