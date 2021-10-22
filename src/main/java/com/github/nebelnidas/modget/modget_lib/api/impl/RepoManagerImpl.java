package com.github.nebelnidas.modget.modget_lib.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.Repository;
import com.github.nebelnidas.modget.manifest_api.api.v0.impl.data.RepositoryImpl;
import com.github.nebelnidas.modget.modget_lib.ModgetLib;
import com.github.nebelnidas.modget.modget_lib.api.def.RepoManager;

public class RepoManagerImpl implements RepoManager {
	protected List<Repository> repos = new ArrayList<>(2);
	protected int lastId = -1;


	@Override
	public void init(List<String> repoUris) throws Exception {
		reload(repoUris);
	}

	@Override
	public void reload(List<String> repoUris) throws Exception {
		repos.clear();
		lastId = -1;
		for (String uri : repoUris) {
			addRepo(uri);
		}
	}

	@Override
	public void refresh() throws Exception {
		List<Repository> newRepos = new ArrayList<>();
		for (Repository repo : repos) {
			try {
				repo.refresh();
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while trying to refresh repository %s", repo.getId()));
				throw e;
			}
			newRepos.add(repo);
		}
		repos = newRepos;
	}

	@Override
	public List<Repository> getRepos() {
		return this.repos;
	}

	@Override
	public void addRepo(String url) {
		repos.add(new RepositoryImpl(lastId + 1, url));
		ModgetLib.logInfo(String.format("Repository added: ID='%s'; URI='%s'", lastId + 1, url));
		lastId++;
	}

	@Override
	public void initRepos() throws Exception {
		for (int i = 0; i < repos.size(); i++) {
			Repository initializedRepo = repos.get(i);
			try {
				initializedRepo.init();
			} catch (Exception e) {
				throw e;
			}
			repos.set(i, initializedRepo);
		}
	}

	@Override
	public void removeRepo(int id) {
		this.repos.remove(id);
	}

	@Override
	public void disableRepo(int id) {
		repos.get(id).setEnabled(false);
	}

	@Override
	public void enableRepo(int id) {
		repos.get(id).setEnabled(true);
	}

}
