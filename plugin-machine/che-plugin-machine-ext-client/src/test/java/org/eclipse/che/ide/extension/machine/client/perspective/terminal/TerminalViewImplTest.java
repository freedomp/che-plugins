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

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class TerminalViewImplTest {

    @Mock
    private TerminalJso terminalJso;
    @Mock
    private Element     element;

    @InjectMocks
    private TerminalViewImpl view;

    @Test
    public void terminalShouldBeOpened() {
        when(view.terminalPanel.getElement()).thenReturn(element);

        view.openTerminal(terminalJso);

        verify(view.terminalPanel).getElement();
        verify(terminalJso).open(element);
    }
}