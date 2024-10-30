plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "jumso"
include("root")
include("was")
include("chat")
include("email")
include("domain")
include("domain:main")
findProject(":domain:main")?.name = "main"
