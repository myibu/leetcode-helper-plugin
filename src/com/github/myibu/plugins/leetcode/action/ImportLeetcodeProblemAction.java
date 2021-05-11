package com.github.myibu.plugins.leetcode.action;

import com.github.myibu.plugins.leetcode.JavaDirectory;
import com.github.myibu.plugins.leetcode.ui.ImportLeetcodeProblemDialog;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.presentation.java.PackagePresentationProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImportLeetcodeProblemAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // set select directory
        PsiElement selectElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        JavaDirectory directory = new JavaDirectory(Objects.requireNonNull(event.getProject()).getBasePath());
        if (selectElement != null) {
            if (selectElement instanceof PsiFile) {
                directory = new JavaDirectory(Objects.requireNonNull((PsiFile) selectElement).getVirtualFile().getPath());
            }
            else if (selectElement instanceof PsiDirectory) {
                String packageName = Objects.requireNonNull(((PsiDirectory) selectElement).getPresentation()).getLocationString();
                String directoryName = Objects.requireNonNull((PsiDirectory) selectElement).getVirtualFile().getPath();
                if (directoryName.equals(packageName)) {
                    directory = new JavaDirectory(true,
                            Objects.requireNonNull((PsiDirectory) selectElement).getVirtualFile().getPath(),
                            packageName);
                } else {
                    directory = new JavaDirectory(Objects.requireNonNull((PsiDirectory) selectElement).getVirtualFile().getPath());
                }
            }
        }
        ImportLeetcodeProblemDialog dialog = new ImportLeetcodeProblemDialog(event.getProject(), directory);
        dialog.pack();
        dialog.setVisible(true);
    }
}