package com.example.bookorder

import org.junit.Ignore
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

@Ignore
@SpringBootTest
@ContextConfiguration(initializers = [IntegrationTestConfiguration.IntegrationTestInitializer::class])
abstract class IntegrationTestConfiguration {

    companion object {

        private const val DOCKER_COMPOSE_FILE_PATH = "container/test/docker-compose.yml"

        private val projectRoot: File = File(System.getProperty("project.root")
            ?: throw IllegalStateException("project.root system property is not set"))

        val container: ComposeContainer = run {
            val dockerComposeFile = File(projectRoot, DOCKER_COMPOSE_FILE_PATH)
            if (!dockerComposeFile.exists()) {
                throw IllegalStateException("Docker Compose file not found at: ${dockerComposeFile.absolutePath}")
            }
            ComposeContainer(dockerComposeFile)
                .withExposedService("mysql", 3306)
                .withExposedService("redis", 6380)
                .waitingFor("mysql", Wait.forLogMessage(".*ready for connections.*", 1))
                .waitingFor("redis", Wait.forLogMessage(".*Ready to accept connections.*", 1))
        }

        init {
            container.start()
        }
    }

    class IntegrationTestInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            val properties = mutableMapOf<String, String>()
            val rdbmsHost = container.getServiceHost("mysql", 3306)
            val rdbmsPort = container.getServicePort("mysql", 3306)
            properties["spring.datasource.url"] = "jdbc:mysql://$rdbmsHost:$rdbmsPort/test?useSSL=false&serverTimezone=Asia/Seoul"
            TestPropertyValues.of(properties)
                .applyTo(applicationContext)

            val redisHost = container.getServiceHost("redis", 6380)
            val redisPort = container.getServicePort("redis", 6380)
            properties["redis.single.node"] = "$redisHost:$redisPort"

            TestPropertyValues.of(properties).applyTo(applicationContext)
        }
    }
}