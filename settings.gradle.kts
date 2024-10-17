plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "book-order"
include("domain")
include("core")
include("usecase")
include("usecase:create-order-usecase")
include("usecase:payment-usecase")
include("usecase:order-usecase")
include("adapter")
include("usecase:core")
