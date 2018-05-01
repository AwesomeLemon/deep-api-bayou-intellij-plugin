package com.github.awesomelemon;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class HelloAction extends AnAction {
    public HelloAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        ProgressManager.getInstance().run(new Task.Modal(project, "daf", false) {
            public void run(ProgressIndicator indicator) {
                indicator.setText("This is how you update the indicator");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                indicator.setFraction(0.5);  // halfway done
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
            }
        });
        Messages.showMessageDialog(project, "Hello world!", "Greeting", Messages.getInformationIcon());
    }
}