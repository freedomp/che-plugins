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
package org.eclipse.che.ide.ext.svn.client.diff;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.ext.svn.client.SubversionClientService;
import org.eclipse.che.ide.ext.svn.client.common.RawOutputPresenter;
import org.eclipse.che.ide.ext.svn.client.common.SubversionActionPresenter;
import org.eclipse.che.ide.ext.svn.shared.CLIOutputResponse;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

/**
 * Handler for the {@link org.eclipse.che.ide.ext.svn.client.action.DiffAction} action.
 */
@Singleton
public class DiffPresenter extends SubversionActionPresenter {

    private final DtoUnmarshallerFactory                   dtoUnmarshallerFactory;
    private final NotificationManager                      notificationManager;
    private final SubversionClientService                  service;

    /**
     * Constructor.
     */
    @Inject
    protected DiffPresenter(final AppContext appContext,
                            final DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            final EventBus eventBus,
                            final NotificationManager notificationManager,
                            final RawOutputPresenter console,
                            final SubversionClientService service,
                            final WorkspaceAgent workspaceAgent,
                            final ProjectExplorerPresenter projectExplorerPart) {
        super(appContext, eventBus, console, workspaceAgent, projectExplorerPart);

        this.service = service;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
    }

    public void showDiff() {
        final String projectPath = getCurrentProjectPath();

        if (projectPath == null) {
            return;
        }

        service.showDiff(projectPath, getSelectedPaths(), "HEAD",
                new AsyncRequestCallback<CLIOutputResponse>(dtoUnmarshallerFactory.newUnmarshaller(CLIOutputResponse.class)) {
                    @Override
                    protected void onSuccess(CLIOutputResponse result) {
                        printResponse(result.getCommand(), result.getOutput(), result.getErrOutput());
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showError(exception.getMessage());
                    }
                });
    }

}
