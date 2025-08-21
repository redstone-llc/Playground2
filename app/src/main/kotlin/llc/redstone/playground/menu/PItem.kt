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
import net.minestom.server.inventory.click.Click.*
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem
import kotlin.reflect.KClass

class PItem(material: Material = Material.AIR, var builder: ItemStack = ItemStack.of(material)) {
    constructor(builder: ItemStack) : this(Material.AIR, builder)

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
    var data = mutableListOf<Triple<String, String, TextColor?>>()

    // Blank line
    // Click actions
    private var click: MutableMap<KClass<out Click>, Pair<String?, ((Item, Click) -> PItem?)>> = mutableMapOf()

    fun name(name: String): PItem {
        builder = builder.withCustomName(colorize(name))
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
        data.add(Triple(emoji, line, color))
        return this
    }

    fun leftClick(display: String, action: (Item, Click) -> PItem?): PItem {
        click[Left::class] = Pair(display, action)
        return this
    }

    fun leftClick(action: (Item, Click) -> PItem?): PItem {
        click[Left::class] = Pair(null, action)
        return this
    }

    fun leftClick(action: (Click) -> Unit): PItem {
        click[Left::class] = Pair(null) { item, click ->
            action(click)
            null // Return null to indicate no new item is created
        }
        return this
    }

    fun leftClick(display: String, action: (Click) -> Unit): PItem {
        click[Left::class] = Pair(display) { item, click ->
            action(click)
            null // Return null to indicate no new item is created
        }
        return this
    }

    fun rightClick(display: String, action: (Item, Click) -> PItem?): PItem {
        click[Right::class] = Pair(display, action)
        return this
    }

    fun rightClick(action: (Item, Click) -> PItem?): PItem {
        click[Right::class] = Pair(null, action)
        return this
    }

    fun rightClick(action: (Click) -> Unit): PItem {
        click[Right::class] = Pair(null) { item, click ->
            action(click)
            null // Return null to indicate no new item is created
        }
        return this
    }

    fun rightClick(display: String, action: (Click) -> Unit): PItem {
        click[Right::class] = Pair(display) { item, click ->
            action(click)
            null // Return null to indicate no new item is created
        }
        return this
    }

    fun longestLine(): Int {
        var longestLine = 0
        if (info != null) {
            longestLine = info!!.length
        }
        data.forEach { triple ->
            val lineLength = triple.first.length + triple.second.length + 2
            longestLine = maxOf(longestLine, lineLength)
        }
        click.forEach { (_, pair) ->
            val lineLength = pair.first?.length ?: 0
            longestLine = maxOf(longestLine, lineLength)
        }
        if (longestLine < 20) {
            longestLine = 20 // Minimum length for aesthetic reasons
        }
        return longestLine + 2 // Just so it doesnt look weird
    }

    fun <T : Click> click(
        type: KClass<T>,
        display: String,
        action: (Item, Click) -> PItem?
    ): PItem {
        click[type] = Pair(display, action)
        return this
    }

    fun <T : Click> click(
        type: KClass<T>,
        action: (Item, Click) -> PItem?
    ): PItem {
        click[type] = Pair(null, action)
        return this
    }

    fun build(): ItemStack {
        val lore = mutableListOf<Component>()
        if (info != null) {
            lore.add(colorize("<dark_gray>$info</dark_gray>"))
        }
        if (description != null && description!!.isNotEmpty()) {
            lore.add(Component.empty())
            lore.addAll(wrapLoreLines(colorize("<gray>${description!!}</gray>"), minOf(longestLine(), 40)))
        }
        if (data.isNotEmpty()) {
            lore.add(Component.empty())
            data.forEach { triple ->
                val color = (triple.third ?: successColor)!!.asHexString()
                lore.add(colorize("<${color}><bold> ${triple.first} </bold>${triple.second}</${color}>"))
            }
        }
        if (click.isNotEmpty()) {
            lore.add(Component.empty())
            for ((clickType, pair) in click) {
                val display = pair.first
                if (display != null) {
                    lore.add(
                        colorize(
                            "<bold>${
                                when (clickType) {
                                    Left::class -> "<primary>◀ "
                                    Right::class -> "<secondary>▶ "
                                    else -> ""
                                }
                            }$display${
                                when (clickType) {
                                    Left::class -> "</primary>"
                                    Right::class -> "</secondary>"
                                    else -> ""
                                }
                            }</bold>"
                        )
                    )
                }
            }
        }
        builder = builder.withLore(lore)
        return builder
    }

    fun from(rItem: PItem): PItem {
        this.builder = rItem.builder
        this.info = rItem.info
        this.description = rItem.description
        this.data = rItem.data
        this.click = rItem.click.toMutableMap()
        return this
    }

    //Haha funny
    fun buildItemStack(): ItemStack {
        return build()
    }

    // If we aint building, are we even doing it right?
    fun buildItem(): Item {
        return InvItem(this)
    }

    fun toBuilder(): ItemBuilder {
        return ItemBuilder(build())
    }

    fun clone(): PItem {
        return PItem().from(this)
    }

    class InvItem(
        private val item: PItem
    ) : AbstractItem() {
        override fun getItemProvider(): ItemProvider? {
            return item.toBuilder()
        }

        override fun handleClick(
            click: Click,
            player: Player,
            event: InventoryPreClickEvent
        ) {
            val action = item.click[click.javaClass.kotlin]
            if (action == null) return
            action.second.invoke(this, click)?.let { newItem ->
                update(item, newItem)
            }
        }

    }
}

fun Item.update(oldItem: PItem, newItem: PItem) {
    oldItem.from(newItem)
    this.notifyWindows()
}