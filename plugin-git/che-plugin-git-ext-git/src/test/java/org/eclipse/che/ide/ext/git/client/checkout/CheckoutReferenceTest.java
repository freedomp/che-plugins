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
package org.eclipse.che.ide.ext.git.client.checkout;

import org.eclipse.che.api.git.shared.BranchCheckoutRequest;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectProblem;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.FileContentUpdateEvent;
import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.test.GwtReflectionUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Testing {@link CheckoutReferencePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
public class CheckoutReferenceTest extends BaseTest {
    private static final String CORRECT_REFERENCE   = "someTag";
    private static final String INCORRECT_REFERENCE = "";

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<String>> asyncCallbackCaptor;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<ProjectDescriptor>> projectDescriptorCaptor;

    @Mock
    private CheckoutReferenceView      view;
    @Mock
    private BranchCheckoutRequest      branchCheckoutRequest;

    @Mock
    private EditorPartPresenter    partPresenter;
    @Mock
    private EditorInput            editorInput;
    @Mock
    private EditorAgent            editorAgent;

    @InjectMocks
    private CheckoutReferencePresenter presenter;

    @Override
    public void disarm() {
        super.disarm();
    }

    @Test
    public void testOnReferenceValueChangedWhenValueIsIncorrect() throws Exception {

        presenter.referenceValueChanged(INCORRECT_REFERENCE);

        view.setCheckoutButEnableState(eq(false));
    }

    @Test
    public void testOnReferenceValueChangedWhenValueIsCorrect() throws Exception {

        presenter.referenceValueChanged(CORRECT_REFERENCE);

        view.setCheckoutButEnableState(eq(true));
    }

    @Test
    public void testShowDialog() throws Exception {

        presenter.showDialog();

        verify(view).setCheckoutButEnableState(eq(false));
        verify(view).showDialog();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }


    @Test
    public void onEnterClickedWhenValueIsIncorrect() throws Exception {
        reset(service);
        when(view.getReference()).thenReturn(INCORRECT_REFERENCE);

        presenter.onEnterClicked();

        verify(view, never()).close();
        verify(service, never()).branchCheckout(anyObject(), anyObject(), anyObject());
    }

    @Test
    public void onEnterClickedWhenValueIsCorrect() throws Exception {
        when(dtoFactory.createDto(BranchCheckoutRequest.class)).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withName(anyString())).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withCreateNew(anyBoolean())).thenReturn(branchCheckoutRequest);
        reset(service);
        when(view.getReference()).thenReturn(CORRECT_REFERENCE);

        presenter.onEnterClicked();

        verify(view).close();
        verify(service).branchCheckout(anyObject(), anyObject(), anyObject());
        verify(branchCheckoutRequest).withName(CORRECT_REFERENCE);
        verify(branchCheckoutRequest).withCreateNew(false);
        verifyNoMoreInteractions(branchCheckoutRequest);
    }

    @Test
    public void testOnCheckoutClickedWhenCheckoutIsSuccessful() throws Exception {
        VirtualFile virtualFile = mock(VirtualFile.class);

        NavigableMap<String, EditorPartPresenter> partPresenterMap = new TreeMap<>();
        partPresenterMap.put("partPresenter", partPresenter);

        when(editorAgent.getOpenedEditors()).thenReturn(partPresenterMap);
        when(partPresenter.getEditorInput()).thenReturn(editorInput);

        when(editorInput.getFile()).thenReturn(virtualFile);
        when(virtualFile.getPath()).thenReturn("/foo");

        when(dtoFactory.createDto(BranchCheckoutRequest.class)).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withName(anyString())).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withCreateNew(anyBoolean())).thenReturn(branchCheckoutRequest);
        reset(service);
        when(view.getReference()).thenReturn(CORRECT_REFERENCE);
        when(rootProjectDescriptor.getPath()).thenReturn(PROJECT_PATH);

        presenter.onEnterClicked();

        verify(service).branchCheckout(anyObject(), anyObject(), asyncCallbackCaptor.capture());
        AsyncRequestCallback<String> callback = asyncCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(callback, "");

        verify(branchCheckoutRequest).withName(CORRECT_REFERENCE);
        verify(branchCheckoutRequest).withCreateNew(false);
        verifyNoMoreInteractions(branchCheckoutRequest);
        verify(view).close();
        verify(projectServiceClient).getProject(eq(PROJECT_PATH), projectDescriptorCaptor.capture());
        AsyncRequestCallback<ProjectDescriptor> asyncRequestCallback = projectDescriptorCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(asyncRequestCallback, projectDescriptor);
        verify(projectDescriptor).getProblems();
        verify(projectExplorer).reloadChildren();
        verify(editorAgent).getOpenedEditors();
        verify(partPresenter).getEditorInput();
        verify(editorInput).getFile();
        verify(eventBus).fireEvent(Matchers.<FileContentUpdateEvent>anyObject());
    }

    @Test
    public void testOnCheckoutClickedWhenCheckoutIsSuccessfulButProjectIsNotConfigurated() throws Exception {
        List<ProjectProblem> problemList = Collections.singletonList(mock(ProjectProblem.class));
        when(projectDescriptor.getProblems()).thenReturn(problemList);

        when(dtoFactory.createDto(BranchCheckoutRequest.class)).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withName(anyString())).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withCreateNew(anyBoolean())).thenReturn(branchCheckoutRequest);
        reset(service);
        when(view.getReference()).thenReturn(CORRECT_REFERENCE);
        when(rootProjectDescriptor.getPath()).thenReturn(PROJECT_PATH);

        presenter.onEnterClicked();

        verify(service).branchCheckout(anyObject(), anyObject(), asyncCallbackCaptor.capture());
        AsyncRequestCallback<String> callback = asyncCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(callback, "");

        verify(branchCheckoutRequest).withName(CORRECT_REFERENCE);
        verify(branchCheckoutRequest).withCreateNew(false);
        verifyNoMoreInteractions(branchCheckoutRequest);
        verify(view).close();
        verify(projectServiceClient).getProject(eq(PROJECT_PATH), projectDescriptorCaptor.capture());
        AsyncRequestCallback<ProjectDescriptor> asyncRequestCallback = projectDescriptorCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(asyncRequestCallback, projectDescriptor);
        verify(projectDescriptor).getProblems();
        verify(eventBus).fireEvent(Matchers.<OpenProjectEvent>anyObject());
    }

    @Test
    public void testOnCheckoutClickedWhenCheckoutIsFailed() throws Exception {
        when(dtoFactory.createDto(BranchCheckoutRequest.class)).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withName(anyString())).thenReturn(branchCheckoutRequest);
        when(branchCheckoutRequest.withCreateNew(anyBoolean())).thenReturn(branchCheckoutRequest);

        reset(service);
        when(view.getReference()).thenReturn(CORRECT_REFERENCE);
        when(rootProjectDescriptor.getPath()).thenReturn(PROJECT_PATH);

        presenter.onEnterClicked();

        verify(service).branchCheckout(anyObject(), anyObject(), asyncCallbackCaptor.capture());
        AsyncRequestCallback<String> callback = asyncCallbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));

        verify(branchCheckoutRequest).withName(CORRECT_REFERENCE);
        verify(branchCheckoutRequest).withCreateNew(false);
        verifyNoMoreInteractions(branchCheckoutRequest);
        verify(view).close();
        verify(eventBus, never()).fireEvent(Matchers.<OpenProjectEvent>anyObject());
        verify(console).printError(anyString());
        verify(notificationManager).showError(anyString());
    }
}