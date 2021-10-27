package com.github.nebelnidas.modget.modget_lib.exception;

public class RepoAlreadyExistsException extends Exception {
	private int idOfAlreadyExistingRepo;

	public RepoAlreadyExistsException(int idOfAlreadyExistingRepo) {
		this.idOfAlreadyExistingRepo = idOfAlreadyExistingRepo;
	}

	public int getIdOfAlreadyExistingRepo() {
		return idOfAlreadyExistingRepo;
	}
}
