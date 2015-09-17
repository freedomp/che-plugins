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
package org.eclipse.che.ide.ext.java.client.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.dependenciesupdater.JavaClasspathServiceClient;
import org.eclipse.che.ide.ext.java.client.dependenciesupdater.JavaClasspathServiceClientImpl;
import org.eclipse.che.ide.ext.java.client.documentation.QuickDocPresenter;
import org.eclipse.che.ide.ext.java.client.documentation.QuickDocumentation;
import org.eclipse.che.ide.ext.java.client.inject.factories.PropertyWidgetFactory;
import org.eclipse.che.ide.ext.java.client.navigation.JavaNavigationService;
import org.eclipse.che.ide.ext.java.client.navigation.JavaNavigationServiceImpl;
import org.eclipse.che.ide.ext.java.client.newsourcefile.NewJavaSourceFileView;
import org.eclipse.che.ide.ext.java.client.newsourcefile.NewJavaSourceFileViewImpl;
import org.eclipse.che.ide.ext.java.client.projecttree.JavaNodeFactory;
import org.eclipse.che.ide.ext.java.client.refactoring.rename.JavaRefactoringRename;
import org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsPresenter;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidget;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidgetImpl;
import org.eclipse.che.ide.settings.common.SettingsPagePresenter;

/**
 * @author Evgen Vidolob
 * @author Artem Zatsarynnyy
 */
@ExtensionGinModule
public class JavaGinModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(NewJavaSourceFileView.class).to(NewJavaSourceFileViewImpl.class).in(Singleton.class);
        bind(QuickDocumentation.class).to(QuickDocPresenter.class).in(Singleton.class);
        bind(JavaNavigationService.class).to(JavaNavigationServiceImpl.class);
        bind(JavaClasspathServiceClient.class).to(JavaClasspathServiceClientImpl.class);
        bind(JavaRefactoringRename.class);

        install(new GinFactoryModuleBuilder().build(JavaNodeFactory.class));
        install(new GinFactoryModuleBuilder().implement(PropertyWidget.class, PropertyWidgetImpl.class)
                                             .build(PropertyWidgetFactory.class));

        GinMultibinder<SettingsPagePresenter> settingsBinder = GinMultibinder.newSetBinder(binder(), SettingsPagePresenter.class);
        settingsBinder.addBinding().to(ErrorWarningsPresenter.class);
    }

    @Provides
    @Singleton
    @Named("JavaFileType")
    protected FileType provideJavaFile() {
        return new FileType("Java", JavaResources.INSTANCE.javaFile(), MimeType.TEXT_X_JAVA, "java");
    }

    @Provides
    @Singleton
    @Named("JavaClassFileType")
    protected FileType provideJavaClassFile() {
        return new FileType("Java Class", JavaResources.INSTANCE.javaClassIcon(), MimeType.APPLICATION_JAVA_CLASS, "class");
    }

    @Provides
    @Singleton
    @Named("JspFileType")
    protected FileType provideJspFile() {
        return new FileType("Jsp", JavaResources.INSTANCE.jspFile(), MimeType.APPLICATION_JSP, "jsp");
    }


    @Provides
    @Singleton
    @Named("JsfFileType")
    protected FileType provideJsfFile() {
        return new FileType("Jsf", JavaResources.INSTANCE.jsfFile(), MimeType.TEXT_X_JAVA, "jsf");
    }
}