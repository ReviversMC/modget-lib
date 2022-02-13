package com.github.reviversmc.modget.library.util.search;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class SearchModeBuilder {
	private boolean extensiveSearch = false;
	private int charsNeededForExtensiveSearch = 4;
	private boolean packageIdSearch = true;
	private boolean modIdSearch = false;
	private boolean publisherSearch = false;
	private boolean alternativeNameSearch = false;
	private boolean tagSearch = false;

	public SearchModeBuilder enableExtensiveSearch(boolean extensiveSearch, int charsNeededForExtensiveSearch) {
		this.extensiveSearch = extensiveSearch;
		this.charsNeededForExtensiveSearch = charsNeededForExtensiveSearch;
		return this;
	}

	public SearchModeBuilder enablePackageIdSearch(boolean packageIdSearch) {
		this.packageIdSearch = packageIdSearch;
		return this;
	}

	public SearchModeBuilder enableModIdSearch(boolean modIdSearch) {
		this.modIdSearch = modIdSearch;
		return this;
	}

	public SearchModeBuilder enablePublisherSearch(boolean publisherSearch) {
		this.publisherSearch = publisherSearch;
		return this;
	}

	public SearchModeBuilder enableAlternativeNameSearch(boolean alternativeNameSearch) {
		this.alternativeNameSearch = alternativeNameSearch;
		return this;
	}

	public SearchModeBuilder enableTagSearch(boolean tagSearch) {
		this.tagSearch = tagSearch;
		return this;
	}

	public SearchMode build() {
		return new SearchMode(extensiveSearch, charsNeededForExtensiveSearch, packageIdSearch, modIdSearch,
				publisherSearch, alternativeNameSearch, tagSearch);
	}

}
