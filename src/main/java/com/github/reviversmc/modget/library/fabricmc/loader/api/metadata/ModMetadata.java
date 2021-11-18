/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.reviversmc.modget.library.fabricmc.loader.api.metadata;

import java.util.Collection;
import java.util.Optional;

import com.github.reviversmc.modget.library.fabricmc.loader.api.Version;

/**
 * The metadata of a mod.
 */
public interface ModMetadata {
	/**
	 * Returns the type of the mod.
	 *
	 * <p>The types may be {@code fabric} or {@code builtin} by default.</p>
	 *
	 * @return the type of the mod
	 */
	String getType();

	// When adding getters, follow the order as presented on the wiki.
	// No defaults.

	/**
	 * Returns the mod's ID.
	 *
	 * <p>A mod's id must have only lowercase letters, digits, {@code -}, or {@code _}.</p>
	 *
	 * @return the mod's ID.
	 */
	String getId();

	/**
	 * Returns the mod's ID provides.
	 *
	 * <p>The aliases follow the same rules as ID</p>
	 *
	 * @return the mod's ID provides
	 */
	Collection<String> getProvides();

	/**
	 * Returns the mod's version.
	 */
	Version getVersion();

	/**
	 * Returns the mod's environment.
	 */
	ModEnvironment getEnvironment();

	/**
	 * Returns the mod's display name.
	 */
	String getName();

	/**
	 * Returns the mod's description.
	 */
	String getDescription();

	/**
	 * Returns the mod's licenses.
	 */
	Collection<String> getLicense();

	/**
	 * Gets the path to an icon.
	 *
	 * <p>The standard defines icons as square .PNG files, however their
	 * dimensions are not defined - in particular, they are not
	 * guaranteed to be a power of two.</p>
	 *
	 * <p>The preferred size is used in the following manner:
	 * <ul><li>the smallest image larger than or equal to the size
	 * is returned, if one is present;</li>
	 * <li>failing that, the largest image is returned.</li></ul></p>
	 *
	 * @param size the preferred size
	 * @return the icon path, if any
	 */
	Optional<String> getIconPath(int size);

	/**
	 * Returns if the mod's {@code fabric.mod.json} declares a custom value under {@code key}.
	 *
	 * @param key the key
	 * @return whether a custom value is present
	 */
	boolean containsCustomValue(String key);

	/**
	 * @deprecated Use {@link #containsCustomValue} instead, this will be removed (can't expose GSON types)!
	 */
	@Deprecated
	boolean containsCustomElement(String key);
}