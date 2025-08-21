package llc.redstone.playground.feature.schedules

import feature.actionMenu.ActionsMenu
import net.minestom.server.entity.Player
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.menus.ConfirmMenu
import llc.redstone.playground.menu.menus.MaterialSelector
import llc.redstone.playground.utils.Error
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.dialogs.TextDialogBuilder
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.error
import llc.redstone.playground.utils.isValidIdentifier
import llc.redstone.playground.utils.item
import llc.redstone.playground.utils.noError
import llc.redstone.playground.utils.openTextInput
import llc.redstone.playground.utils.success
import llc.redstone.playground.utils.toTitleCase
import net.kyori.adventure.text.Component
import net.minestom.server.inventory.click.Click
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.item.Item

class ScheduleEditorMenu(
    private val schedule: Schedule,
    private var tab: Int = 1
) : ActionsMenu(
    title = Component.empty(),
    displayName = colorize("<green>Schedule Editor"),
    actions = schedule.actions
) {
    private var prevTab = tab

    override fun title(): Component {
        return MenuCharacters.scheduleEditorActions.component(-10).append {
            when (tab) {
                1 -> MenuCharacters.scheduleEditorActionsSettings.component(-181)
                2 -> MenuCharacters.scheduleEditorSettings.component(-181)
                3 -> MenuCharacters.actionLibrary.component(-181)
                else -> Component.text("error")
            }
        }
    }

    fun initScheduleActionsSettingsTab(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# c # p # i # e #",
                "# # # # # # # # #",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(SchedulesMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                schedule.createDisplayItem().build()
            )
            .build()
    }

    fun initScheduleSettingsTab(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# n # d # m # D #",
                "# # # # # # # # #",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(SchedulesMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                schedule.createDisplayItem().build()
            )

            .addIngredient(
                'n', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Name")
                    .description("Change the name of the schedule")
                    .leftClick("open dialog") {
                        player.openTextInput() {
                            title = "<primary>Change Schedule Name"
                            message = "Enter the new name for the schedule:"
                            previous = schedule.name
                            validator = { newName ->
                                if (!newName.isValidIdentifier())
                                    error("Schedule name is not a valid identifier!")
                                else if (player.getSandbox()!!.schedules.any { it.name == newName })
                                    error("Schedule name already exists!")
                                else noError
                            }
                            action = { newName ->
                                schedule.name = newName
                                player.success("Schedule name changed to $newName")
                                this@ScheduleEditorMenu.open(player)
                            }
                        }
                    }
                    .buildItem()
            )
            .addIngredient(
                'd', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Description")
                    .description("Change the description of the schedule")
                    .leftClick("open chat") {
                        player.openTextInput() {
                            title = "<primary>Change Schedule Description"
                            message = "Enter the new description for the schedule:"
                            previous = schedule.description
                            visualizer = TextDialogBuilder.mcFiveVisualizer
                            validator = { newDescription ->
                                if (newDescription.isBlank())
                                    error("Schedule description cannot be empty!")
                                else noError
                            }
                            action = { newDescription ->
                                schedule.description = newDescription
                                player.success("Schedule description changed to $newDescription")
                                this@ScheduleEditorMenu.open(player)
                            }
                        }
                    }
                    .buildItem()
            )
            .addIngredient(
                'm', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Duration")
                    .description("Change the duration of the schedule")
                    .data("Current Duration", "${schedule.duration} ${schedule.unit.name.lowercase()}", null)
                    .leftClick("edit duration") {
                        player.openTextInput() {
                            title = "<primary>Change Schedule Duration"
                            message = "Enter the new duration for the schedule in ${schedule.unit.name.lowercase()}:"
                            previous = schedule.duration.toString()
                            validator = fun(newDuration: String): Error {
                                val duration = newDuration.toIntOrNull() ?: return error("Duration must be a number!")
                                return if (duration < 1 || duration > 5000) error("Duration must be at least 1 and at most 5000!")
                                else noError
                            }
                            action = { newDuration ->
                                val duration = newDuration.toLong()
                                schedule.duration = duration
                                schedule.createScheduleTask(player.getSandbox()!!)
                                open(player)
                                player.success("Schedule duration changed to $duration ${schedule.unit.name.lowercase()}")
                            }
                        }
                    }
                    .click(Click.Middle::class, "change duration unit") { _, _ ->
                        val units = TimeUnit.entries
                        val currentIndex = units.indexOf(schedule.unit)
                        val newIndex = (currentIndex + 1) % units.size
                        schedule.unit = units[newIndex]
                        schedule.createScheduleTask(player.getSandbox()!!)
                        open(player)
                        player.success(
                            "Schedule duration unit changed to ${
                                schedule.unit.name.lowercase().toTitleCase()
                            }"
                        )
                        null
                    }
                    .buildItem()
            )
            .addIngredient(
                'D', PItem(GlobalIcons.empty.item())
                    .name("<red>Delete Schedule")
                    .description("Delete this schedule permanently")
                    .leftClick("delete schedule") {
                        ConfirmMenu(this) {
                            player.getSandbox()!!.schedules.remove(schedule)
                            player.success("Schedule ${schedule.name} deleted!")
                            SchedulesMenu().open(player)
                        }.open(player)
                    }
                    .buildItem()
            )
            .build()
    }

    fun <G : Gui, S : Gui.Builder<G, S>> Gui.Builder<G, S>.addTabButtons(player: Player): Gui.Builder<G, S> {
        addIngredient(
            'A',
            PItem(GlobalIcons.empty.item())
                .name(if (tab != 1) "<green>Actions Settings" else "<red>Actions Settings")
                .description("Copy, paste, import or export actions")
                .apply {
                    if (tab != 1) leftClick("go to actions settings tab") {
                        tab = 1
                        open(player)
                    }
                }
                .buildItem()
        )
        addIngredient(
            's',
            PItem(GlobalIcons.empty.item())
                .name(if (tab != 2) "<green>Settings" else "<red>Settings")
                .description("Stuff that the schedule has that should be changed")
                .apply {
                    if (tab != 2) leftClick("go to settings tab") {
                        tab = 2
                        open(player)
                    }
                }
                .buildItem()
        )
        return this
    }

    override fun actionLibraryGui(player: Player): PagedGui.Builder<Item> {
        return super.actionLibraryGui(player)
            .addIngredient(
                'b', PItem(GlobalIcons.empty.item())
                    .name("<red>Back to Previous Tab")
                    .description("Go back to the schedule editor tab you were on")
                    .leftClick("go back") {
                        tab = prevTab
                        open(player)
                    }
                    .buildItem()
            )
    }

    override fun topGui(player: Player): ScrollGui.Builder<Item> {
        return super.topGui(player)
            .addIngredient(
                'a', PItem(GlobalIcons.empty.item())
                    .name(if (tab != 3) "<green>Action Library" else "<red>Action Library")
                    .description("Open the action library to view all available actions.")
                    .apply {
                        if (tab != 3) leftClick("open action library") {
                            prevTab = tab
                            tab = 3
                            open(player)
                        } else {
                            leftClick("close action library") {
                                tab = prevTab
                                open(player)
                            }
                        }
                    }
                    .buildItem()
            )
    }

    override fun initBottomGUI(player: Player): Gui? {
        when (tab) {
            1 -> return initScheduleActionsSettingsTab(player)
            2 -> return initScheduleSettingsTab(player)
            3 -> return super.initBottomGUI(player)
            else -> {
                player.err("Invalid tab: $tab")
                return null
            }
        }
    }
}
