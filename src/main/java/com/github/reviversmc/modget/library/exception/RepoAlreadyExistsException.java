package com.github.reviversmc.modget.library.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RepoAlreadyExistsException extends Exception {
	private int idOfAlreadyExistingRepo;

	public RepoAlreadyExistsException(int idOfAlreadyExistingRepo) {
		this.idOfAlreadyExistingRepo = idOfAlreadyExistingRepo;
	}

	public int getIdOfAlreadyExistingRepo() {
		return idOfAlreadyExistingRepo;
	}
}
