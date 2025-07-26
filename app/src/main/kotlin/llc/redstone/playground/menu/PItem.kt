package llc.redstone.playground.menu

import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.smallCapsFont
import llc.redstone.playground.utils.successColor
import llc.redstone.playground.utils.wrapLoreLines
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.Click
import net.minestom.server.inventory.click.Click.Left
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class PItem(material: Material = Material.AIR, var builder: ItemBuilder = ItemBuilder(material)) {
    constructor(builder: ItemBuilder) : this(Material.AIR, builder)

    // First line of lore &8 formatting before it
    var info: String? = null
        private set

    // Blank line
    // Wrapped lore
    var description: String? = null
        private set

    // Blank line
    /*
       Data display
       First String is the emoji, second is the rest of the line
       Formatted as:
       <bold><success> <emoji> <line></success></bold>
    */
    var data = mutableMapOf<String, Pair<String, TextColor?>>()

    // Blank line
    // Click actions
    var leftClick: Pair<String?, ((Item, Click) -> PItem?)>? = null
        private set
    var rightClick: Pair<String?, ((Item, Click) -> PItem?)>? = null
        private set

    fun name(name: String): PItem {
        builder.setDisplayName(colorize(name))
        return this
    }

    fun info(info: String): PItem {
        this.info = info
        return this
    }

    fun description(description: String): PItem {
        this.description = description
        return this
    }

    fun data(emoji: String, line: String, color: TextColor?): PItem {
        data[emoji] = Pair(line, color)
        return this
    }

    fun leftClick(display: String, action: (Item, Click) -> PItem?): PItem {
        this.leftClick = Pair(display, action)
        return this
    }

    fun leftClick(action: (Item, Click) -> PItem?): PItem {
        this.leftClick = Pair(null, action)
        return this
    }

    fun rightClick(display: String, action: (Item, Click) -> PItem?): PItem {
        this.rightClick = Pair(display, action)
        return this
    }

    fun rightClick(action: (Item, Click) -> PItem?): PItem {
        this.rightClick = Pair(null, action)
        return this
    }

    fun longestLine(): Int {
        var longestLine = 0
        if (info != null) {
            longestLine = info!!.length
        }
        data.forEach { (emoji, pair) ->
            val lineLength = emoji.length + pair.first.length + 2
            longestLine = maxOf(longestLine, lineLength)
        }
        if (leftClick != null) {
            longestLine = maxOf(longestLine, leftClick!!.first?.length ?: 0)
        }
        if (rightClick != null) {
            longestLine = maxOf(longestLine, rightClick!!.first?.length ?: 0)
        }

        if (longestLine < 20) {
            longestLine = 20 // Minimum length for aesthetic reasons
        }
        return longestLine + 2 // Just so it doesnt look weird
    }

    fun build(): ItemBuilder {
        val lore = mutableListOf<Component>()
        if (info != null) {
            lore.add(colorize("<dark_gray>$info</dark_gray>"))
        }
        if (description != null) {
            lore.add(Component.empty())
            lore.addAll(wrapLoreLines(colorize("<gray>${description!!}</gray>"), minOf(longestLine(), 40)))
        }
        if (data.isNotEmpty()) {
            lore.add(Component.empty())
            data.forEach { (emoji, pair) ->
                val color = (pair.second ?: successColor)!!.asHexString()
                lore.add(colorize("<${color}><bold> $emoji </bold>${pair.first}</${color}>"))
            }
        }
        if (leftClick?.first != null || rightClick?.first != null) {
            lore.add(Component.empty())
            if (leftClick?.first != null) {
                lore.add(colorize("<bold><primary_click>◀ ${smallCapsFont(leftClick!!.first!!)}</primary_click></bold>"))
            }
            if (rightClick?.first != null) {
                lore.add(colorize("<bold><secondary_click>▶ ${smallCapsFont(rightClick!!.first!!)}</secondary_click></bold>"))
            }
        }
        builder.setLore(lore)
        return builder
    }

    fun from(rItem: PItem): PItem {
        this.builder = rItem.builder
        this.info = rItem.info
        this.description = rItem.description
        this.data = rItem.data
        this.leftClick = rItem.leftClick
        this.rightClick = rItem.rightClick
        return this
    }

    //Haha funny
    fun buildItemStack(): ItemStack {
        return build().get()
    }

    // If we aint building, are we even doing it right?
    fun buildItem(): Item {
        return InvItem(this)
    }

    class InvItem(
        private val item: PItem
    ): AbstractItem() {
        override fun getItemProvider(): ItemProvider? {
            return item.build()
        }
        override fun handleClick(
            click: Click,
            player: Player,
            event: InventoryPreClickEvent
        ) {
            val newItem: PItem? = if (click is Left && item.leftClick != null) {
                item.leftClick!!.second.invoke(this, click)
            } else if (click is Click.Right && item.rightClick != null) {
                item.rightClick!!.second.invoke(this, click)
            } else {
                null
            }
            if (newItem != null) {
                update(item, newItem)
            }
        }

    }
}

fun Item.update(oldItem: PItem, newItem: PItem) {
    oldItem.from(newItem)
    this.notifyWindows()
}