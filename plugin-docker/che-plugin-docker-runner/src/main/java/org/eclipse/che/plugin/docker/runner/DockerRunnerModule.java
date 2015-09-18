/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.docker.runner;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import org.eclipse.che.api.runner.internal.Runner;
import org.eclipse.che.plugin.docker.client.CgroupOOMDetector;
import org.eclipse.che.plugin.docker.client.DockerOOMDetector;

/**
 * Docker runner deployer.
 *
 * @author andrew00x
 */
public class DockerRunnerModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), Runner.class).addBinding().to(DockerRunner.class);
        bind(EmbeddedDockerRunnerRegistryPlugin.class).asEagerSingleton();
        bind(ApplicationLinksGenerator.class).to(CustomPortApplicationLinksGenerator.class);
        bind(DockerOOMDetector.class).to(CgroupOOMDetector.class);
    }
}
