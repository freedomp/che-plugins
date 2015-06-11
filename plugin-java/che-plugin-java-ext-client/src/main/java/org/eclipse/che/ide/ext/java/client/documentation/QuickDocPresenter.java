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
package org.eclipse.che.ide.ext.java.client.documentation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.ext.java.client.projecttree.JavaSourceFolderUtil;
import org.eclipse.che.ide.jseditor.client.position.PositionConverter;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import org.eclipse.che.ide.util.loging.Log;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class QuickDocPresenter implements QuickDocumentation, QuickDocView.ActionDelegate {


    private QuickDocView view;
    private AppContext   appContext;
    private String       caContext;
    private EditorAgent  editorAgent;

    @Inject
    public QuickDocPresenter(QuickDocView view, AppContext appContext, @Named("javaCA") String caContext, EditorAgent editorAgent) {
        this.view = view;
        this.appContext = appContext;
        this.caContext = caContext;
        this.editorAgent = editorAgent;
    }

    @Override
    public void showDocumentation() {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
        if (activeEditor == null) {
            return;
        }

        if (!(activeEditor instanceof EmbeddedTextEditorPresenter)) {
            Log.error(getClass(), "Quick Document support only EmbeddedTextEditorPresenter as editor");
            return;
        }

        EmbeddedTextEditorPresenter editor = ((EmbeddedTextEditorPresenter)activeEditor);
        int offset = editor.getCursorOffset();
        final PositionConverter.PixelCoordinates coordinates = editor.getPositionConverter().offsetToPixel(offset);
        view.show(caContext + "/jdt/javadoc/" + appContext.getWorkspace().getId() + "/find?fqn=" + JavaSourceFolderUtil.getFQNForFile(editor.getEditorInput().getFile()) + "&projectpath=" +
                  appContext.getCurrentProject().getProjectDescription().getPath() + "&offset=" + offset, coordinates.getX(), coordinates.getY() + 16);
    }

    @Override
    public void onCloseView() {

    }
}
