package llc.redstone.playground.commands

import llc.redstone.playground.feature.housingMenu.PlaygroundMenu
import llc.redstone.playground.managers.*
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.setInstanceSafe
import llc.redstone.playground.utils.success
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand


object PlaygroundCommand: Kommand("playground", "pg") {
    init {
        default { sender, _ -> {
            sender.success("Welcome to the Playground! Use /playground help for commands.")
        }}

        executes(Arg.literal("help")) {
            help(player)
        }

        val uuidArg = Arg.string("uuid")
        executes(Arg.literal("goto"), uuidArg) {
            val uuid = args[uuidArg]
            goto(player, uuid)
        }

        executes(Arg.literal("create")) {
            try {
                var sandbox = createSandbox(player)
                if (sandbox.instance == null) return@executes player.err("An error occurred while creating your sandbox instance.")

                player.setInstance(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
                player.gameMode = GameMode.CREATIVE

                sandbox.startEventListener()

                player.success("You have been teleported to your sandbox instance!")
            } catch (e: Exception) {
                e.printStackTrace()
                player.err("An error occurred while creating your sandbox: ${e.message}")
            }
        }

        executes(Arg.literal("menu")) {
            try {
                menu(player)
            } catch (e: Exception) {
                e.printStackTrace()
                player.err("An error occurred while opening the playground menu: ${e.message}")
            }
        }

    }

    fun help(sender: Player) {
        sender.success("Playground Commands:")
        sender.success("/playground help - Show this help message.")
        sender.success("/playground create <name> - Create a new sandbox instance with the given name.")
        sender.success("/playground goto <uuid> - Teleport to a sandbox instance by its UUID.")
        sender.success("/playground menu - Open the playground menu.")
    }

    fun menu(sender: Player) {
        if (sender.getSandbox() == null) {
            sender.err("You are not in a sandbox!")
            return
        }
        PlaygroundMenu().open(sender)
    }

    fun goto(sender: Player, uuid: String) {
        val sandbox = loadedSandboxes[uuid] ?: return sender.err("That sandbox does not exist.")
        if (sandbox.instance == null) sandbox.loadInstance()
        if (sandbox.instance == null) return sender.err("An error occurred while teleporting to your sandbox.")

        sender.setInstanceSafe(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
        sender.gameMode = GameMode.CREATIVE

        sandbox.save()
        sandbox.startEventListener()
        sandbox.spawnNPCs()

        sender.success("You have been teleported to sandbox instance <dark_gray>(ID: $uuid)</dark_gray>!")
    }
}