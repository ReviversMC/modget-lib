package com.github.nebelnidas.modgetlib.manager;

import java.util.ArrayList;

import com.github.nebelnidas.modgetlib.api.RepoManagerBase;

public class RepoManager extends RepoManagerBase {

	public void init(ArrayList<String> repoUris) {
		for (String uri : repoUris) {
			addRepo(uri);
		}
	}

}
