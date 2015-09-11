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
package org.eclipse.che.ide.extension.machine.client.machine.extserver;

import com.google.gwt.core.client.Callback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.bootstrap.ProjectTemplatesComponent;
import org.eclipse.che.ide.bootstrap.ProjectTypeComponent;
import org.eclipse.che.ide.core.Component;
import org.eclipse.che.api.machine.gwt.client.events.ExtServerStateEvent;
import org.eclipse.che.api.machine.gwt.client.events.ExtServerStateHandler;
import org.eclipse.che.ide.util.loging.Log;

/**
 * @author Roman Nikitenko
 */
@Singleton
public class ProjectApiComponentInitializer {

    private ProjectTypeComponent      projectTypeComponent;
    private ProjectTemplatesComponent projectTemplatesComponent;

    @Inject
    public ProjectApiComponentInitializer(EventBus eventBus,
                                          ProjectTypeComponent projectTypeComponent,
                                          ProjectTemplatesComponent projectTemplatesComponent) {
        this.projectTypeComponent = projectTypeComponent;
        this.projectTemplatesComponent = projectTemplatesComponent;

        eventBus.addHandler(ExtServerStateEvent.TYPE, new ExtServerStateHandler() {
            @Override
            public void onExtServerStarted(ExtServerStateEvent event) {
                initialize();
            }

            @Override
            public void onExtServerStopped(ExtServerStateEvent event) {

            }

            @Override
            public void onExtServerFailed(ExtServerStateEvent event) {

            }
        });
    }

    public void initialize() {
        startProjectTypeComponent();
        startProjectTemplatesComponent();
    }

    private void startProjectTypeComponent() {
        projectTypeComponent.start(new Callback<Component, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Log.error(getClass(), reason.getMessage());
            }

            @Override
            public void onSuccess(Component result) {
            }
        });
    }

    private void startProjectTemplatesComponent() {
        projectTemplatesComponent.start(new Callback<Component, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Log.error(getClass(), reason.getMessage());
            }

            @Override
            public void onSuccess(Component result) {
            }
        });
    }
}
