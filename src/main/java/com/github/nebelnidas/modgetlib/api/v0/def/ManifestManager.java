package com.github.nebelnidas.modgetlib.api.v0.def;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.api.v0.def.data.Repository;
import com.github.nebelnidas.modgetlib.api.v0.def.data.manifest.Manifest;

public interface ManifestManager {

	public String assembleManifestUri(Repository repo, String publisher, String modId);

	public Manifest downloadManifest(Repository repo, String modId, String packageIdParts);

	public List<RecognizedMod> downloadManifests(List<RecognizedMod> recognizedMods);

}
