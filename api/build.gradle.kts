plugins {
    `maven-publish`
}

dependencies {
    compileOnlyApi("io.papermc.paper", "paper-api", "1.18-R0.1-SNAPSHOT")
    compileOnlyApi("org.checkerframework", "checker-qual", "3.15.0")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories.maven {
        url = uri("https://repo.pl3x.net/snapshots")
        credentials(PasswordCredentials::class)
    }
}
