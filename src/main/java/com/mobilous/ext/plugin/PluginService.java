package com.mobilous.ext.plugin;


import com.mobilous.ext.plugin.util.HeterogeneousMap;

import net.xeoh.plugins.base.Plugin;

/**
 * Interface for the plugin service containing methods to be implemented.
 *
 * Please do not modify this file.
 *
 * @author yanto
 */
public interface PluginService extends Plugin {

	public HeterogeneousMap getServiceName();
	public HeterogeneousMap authenticate(HeterogeneousMap dataset);
	public HeterogeneousMap getSchema(HeterogeneousMap dataset);
	public HeterogeneousMap create(HeterogeneousMap dataset);
	public HeterogeneousMap read(HeterogeneousMap dataset);
	public HeterogeneousMap update(HeterogeneousMap dataset);
	public HeterogeneousMap delete(HeterogeneousMap dataset);
	public HeterogeneousMap numrecord(HeterogeneousMap dataset);

}
