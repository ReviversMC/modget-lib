package com.github.nebelnidas.modget.modget_lib.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modget.manifest_api.spec3.api.data.Repository;
import com.github.nebelnidas.modget.manifest_api.spec3.impl.data.RepositoryImpl;
import com.github.nebelnidas.modget.modget_lib.ModgetLib;
import com.github.nebelnidas.modget.modget_lib.exception.NoSuchRepoException;
import com.github.nebelnidas.modget.modget_lib.exception.RepoAlreadyExistsException;

public class RepoManager {
	protected List<Repository> repos = new ArrayList<>(2);
	protected int lastId = -1;


	public void init(List<String> repoUris) throws Exception {
		reload(repoUris);
	}

	public void reload(List<String> repoUris) throws Exception {
		repos.clear();
		lastId = -1;
		for (String uri : repoUris) {
			addRepo(uri);
		}
	}

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

	public List<Repository> getRepos() {
		return this.repos;
	}

	public void addRepo(String url) throws RepoAlreadyExistsException {
		for (Repository repo : repos) {
			if (repo.getUri().equals(url)) {
				throw new RepoAlreadyExistsException(repo.getId());
			}
		}
		repos.add(new RepositoryImpl(lastId + 1, url));
		ModgetLib.logInfo(String.format("Repository added: ID='%s'; URI='%s'", lastId + 1, url));
		lastId++;
	}

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

	public void removeRepo(int id) throws NoSuchRepoException {
		for (int i = 0; i < repos.size(); i++) {
			if (repos.get(i).getId() == id) {
                repos.remove(i);
                return;
            }
		}
		throw new NoSuchRepoException();
	}

	public void disableRepo(int id) throws NoSuchRepoException  {
		for (int i = 0; i < repos.size(); i++) {
			if (repos.get(i).getId() == id) {
				Repository repo = repos.get(i);
				repo.setEnabled(false);
                repos.set(i, repo);
                return;
            }
		}
		throw new NoSuchRepoException();
	}

	public void enableRepo(int id) throws NoSuchRepoException  {
		for (int i = 0; i < repos.size(); i++) {
			if (repos.get(i).getId() == id) {
				Repository repo = repos.get(i);
				repo.setEnabled(true);
                repos.set(i, repo);
                return;
            }
		}
		throw new NoSuchRepoException();
	}

}
