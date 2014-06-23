package uk.co.neylan.plugins.makeiteasy.action;

import com.intellij.codeInsight.navigation.GotoTargetHandler;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class GoToMakerAdditionalAction implements GotoTargetHandler.AdditionalAction {

    static final String TEXT = "Create New Maker...";
    static final Icon ICON = IconLoader.getIcon("/actions/intentionBulb.png");

    @Override
    public String getText() {
        return TEXT;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public void execute() {
    }
}
