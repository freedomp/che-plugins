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
package org.eclipse.che.ide.extension.machine.client.processes;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.machine.gwt.client.MachineServiceClient;
import org.eclipse.che.api.machine.shared.*;
import org.eclipse.che.api.machine.shared.Process;
import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.api.machine.shared.dto.ProcessDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.HasView;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for managing machines process and terminals.
 *
 * @author Anna Shumilova
 */
@Singleton
public class ProcessesPresenter extends BasePresenter implements ProcessesView.ActionDelegate, HasView{

    private final MachineLocalizationConstant localizationConstant;
    private final DialogFactory               dialogFactory;
    private final ProcessesView               view;
    private final MachineResources            resources;
    private final AppContext                  appContext;
    private final MachineServiceClient        machineService;
    private final EntityFactory               entityFactory;
    private ProcessTreeNode                   rootNode;

    @Inject
    public ProcessesPresenter(ProcessesView view,
                              MachineLocalizationConstant localizationConstant,
                              EventBus eventBus,
                              MachineServiceClient machineService,
                              DialogFactory dialogFactory,
                              EntityFactory entityFactory,
                              MachineResources resources, AppContext appContext) {
        this.view = view;
        this.localizationConstant = localizationConstant;
        this.dialogFactory = dialogFactory;
        this.resources = resources;
        this.entityFactory = entityFactory;
        this.appContext = appContext;
        this.machineService = machineService;

        this.view.setDelegate(this);
        this.fetchMachines();
    }

    @Override
    public View getView() {
        return view;
    }

    @NotNull
    @Override
    public String getTitle() {
        return localizationConstant.viewProcessesTitle();
    }

    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    @Nullable
    @Override
    public SVGResource getTitleSVGImage() {
        return resources.terminal();
    }

    @Override
    public String getTitleToolTip() {
        return localizationConstant.viewProcessesTooltip();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** Get the list of all available machines.*/
    public void fetchMachines() {
        String workspaceId = appContext.getWorkspace().getId();

        Promise<List<MachineDescriptor>> machinesPromise = machineService.getWorkspaceMachines(workspaceId);

        machinesPromise.then(new Operation<List<MachineDescriptor>>() {
            @Override
            public void apply(List<MachineDescriptor> machines) throws OperationException {
                List<ProcessTreeNode> rootChildren = new ArrayList<>();

                rootNode = new ProcessTreeNode(null, "root", rootChildren);

                for (MachineDescriptor descriptor : machines) {
                    if (descriptor.isDev()) {
                        List<ProcessTreeNode> processTreeNodes = new ArrayList<ProcessTreeNode>();
                        ProcessTreeNode machineNode = new ProcessTreeNode(rootNode, descriptor, processTreeNodes);
                        rootChildren.add(machineNode);
                        view.setProcessesData(rootNode);
                        //TODO fetchProcesses(descriptor.getId(), processTreeNodes, machineNode);
                    }
                }

            }
        });
    }

    public void addCommand(@NotNull String machineId, @NotNull CommandConfiguration configuration) {
        ProcessTreeNode machineTreeNode = findProcessTreeNodeById(machineId);
        if (machineTreeNode != null) {
            machineTreeNode.getChildren().add(new ProcessTreeNode(machineTreeNode, configuration, null));
            view.setProcessesData(rootNode);
        } else {
            //TODO
        }
    }

    private ProcessTreeNode findProcessTreeNodeById(@NotNull String id) {
        for (ProcessTreeNode processTreeNode : rootNode.getChildren()) {
            if (id.equals(processTreeNode.getId())) {
                return processTreeNode;
            }
        }
        return null;
    }

    /*private void fetchProcesses(String machineId, final List<ProcessTreeNode> processTreeNodes, final ProcessTreeNode machineNode) {
        Promise<List<ProcessDescriptor>> processesPromise = machineService.getProcesses(machineId);
        processesPromise.then(new Operation<List<ProcessDescriptor>>() {
            @Override
            public void apply(List<ProcessDescriptor> processDescriptors) throws OperationException {
                for (ProcessDescriptor processDescriptor : processDescriptors) {
                    ProcessTreeNode processTreeNode = new ProcessTreeNode(machineNode, processDescriptor, null);
                    processTreeNodes.add(processTreeNode);
                }
                view.setProcessesData(rootNode);
            }
        });
    }*/
}
