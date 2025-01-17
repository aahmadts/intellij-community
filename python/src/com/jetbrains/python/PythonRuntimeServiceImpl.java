// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.jetbrains.python;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.jetbrains.python.console.PydevConsoleRunner;
import com.jetbrains.python.console.PydevDocumentationProvider;
import com.jetbrains.python.console.completion.PydevConsoleReference;
import com.jetbrains.python.console.pydev.ConsoleCommunication;
import com.jetbrains.python.documentation.PyRuntimeDocstringFormatter;
import com.jetbrains.python.documentation.docstrings.DocStringFormat;
import com.jetbrains.python.parsing.console.PythonConsoleData;
import com.jetbrains.python.psi.LanguageLevel;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.resolve.PyResolveContext;
import com.jetbrains.python.sdk.PythonSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PythonRuntimeServiceImpl extends PythonRuntimeService {
  @Override
  public boolean isInPydevConsole(@NotNull PsiElement file) {
    return PydevConsoleRunner.isInPydevConsole(file);
  }

  @Nullable
  @Override
  public Sdk getConsoleSdk(@NotNull PsiElement foothold) {
    return PydevConsoleRunner.getConsoleSdk(foothold);
  }

  @Override
  public String createPydevDoc(PsiElement element, PsiElement originalElement) {
    return PydevDocumentationProvider.createDoc(element, originalElement);
  }

  @NotNull
  @Override
  public LanguageLevel getLanguageLevelForSdk(@Nullable Sdk sdk) {
    return PythonSdkType.getLanguageLevelForSdk(sdk);
  }

  @Override
  public PsiPolyVariantReference getPydevConsoleReference(@NotNull PyReferenceExpression element,
                                                          @NotNull PyResolveContext context) {
    PsiFile file = element.getContainingFile();
    if (file != null) {
      final ConsoleCommunication communication = file.getCopyableUserData(PydevConsoleRunner.CONSOLE_COMMUNICATION_KEY);
      if (communication != null) {
        PyExpression qualifier = element.getQualifier();
        final String prefix = qualifier == null ? "" : qualifier.getText() + ".";
        return new PydevConsoleReference(element, communication, prefix, context.allowRemote());
      }
    }
    return null;
  }

  @Override
  public PythonConsoleData getPythonConsoleData(@Nullable ASTNode node) {
      return PydevConsoleRunner.getPythonConsoleData(node);
  }

  @Override
  public String formatDocstring(Module module, DocStringFormat format, String docstring) {
    return PyRuntimeDocstringFormatter.runExternalTool(module, format, docstring);
  }
}
