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
package org.eclipse.che.ide.ext.python.server.inject;

import org.eclipse.che.api.project.server.ValueProviderFactory;
import org.eclipse.che.api.project.server.type.ProjectType;
import org.eclipse.che.ide.ext.python.server.project.type.PythonProjectType;
import org.eclipse.che.ide.ext.python.server.project.type.PythonValueProviderFactory;
import org.eclipse.che.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/** @author Vladyslav Zhukovskii */
@DynaModule
public class PythonModule extends AbstractModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        Multibinder<ValueProviderFactory> multiBinder = Multibinder.newSetBinder(binder(), ValueProviderFactory.class);
        multiBinder.addBinding().to(PythonValueProviderFactory.class);

        Multibinder<ProjectType> projectTypeMultibinder = Multibinder.newSetBinder(binder(), ProjectType.class);
        projectTypeMultibinder.addBinding().to(PythonProjectType.class);
    }
}
