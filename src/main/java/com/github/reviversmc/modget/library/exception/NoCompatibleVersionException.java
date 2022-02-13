package com.github.reviversmc.modget.library.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class NoCompatibleVersionException extends Exception {
	public NoCompatibleVersionException() {}
}
