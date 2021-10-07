package com.github.nebelnidas.modgetlib.api.v0.def;

import java.util.List;

import com.github.nebelnidas.modgetlib.api.v0.def.data.Repository;

public interface RepoManager {

	public void init(List<String> repoUris);

	public List<Repository> getRepos();

	public void addRepo(String url);

	public void removeRepo(int id);

	public void disableRepo(int id);

	public void enableRepo(int id);

}
