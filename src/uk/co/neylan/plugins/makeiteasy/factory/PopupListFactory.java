package uk.co.neylan.plugins.makeiteasy.factory;

import com.intellij.ui.components.JBList;
import uk.co.neylan.plugins.makeiteasy.action.GoToMakerAdditionalAction;
import pl.mjedynak.idea.plugins.builder.renderer.ActionCellRenderer;

import javax.swing.*;
import java.util.Arrays;

public class PopupListFactory {

    private final ActionCellRenderer actionCellRenderer = new ActionCellRenderer();

    @SuppressWarnings("unchecked")
    public JList getPopupList() {
        JList list = new JBList(Arrays.asList(new GoToMakerAdditionalAction()));
        list.setCellRenderer(actionCellRenderer);
        return list;
    }
}
