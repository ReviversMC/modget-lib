package com.github.reviversmc.modget.library.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.ModgetLib;
import com.github.reviversmc.modget.library.exception.NoSuchRepoException;
import com.github.reviversmc.modget.library.exception.RepoAlreadyExistsException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.impl.data.BasicManifestRepository;

public class RepoManager {
	protected List<ManifestRepository> repos = new ArrayList<>(2);
	protected int lastId = -1;


	/**
	 * Initializes the RepoManager
	 */
	public void init(List<String> repoUris) throws Exception {
		reload(repoUris);
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
		repos.add(new BasicManifestRepository(++lastId, url));
		ModgetLib.logInfo(String.format("Manifest repository added: ID=%s; URI='%s'", lastId, url));
	}

	/**
	 * Adds a new ManifestRepository and changes its ID to align with the other managed repos
	 */
	public void addRepo(ManifestRepository repo) throws RepoAlreadyExistsException {
		for (ManifestRepository existingRepo : repos) {
			if (existingRepo.getUri().equals(repo.getUri())) {
				throw new RepoAlreadyExistsException(existingRepo.getId());
			}
		}
		repo.setId(++lastId);
		repos.add(repo);
		ModgetLib.logInfo(String.format("Manifest repository added: ID=%s; URI='%s'", lastId, lastId));
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
