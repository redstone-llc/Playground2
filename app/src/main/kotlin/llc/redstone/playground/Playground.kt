package llc.redstone.playground

import com.gestankbratwurst.ambrosia.Ambrosia
import com.gestankbratwurst.ambrosia.impl.mongodb.MongoAmbrosia
import com.google.gson.Gson
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.github.togar2.pvp.MinestomPvP
import io.github.togar2.pvp.feature.CombatFeatureSet
import io.github.togar2.pvp.feature.CombatFeatures
import llc.redstone.playground.action.Action
import llc.redstone.playground.commands.PlaygroundCommand
import llc.redstone.playground.managers.loadInstance
import llc.redstone.playground.managers.loadSandboxes
import llc.redstone.playground.managers.loadedSandboxes
import llc.redstone.playground.utils.ActionTypeAdapter
import llc.redstone.playground.utils.EntityTypeTypeAdapter
import llc.redstone.playground.utils.MessageUtils
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.initMinecraftServer
import llc.redstone.playground.utils.logging.Loading
import llc.redstone.playground.utils.logging.Logger
import llc.redstone.playground.utils.setInstanceSafe
import me.lucko.spark.minestom.SparkMinestom
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.extras.MojangAuth
import net.sinender.lobby.LOBBY_INSTANCE
import net.sinender.lobby.createLobbyInstance
import org.bson.UuidRepresentation
import org.everbuild.blocksandstuff.blocks.BlockBehaviorRuleRegistrations
import org.everbuild.blocksandstuff.blocks.BlockPlacementRuleRegistrations
import org.everbuild.blocksandstuff.blocks.PlacedHandlerRegistration
import org.everbuild.blocksandstuff.fluids.MinestomFluids
import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission
import org.everbuild.celestia.orion.platform.minestom.pack.withResourcePacksInDev
import java.nio.file.Path


class Playground(
    val sandboxes: MutableList<String>
) : OrionServer() {
    companion object {
        lateinit var mongoAmbrosia: MongoAmbrosia
        lateinit var gson: Gson
    }

    init {
        Loading.start("Pre-Initializing Playground") { initMinecraftServer(server); message("Pre-Initialized Playground", 1.0) }

        Loading.start("Playground Startup") {
            progress(0.0)
            Loading.start("Creating Lobby Instance") {
                if (sandboxes.isEmpty() || sandboxes.size > 1) {
                    createLobbyInstance(); message("Lobby instance created", 1.0)
                } else {
                    // If only one sandbox is specified, we can skip creating a lobby instance
                    message("Skipping lobby instance creation, single sandbox mode", 1.0)
                    MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
                        val player = event.player
                        val uuid = sandboxes.firstOrNull() ?: return@addListener
                        val sandbox = loadedSandboxes[uuid] ?: return@addListener player.kick(MessageUtils.err("Sandbox with UUID $uuid not found."))
                        if (sandbox.instance == null) sandbox.loadInstance()
                        if (sandbox.instance == null) return@addListener player.kick(MessageUtils.err("An error occurred while teleporting to your sandbox."))

                        event.spawningInstance = sandbox.instance
                    }

                    MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent::class.java) {
                        PlaygroundCommand.goto(it.player, sandboxes.firstOrNull() ?: return@addListener)
                    }
                }
            }
            progress(0.33)
            Loading.start("Registering Commands") {
                PlaygroundCommand.register()
            }
            progress(0.66)
            Loading.start("Initializing Dependencies") {
                //All of this may be replaced to use message rather than Loading.start
                Loading.start("Initializing MinestomPvP...") { MinestomPvP.init() }

                Loading.start("Initializing Blocks and Stuff") {
                    BlockPlacementRuleRegistrations.registerDefault()
                    BlockBehaviorRuleRegistrations.registerDefault()
                    PlacedHandlerRegistration.registerDefault()
                    MinestomFluids.enableFluids()
                    MinestomFluids.enableVanillaFluids()
                }

                Loading.start("Initializing Combat Features") {
                    val modernVanilla: CombatFeatureSet = CombatFeatures.legacyVanilla()
                    MinecraftServer.getGlobalEventHandler().addChild(modernVanilla.createNode())
                }

                // Initialize the MongoDB connection
                Loading.start("Initializing MongoDB") {
                    message("Connecting to MongoDB", 0.0)
                    val connectionString = ConnectionString("mongodb://localhost:27017")
                    val settings: MongoClientSettings = MongoClientSettings.builder()
                        .uuidRepresentation(UuidRepresentation.STANDARD)
                        .applyConnectionString(connectionString)
                        .build()
                    val mongoClient: MongoClient = MongoClients.create(settings)
                    val database: MongoDatabase = mongoClient.getDatabase("playground")

                    message("MongoDB connected successfully!", 0.5)
                    // Create an Ambrosia instance
                    mongoAmbrosia = Ambrosia.mongoDB()
                        // Add additional gson configuration if needed
                        .gsonBuild()
                        .construct {
                            it.registerTypeAdapter(EntityType::class.java, EntityTypeTypeAdapter())
                            it.registerTypeAdapter(Action::class.java, ActionTypeAdapter())

                            gson = it.create()
                            it
                        }
                        .database(database)
                        .build()

                    message("Loading Sandboxes from MongoDB...", 0.75)
                    loadSandboxes(sandboxes)

                    message("MongoDB and Ambrosia initialized successfully!", 1.0)
                }

                Loading.start("Initializing Spark") {
                    val directory = Path.of("spark")
                    SparkMinestom.builder(directory)
                        .commands(true)
                        .permissionHandler { sender, permission ->
                            (sender as? Player)?.hasPermission(permission) ?: true
                        }
                        .enable()
                }
            }

            message("Playground initialized successfully!", 1.0)
        }

        Loading.start("Hashing resource packs...") {
            withResourcePacksInDev()
        }
    }
}


fun main(args: Array<String>) {
    val pg = Playground(args.toMutableList())
    pg.bind()
}