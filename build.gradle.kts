plugins {
	java
	checkstyle
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "8.9.0"
}

group = "com.staticoyster"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

checkstyle {
	toolVersion = "10.21.4"
	configDirectory = rootProject.layout.projectDirectory.dir("linter/checkstyle")
	maxWarnings = 0
}

spotless {
	java {
		target("src/*/java/**/*.java")
		importOrder("\\#", "java", "javax", "jakarta", "org", "com", "tools")
		removeUnusedImports()
		trimTrailingWhitespace()
		endWithNewline()
		leadingSpacesToTabs()
	}
}

val immutablesVersion = "2.10.1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-json")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.springframework.boot:spring-boot-starter-restclient:4.1.0")

	compileOnly("org.immutables:value:$immutablesVersion")
	annotationProcessor("org.immutables:value:$immutablesVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
//	failOnNoDiscoveredTests = false
}
