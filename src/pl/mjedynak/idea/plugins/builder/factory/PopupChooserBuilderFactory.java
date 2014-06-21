package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.openapi.ui.popup.PopupChooserBuilder;

import javax.swing.*;

public class PopupChooserBuilderFactory {

    public PopupChooserBuilder getPopupChooserBuilder(JList list) {
        return new PopupChooserBuilder(list);
    }
}
