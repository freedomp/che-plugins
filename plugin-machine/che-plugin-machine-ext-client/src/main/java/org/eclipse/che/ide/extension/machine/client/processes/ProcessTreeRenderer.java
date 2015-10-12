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

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.html.SpanElement;

import com.google.inject.Inject;

import org.eclipse.che.api.machine.shared.dto.MachineDescriptor;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel.MachineTreeNode;
import org.eclipse.che.ide.ui.tree.NodeRenderer;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;
import org.eclipse.che.ide.util.dom.Elements;
import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * Renderer for {@ProcessTreeNode} UI presentation.
 *
 * @author Anna Shumilova
 */
public class ProcessTreeRenderer implements NodeRenderer<ProcessTreeNode> {

    private final MachineResources resources;
    private final MachineLocalizationConstant locale;

    @Inject
    public ProcessTreeRenderer(MachineResources resources, MachineLocalizationConstant locale) {
        this.resources = resources;
        this.locale = locale;
    }

    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    @Override
    public SpanElement renderNodeContents(ProcessTreeNode node) {
        if (node.getData() instanceof MachineDescriptor) {
            return createMachineElement((MachineDescriptor)node.getData());
        } else if (node.getData() instanceof CommandConfiguration) {
            return createCommandElement((CommandConfiguration)node.getData());
        } else {
            //TODO terminal node
        }
        return Elements.createSpanElement();
    }


    private SpanElement createMachineElement(MachineDescriptor machine) {
        SpanElement root = Elements.createSpanElement();
        if (machine.isDev()) {
            SpanElement devLabel = Elements.createSpanElement(resources.getCss().devMachineLabel());
            devLabel.setTextContent(locale.viewProcessesDevTitle());
            root.appendChild(devLabel);
        }
        Element nameElement = Elements.createSpanElement();
        nameElement.setTextContent(machine.getDisplayName());

        Element statusElement = Elements.createSpanElement(resources.getCss().machineStatus());
        root.appendChild(statusElement);

        root.appendChild(nameElement);

        return root;
    }

    private SpanElement createCommandElement(CommandConfiguration command) {
        SpanElement root = Elements.createSpanElement();

        SpanElement iconElement = Elements.createSpanElement(resources.getCss().processIcon());
        iconElement.appendChild((Node)resources.output().getSvg().getElement());

        root.appendChild(iconElement);

        Element nameElement = Elements.createSpanElement();
        nameElement.setTextContent(command.getName());
        root.appendChild(nameElement);

        return root;
    }


    @Override
    public void updateNodeContents(TreeNodeElement<ProcessTreeNode> treeNode) {
    }
}
