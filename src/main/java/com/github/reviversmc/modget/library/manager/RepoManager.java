package com.github.reviversmc.modget.library.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoSuchRepoException;
import com.github.reviversmc.modget.library.exception.RepoAlreadyExistsException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.impl.data.BasicManifestRepository;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class RepoManager {
	protected List<ManifestRepository> repos = new ArrayList<>(2);
	protected int lastId = -1;


	/**
	 * Initializes the RepoManager and the its ManifestRepositories
	 */
	public void init(List<String> repoUris) throws Exception {
		reload(repoUris);
		initRepos();
	}

	/**
	 * Reloads the RepoManager with a new set of ManifestRepositories
	 */
	public void reload(List<String> repoUris) throws Exception {
		repos.clear();
		lastId = -1;
		for (String uri : repoUris) {
			addRepo(uri);
		}
	}

	/**
	 * Refreshes all managed ManifestRepositories
	 */
	public void refresh() throws Exception {
		for (ManifestRepository repo : repos) {
			try {
				repo.refresh();
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while trying to refresh Repo%s", repo.getId()), ExceptionUtils.getStackTrace(e));
			}
		}
	}

	/**
	 * Get all managed ManifestRepositories
	 */
	public List<ManifestRepository> getRepos() {
		return this.repos;
	}

	/**
	 * Adds a new ManifestRepository via its URL
	 */
	public void addRepo(String url) throws RepoAlreadyExistsException {
		for (ManifestRepository repo : repos) {
			if (repo.getUri().equals(url)) {
				throw new RepoAlreadyExistsException(repo.getId());
			}
		}
		repos.add(new BasicManifestRepository(lastId + 1, url));
		ModgetLib.logInfo(String.format("Manifest repository added: ID=%s; URI='%s'", lastId + 1, url));
		lastId++;
	}

	/**
	 * Initializes the managed ManifestRepositories
	 */
	private void initRepos() throws Exception {
		for (ManifestRepository repo : repos) {
			try {
				repo.init();
			} catch (Exception e) {
				ModgetLib.logWarn(String.format("An error occurred while trying to initialize Repo%s", repo.getId()), ExceptionUtils.getStackTrace(e));
			}
		}
	}

	/**
	 * Returns the ManifestRepository with the specified ID.
	 * If no such ManifestRepository exists, throw an exception.
	 */
	public ManifestRepository getRepo(int id) throws NoSuchRepoException {
		for (ManifestRepository repo : repos) {
			if (repo.getId() == id) {
                return repo;
            }
		}
		throw new NoSuchRepoException();
	}

	/**
	 * Removes the ManifestRepository with the specified ID from the managed repos.
	 * If no such ManifestRepository exists, throw an exception.
	 */
	public void removeRepo(int id) throws NoSuchRepoException {
		repos.remove(getRepo(id));
	}

}
