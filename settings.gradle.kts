plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "book-order"
include("domain")
include("common")
include("usecase")
include("usecase:payment-usecase")
include("usecase:order-usecase")
include("adapter")
include("usecase:core")
include("bookstore-api")
include("adapter:mysql")
include("common:rest")
include("adapter:distributed-lock")
include("payment-worker")
include("common:util")
include("common:kafka")
include("external-api")
include("adapter:payment-api-client")
include("order-worker")
include("common:config")
include("adapter:kafka")
