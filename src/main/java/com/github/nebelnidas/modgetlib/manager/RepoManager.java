package com.github.nebelnidas.modgetlib.manager;

import java.util.ArrayList;

import com.github.nebelnidas.modgetlib.api.RepoManagerBase;

public class RepoManager extends RepoManagerBase {

	public void init(ArrayList<String> repoUris, int supportedManifestSpec) {
		for (String uri : repoUris) {
			addRepo(String.format("%s/v%s", uri, supportedManifestSpec));
		}
	}

}
