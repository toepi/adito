/**
 * 
 */
package com.adito.extensions.itemactions;

import com.adito.extensions.ExtensionBundleItem;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.SessionInfo;
import com.adito.table.AvailableTableItemAction;
import com.adito.table.TableItemAction;

public final class ExtensionInformationAction extends TableItemAction {

    public ExtensionInformationAction() {
        super("extensionInformation", "extensions", 400, "", true, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
    }

    public boolean isEnabled(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return item.getBundle().getInstructionsURL()!=null && !item.getBundle().getInstructionsURL().equals("") && !item.getSubFormName().equals("updateableExtensionsForm");
    }

    public String getOnClick(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return "window.open('" + item.getBundle().getInstructionsURL() + "')";
    }
}