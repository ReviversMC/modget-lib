package com.github.nebelnidas.modgetlib.api.v0.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modgetlib.ModgetLib;
import com.github.nebelnidas.modgetlib.api.v0.def.RepoManager;
import com.github.nebelnidas.modgetlib.api.v0.def.data.Repository;
import com.github.nebelnidas.modgetlib.api.v0.impl.data.RepositoryImpl;

public class RepoManagerImpl implements RepoManager {
	protected List<Repository> repos = new ArrayList<Repository>();
	protected int lastId = -1;

	public void init(List<String> repoUris) {
		for (String uri : repoUris) {
			addRepo(uri);
		}
	}

	public List<Repository> getRepos() {
		return this.repos;
	}

	public void addRepo(String url) {
		repos.add(new RepositoryImpl(lastId + 1, url));
		ModgetLib.logInfo(String.format("Repository added: ID=%s; URI=%s", lastId + 1, url));
		lastId++;
	}

	public void removeRepo(int id) {
		this.repos.remove(id);
	}

	public void disableRepo(int id) {
		repos.get(id).setEnabled(false);
	}

	public void enableRepo(int id) {
		repos.get(id).setEnabled(true);
	}

}
