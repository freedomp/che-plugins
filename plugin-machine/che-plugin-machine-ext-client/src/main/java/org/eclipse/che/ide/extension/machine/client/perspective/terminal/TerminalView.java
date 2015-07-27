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
package org.eclipse.che.ide.extension.machine.client.perspective.terminal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * The interface defines methods to control displaying of terminal.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TerminalViewImpl.class)
interface TerminalView extends IsWidget {

    /**
     * Change visibility state of panel.
     *
     * @param visible
     *         <code>true</code> panel is visible,<code>false</code> panel is not visible
     */
    void setVisible(boolean visible);

    /**
     * Opens current terminal.
     *
     * @param terminal
     *         terminal which will be opened
     */
    void openTerminal(@Nonnull TerminalJso terminal);
}
