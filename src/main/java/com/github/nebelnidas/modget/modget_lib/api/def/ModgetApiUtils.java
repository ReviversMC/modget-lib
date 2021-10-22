package com.github.nebelnidas.modget.modget_lib.api.def;

import java.io.IOException;
import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.Repository;

public interface ModgetApiUtils {

	public List<RecognizedMod> scanMods(List<RecognizedMod> mods, List<String> ignoredModIds, List<Repository> repos) throws IOException;

	// public List<RecognizedMod> searchForMods(List<Repository> repos, String term, int charsNeededForExtensiveSearch) throws IOException; // TODO
	public List<RecognizedMod> searchForMods(List<Repository> repos, String term, int charsNeededForExtensiveSearch, String gameVersion) throws IOException;
}