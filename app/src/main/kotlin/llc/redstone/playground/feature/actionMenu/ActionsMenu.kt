package feature.actionMenu

import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionCategory
import llc.redstone.playground.action.ActionEnum
import llc.redstone.playground.menu.*
import net.minestom.server.entity.Player
import llc.redstone.playground.feature.actionMenu.ActionEditMenu
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.DownItem
import llc.redstone.playground.menu.items.UpItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.item
import llc.redstone.playground.utils.openTextInput
import llc.redstone.playground.utils.toTitleCase
import net.kyori.adventure.text.Component
import net.minestom.server.inventory.click.Click
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.items.FunctionIcons
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

open class ActionsMenu(
    val actions: MutableList<Action>,
    val backMenu: AbstractMenu? = null,
    title: Component = MenuCharacters.actionsBase.component(-10).append(MenuCharacters.actionLibrary.component(-181)),
    displayName: Component = colorize("<green>Actions Menu")
) : NormalMenu(title, displayName) {
    var category: ActionCategory = ActionCategory.ALL
    var search: String? = null

    open fun topGui(player: Player): ScrollGui.Builder<Item> {
        return ScrollGui.items()
            .setStructure(
                "x x x x x x x x u",
                "x x x x x x x x u",
                "x x x x x x x x #",
                "x x x x x x x x d",
                "x x x x x x x x d",
                "# # # a a a # # #"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('u', UpItem())
            .addIngredient('d', DownItem())
            .addIngredient('a', PItem(GlobalIcons.empty.item())
                .name("<red>Open Action Library")
                .info("Currently Open")
                .description("Open the action library to view all available actions.")
                .buildItem()
            )
            .setContent(content(player))
    }

    override fun initTopGUI(player: Player): Gui {
        return topGui(player).build()
    }



    open fun actionLibraryGui(player: Player): PagedGui.Builder<Item> {
        fun categoryItem(category: ActionCategory): Item {
            val stack = if (this.category == category) {
                FunctionIcons.getIcon(category.name).item()
            } else {
                GlobalIcons.empty.item()
            }
            return PItem(stack)
                .name("<green>${category.name.toTitleCase()} Actions")
                .description("Click to view ${category.name.lowercase()} actions")
                .apply {
                    if (this@ActionsMenu.category == category) {
                        info("Currently Open")
                    } else {
                        leftClick("open category") {
                            this@ActionsMenu.category = category
                            this@ActionsMenu.search = null
                            open(player)
                        }
                    }
                }
                .buildItem()
        }

        fun actionLibraryContent(): List<Item> {
            val filtered = ActionEnum.entries.map { it.clazz.getDeclaredConstructor().newInstance() }
                .filter { action ->
                (category == ActionCategory.ALL || action.enum.category == category) &&
                (search == null || action.name.contains(search!!, true)
                        || action.description.contains(search!!, true))
            }
            return filtered.map { action ->
                action.createAddDisplayItem()
                    .leftClick("add") {
                        actions.add(action)
                        open(player)
                    }
                    .buildItem()
            }
        }

        return PagedGui.items()
            .setStructure(
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "b # p e s l m # S"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .apply { if (backMenu != null) addIngredient('b', BackItem(backMenu)) }
            .addIngredient('p', categoryItem(ActionCategory.ALL))
            .addIngredient('e', categoryItem(ActionCategory.PLAYER))
            .addIngredient('s', categoryItem(ActionCategory.ENTITY))
            .addIngredient('l', categoryItem(ActionCategory.SANDBOX))
            .addIngredient('m', categoryItem(ActionCategory.MISC))
            .addIngredient('S', PItem(GlobalIcons.empty.item())
                .name("<green>Search Actions")
                .description("Search for actions by name or description.")
                .leftClick("search") {
                    player.openTextInput {
                        action = { input ->
                            search = input.ifBlank { null }
                            open(player)
                        }
                    }
                }
                .rightClick("clear search") {
                    search = null
                    open(player)
                }
                .buildItem()
            )
            .setContent(actionLibraryContent())
    }

    override fun initBottomGUI(player: Player): Gui? {
        return actionLibraryGui(player).build()
    }

    private fun content(player: Player): List<Item> {
        return actions.map { action ->
            action.createDisplayItem()
                .leftClick("edit") { _, _ ->
                    if (action.properties.isNotEmpty()) ActionEditMenu(
                        action,
                        this
                    ).open(player)

                    null
                }
                .click(Click.Middle::class, "<success>⮝ clone</success>") { _, _ ->
                    val newAction = action.clone()
                    actions.add(newAction)
                    open(player)

                    null
                }
                .rightClick("remove") { _, _ ->
                    actions.remove(action)
                    open(player)

                    null
                }.buildItem()
        }
    }
}