package com.github.nebelnidas.modget.modget_lib.api.def;

import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.Repository;

public interface RepoManager {

	public void init(List<String> repoUris) throws Exception;

	public void reload(List<String> repoUris) throws Exception;

	public void refresh() throws Exception;

	public List<Repository> getRepos();

	public void addRepo(String url);

	public void initRepos() throws Exception;

	public void removeRepo(int id);

	public void disableRepo(int id);

	public void enableRepo(int id);

}
