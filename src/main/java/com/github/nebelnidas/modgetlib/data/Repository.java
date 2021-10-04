package com.github.nebelnidas.modgetlib.data;

import java.net.URL;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.nebelnidas.modgetlib.ModgetLib;
import com.github.nebelnidas.modgetlib.config.ModgetConfig;

public class Repository {
	private final int id;
	private final String uri;
	private LookupTable lookupTable;
	private boolean enabled = true;
	private boolean outdated = false;

	public Repository(int id, String uri) {
		this.id = id;
		if (uri.endsWith("/")) {
			uri = uri.substring(0, uri.length() - 1);
		}
		this.uri = uri;
		try {
			refresh();
		} catch (Exception e) {}
	}


	public void refresh() throws UnknownHostException, Exception {
		lookupTable = downloadLookupTable();
		outdated = checkForNewVersion();
	}

	private LookupTable downloadLookupTable() throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		try {
			LookupTableEntry[] entries = mapper.readValue(new URL(String.format("%s/v%s/%s", uri, ModgetConfig.SUPPORTED_MANIFEST_SPEC, "/lookup-table.yaml")), LookupTableEntry[].class);

			LookupTable newLookupTable = new LookupTable(this, entries);
			for (LookupTableEntry entry : entries) {
				entry.setParentLookupTable(newLookupTable);
			}
			return newLookupTable;
        } catch (Exception e) {
			if (e instanceof UnknownHostException) {
				ModgetLib.logWarn("Couldn't connect to the manifest repository. Please check your Internet connection!");
			} else {
				ModgetLib.logWarn("Couldn't connect to the manifest repository", e.getMessage());
			}
			throw e;
        }
	}

	private boolean checkForNewVersion() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		try {
			mapper.readValue(new URL(uri + (ModgetConfig.SUPPORTED_MANIFEST_SPEC + 1) + "/lookup-table.yaml"), LookupTableEntry[].class);

			return true;
        } catch (Exception e) {
			if (e instanceof UnknownHostException) {
				ModgetLib.logWarn("Couldn't check for new repository versions. Please check your Internet connection!");
			}
        }
		return false;
	}


	public int getId() {
		return this.id;
	}

	public String getUri() {
		return this.uri;
	}

	public LookupTable getLookupTable() {
		return this.lookupTable;
	}

	public void setLookupTable(LookupTable lookupTable) {
		this.lookupTable = lookupTable;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isOutdated() {
		return this.outdated;
	}
}
