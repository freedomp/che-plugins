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
package org.eclipse.che.ide.ext.gwt.client.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.gwt.client.GwtResources;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.ConfigurationFactory;
import org.eclipse.che.ide.extension.machine.client.command.ConfigurationPage;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.machine.MachineManager;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;

/**
 * GWT command type.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class GwtCommandType implements CommandType {

    /** GWT command template. */
    public static final String COMMAND_TEMPLATE = "mvn clean gwt:run-codeserver";

    private static final String ID           = "gwt";
    private static final String DISPLAY_NAME = "GWT";

    private final GwtResources            resources;
    private final AppContext              appContext;
    private final MachineManager          machineManager;
    private final GwtConfigurationFactory configurationFactory;

    private final Collection<ConfigurationPage<? extends CommandConfiguration>> pages;

    @Inject
    public GwtCommandType(GwtResources resources, GwtCommandPagePresenter page, AppContext appContext, MachineManager machineManager) {
        this.resources = resources;
        this.appContext = appContext;
        this.machineManager = machineManager;
        configurationFactory = new GwtConfigurationFactory(this);
        pages = new LinkedList<>();
        pages.add(page);
    }

    @Nonnull
    @Override
    public String getId() {
        return ID;
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Nonnull
    @Override
    public SVGResource getIcon() {
        return resources.gwtCommandType();
    }

    @Nonnull
    @Override
    public Collection<ConfigurationPage<? extends CommandConfiguration>> getConfigurationPages() {
        return pages;
    }

    @Nonnull
    @Override
    public ConfigurationFactory<GwtCommandConfiguration> getConfigurationFactory() {
        return configurationFactory;
    }

    @Nonnull
    @Override
    public String getCommandTemplate() {
        final StringBuilder commandTemplateBuilder = new StringBuilder(COMMAND_TEMPLATE);

        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            commandTemplateBuilder.append(" -f ").append(currentProject.getRootProject().getName());
        }

        final Machine devMachine = machineManager.getDeveloperMachine();
        if (devMachine != null) {
            final String hostName = devMachine.getMetadata().get("config.hostname");
            if (hostName != null) {
                commandTemplateBuilder.append(" -Dgwt.bindAddress=").append(hostName);
            }
        }

        return commandTemplateBuilder.toString();
    }
}