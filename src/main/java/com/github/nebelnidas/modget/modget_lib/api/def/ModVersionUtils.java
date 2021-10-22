package com.github.nebelnidas.modget.modget_lib.api.def;

import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.manifest.ModVersion;
import com.github.nebelnidas.modget.modget_lib.api.exception.NoCompatibleVersionException;

public interface ModVersionUtils {

	public List<ModVersion> getCompatibleVersions(List<ModVersion> allVersions, String gameVersion);

	public ModVersion getLatestVersion(List<ModVersion> versions);

	public ModVersion getLatestCompatibleVersion(List<ModVersion> allVersions, String gameVersion) throws NoCompatibleVersionException;

	public List<RecognizedMod> getModsWithUpdates(List<RecognizedMod> mods, String gameVersion);

}
