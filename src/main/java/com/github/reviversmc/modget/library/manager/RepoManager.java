package com.github.reviversmc.modget.library.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoSuchRepoException;
import com.github.reviversmc.modget.library.exception.RepoAlreadyExistsException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.impl.data.ManifestRepositoryImpl;

public class RepoManager {
	protected List<ManifestRepository> repos = new ArrayList<>(2);
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
		List<ManifestRepository> newRepos = new ArrayList<>();
		for (ManifestRepository repo : repos) {
			try {
				repo.refresh();
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while trying to refresh ManifestRepository %s", repo.getId()));
				throw e;
			}
			newRepos.add(repo);
		}
		repos = newRepos;
	}

	public List<ManifestRepository> getRepos() {
		return this.repos;
	}

	public void addRepo(String url) throws RepoAlreadyExistsException {
		for (ManifestRepository repo : repos) {
			if (repo.getUri().equals(url)) {
				throw new RepoAlreadyExistsException(repo.getId());
			}
		}
		repos.add(new ManifestRepositoryImpl(lastId + 1, url));
		ModgetLib.logInfo(String.format("ManifestRepository added: ID='%s'; URI='%s'", lastId + 1, url));
		lastId++;
	}

	public void initRepos() throws Exception {
		for (int i = 0; i < repos.size(); i++) {
			ManifestRepository initializedRepo = repos.get(i);
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
				ManifestRepository repo = repos.get(i);
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
				ManifestRepository repo = repos.get(i);
				repo.setEnabled(true);
                repos.set(i, repo);
                return;
            }
		}
		throw new NoSuchRepoException();
	}

}
