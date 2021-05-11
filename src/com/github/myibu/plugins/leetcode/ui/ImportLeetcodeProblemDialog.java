package com.github.myibu.plugins.leetcode.ui;

import com.github.myibu.plugins.leetcode.JavaDirectory;
import com.github.myibu.plugins.leetcode.LeetcodeHelper;
import com.github.myibu.plugins.leetcode.LeetcodeProblem;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImportLeetcodeProblemDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField urlTextField;
    private TextFieldWithBrowseButton directoryTextFieldWithBrowser;
    private JTextField classNameTextField;
    private JTextField packageNameTextField;

    private Project project;
    private JavaDirectory directory;

    public ImportLeetcodeProblemDialog(Project project, JavaDirectory directory) {
        this(project);
        this.directory = directory;
        urlTextField.setText(LeetcodeHelper.DEFAULT_LEETCODE_URL);
        directoryTextFieldWithBrowser.setText(directory.absolutePath());
    }

    public ImportLeetcodeProblemDialog(Project project) {
        this.project = project;
        Point point = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        this.setLocation(point.x - 400 / 2, 200);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        // https://plugins.jetbrains.com/docs/intellij/file-and-class-choosers.html#via-textfield
        directoryTextFieldWithBrowser.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFolderDescriptor());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Import problem from leetcode website") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setText("Importing leetcode problem");
                progressIndicator.setIndeterminate(true);
                try {
                    String leetcodeUrl = urlTextField.getText();
                    String saveDir = directoryTextFieldWithBrowser.getText();
                    if (!LeetcodeHelper.isEmpty(leetcodeUrl) && !LeetcodeHelper.isEmpty(saveDir)) {
                        LeetcodeHelper helper = LeetcodeHelper.getInstance();
                        LeetcodeProblem leetcodeProblem = helper.importProblemFromLeetcodeWebsite(leetcodeUrl, saveDir);
                        helper.writeProblem2File(leetcodeProblem, saveDir, classNameTextField.getText(), packageNameTextField.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
