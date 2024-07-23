plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.molean"
version = "1.0"

dependencies {
    implementation("net.fabricmc:access-widener:2.1.0")
    implementation("net.fabricmc:tiny-remapper:0.10.1:fat")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.zeroturnaround:zt-zip:1.17")
    implementation("com.google.guava:guava:33.0.0-jre")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

gradlePlugin {
    plugins {
        create("ignite") {
            id = "com.molean.ignite.access-widener"
            implementationClass = "com.molean.IgniteAWP"
        }
    }
}
