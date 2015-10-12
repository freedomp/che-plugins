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
package org.eclipse.che.ide.extension.machine.client.command.arbitrary;

import org.eclipse.che.api.machine.shared.dto.CommandDto;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfigurationFactory;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;

import javax.validation.constraints.NotNull;

/**
 * Factory for {@link ArbitraryCommandConfiguration} instances.
 *
 * @author Artem Zatsarynnyy
 */
public class ArbitraryCommandConfigurationFactory extends CommandConfigurationFactory<ArbitraryCommandConfiguration> {

    protected ArbitraryCommandConfigurationFactory(@NotNull CommandType commandType) {
        super(commandType);
    }

    @NotNull
    @Override
    public ArbitraryCommandConfiguration createFromDto(@NotNull CommandDto descriptor) {
        final ArbitraryCommandConfiguration configuration = new ArbitraryCommandConfiguration(getCommandType(), descriptor.getName());
        configuration.setCommandLine(descriptor.getCommandLine());
        return configuration;
    }
}
