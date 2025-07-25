package llc.redstone.playground

import com.gestankbratwurst.ambrosia.Ambrosia
import com.gestankbratwurst.ambrosia.impl.mongodb.MongoAmbrosia
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
import llc.redstone.playground.managers.loadSandboxes
import llc.redstone.playground.utils.ActionTypeAdapter
import llc.redstone.playground.utils.EntityTypeTypeAdapter
import llc.redstone.playground.utils.initMinecraftServer
import llc.redstone.playground.utils.logging.Loading
import llc.redstone.playground.utils.logging.Logger
import me.lucko.spark.minestom.SparkMinestom
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.extras.MojangAuth
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


object Playground : OrionServer() {
    lateinit var mongoAmbrosia: MongoAmbrosia


    init {
        Loading.start("Pre-Initializing Playground") { initMinecraftServer(server); message("Pre-Initialized Playground", 1.0) }

        Loading.start("Playground Startup") {
            progress(0.0)
            Loading.start("Creating Lobby Instance") { createLobbyInstance(); message("Lobby instance created", 1.0) }
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
                        }
                        .database(database)
                        .build()

                    message("Loading Sandboxes from MongoDB...", 0.75)
                    loadSandboxes()

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


fun main() {
    Playground.bind()
}