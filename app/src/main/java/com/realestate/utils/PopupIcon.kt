package com.realestate.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.realestate.R

/**
 * Created by Chandan on 28/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class PopupIcon {

    /**
     * Moves icons from the PopupMenu's MenuItems' icon fields into the menu title as a Spannable with the icon and title text.
     */
    internal fun insertMenuItemIcons(context: Context, popupMenu: PopupMenu) {
        val menu: Menu = popupMenu.getMenu()
        if (hasIcon(menu)) {
            for (i in 0 until menu.size()) {
                insertMenuItemIcon(context, menu.getItem(i))
            }
        }
    }

    /**
     * @return true if the menu has at least one MenuItem with an icon.
     */
    private fun hasIcon(menu: Menu): Boolean {
        for (i in 0 until menu.size()) {
            if (menu.getItem(i).getIcon() != null) return true
        }
        return false
    }

    /**
     * Converts the given MenuItem's title into a Spannable containing both its icon and title.
     */
    private fun insertMenuItemIcon(context: Context, menuItem: MenuItem) {
        var icon: Drawable = menuItem.getIcon()

        // If there's no icon, we insert a transparent one to keep the title aligned with the items
        // which do have icons.
        if (icon == null) icon = ColorDrawable(Color.TRANSPARENT)
        val iconSize: Int =
            context.getResources().getDimensionPixelSize(R.dimen.text_margin_20dp)
        icon.setBounds(0, 0, iconSize, iconSize)
        val imageSpan = ImageSpan(icon)

        // Add a space placeholder for the icon, before the title.
        val ssb = SpannableStringBuilder("       " + menuItem.getTitle())

        // Replace the space placeholder with the icon.
        ssb.setSpan(imageSpan, 1, 2, 0)
        menuItem.setTitle(ssb)
        // Set the icon to null just in case, on some weird devices, they've customized Android to display
        // the icon in the menu... we don't want two icons to appear.
        menuItem.setIcon(null)
    }
}