/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.jdt.internal.corext.refactoring.util;

import org.eclipse.che.ide.ext.java.jdt.core.ToolFactory;
import org.eclipse.che.ide.ext.java.jdt.core.compiler.IScanner;
import org.eclipse.che.ide.ext.java.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.che.ide.ext.java.jdt.core.compiler.InvalidInputException;
import org.eclipse.che.ide.ext.java.jdt.internal.corext.dom.Selection;
import org.eclipse.che.ide.ext.java.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.che.ide.ext.java.jdt.refactoring.RefactoringStatus;
import org.eclipse.che.ide.runtime.Assert;


public class CommentAnalyzer {

    private CommentAnalyzer() {
    }

    public static RefactoringStatus perform(Selection selection, IScanner scanner, int start, int length) {
        RefactoringStatus result = new RefactoringStatus();
        if (length <= 0)
            return result;
        new CommentAnalyzer().check(result, selection, scanner, start, start + length - 1);
        return result;
    }

    private void check(RefactoringStatus result, Selection selection, IScanner scanner, int start, int end) {
        char[] characters = scanner.getSource();
        selection = adjustSelection(characters, selection, end);
        scanner.resetTo(start, end);

        int token = 0;
        try {
            loop:
            while (token != ITerminalSymbols.TokenNameEOF) {
                token = scanner.getNextToken();
                switch (token) {
                    case ITerminalSymbols.TokenNameCOMMENT_LINE:
                    case ITerminalSymbols.TokenNameCOMMENT_BLOCK:
                    case ITerminalSymbols.TokenNameCOMMENT_JAVADOC:
                        if (checkStart(scanner, selection.getOffset())) {
                            result.addFatalError(RefactoringCoreMessages.INSTANCE.CommentAnalyzer_starts_inside_comment());
                            break loop;
                        }
                        if (checkEnd(scanner, selection.getInclusiveEnd())) {
                            result.addFatalError(RefactoringCoreMessages.INSTANCE.CommentAnalyzer_ends_inside_comment());
                            break loop;
                        }
                        break;
                }
            }
        } catch (InvalidInputException e) {
            result.addFatalError(RefactoringCoreMessages.INSTANCE.CommentAnalyzer_internal_error());
        }
    }

    private boolean checkStart(IScanner scanner, int position) {
        return scanner.getCurrentTokenStartPosition() < position && position <= scanner.getCurrentTokenEndPosition();
    }

    private boolean checkEnd(IScanner scanner, int position) {
        return scanner.getCurrentTokenStartPosition() <= position && position < scanner.getCurrentTokenEndPosition();
    }

    private Selection adjustSelection(char[] characters, Selection selection, int end) {
        int newEnd = selection.getInclusiveEnd();
        for (int i = selection.getExclusiveEnd(); i <= end; i++) {
            char ch = characters[i];
            if (ch != '\n' && ch != '\r')
                break;
            newEnd++;
        }
        return Selection.createFromStartEnd(selection.getOffset(), newEnd);
    }

    /**
     * Removes comments and whitespace
     *
     * @param reference
     *         the type reference
     * @return the reference only consisting of dots and java identifier characters
     */
    public static String normalizeReference(String reference) {
        IScanner scanner = ToolFactory.createScanner(false, false, false, false);
        scanner.setSource(reference.toCharArray());
        StringBuffer sb = new StringBuffer();
        try {
            int tokenType = scanner.getNextToken();
            while (tokenType != ITerminalSymbols.TokenNameEOF) {
                sb.append(scanner.getRawTokenSource());
                tokenType = scanner.getNextToken();
            }
        } catch (InvalidInputException e) {
            Assert.isTrue(false, reference);
        }
        reference = sb.toString();
        return reference;
    }
}
