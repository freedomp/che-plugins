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
package org.eclipse.che.ide.ext.java.client.newsourcefile;

/**
 * Type of Java source file.
 *
 * @author Artem Zatsarynnyy
 */
enum JavaSourceFileType {
    CLASS("Class"),
    INTERFACE("Interface"),
    ENUM("Enum"),
    ANNOTATION("Annotation");

    private final String value;

    private JavaSourceFileType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
