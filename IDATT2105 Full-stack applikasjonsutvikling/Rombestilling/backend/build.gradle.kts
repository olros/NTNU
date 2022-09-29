import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
  	id("com.google.cloud.tools.jib") version "3.0.0"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
	kotlin("plugin.jpa") version "1.4.32"
	kotlin("kapt") version "1.4.10"
	id("idea")
	id ("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
}
group = "fullstack-project"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11


repositories {
	mavenCentral()
}

var queryDslVersion = "4.2.1"




dependencies {
	implementation ("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation ("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation ("io.jsonwebtoken:jjwt-jackson:0.11.2")
	implementation("org.modelmapper:modelmapper:2.3.0")
	//QueryDsl (filtering)
	implementation("com.querydsl:querydsl-core:${queryDslVersion}")
	implementation("com.querydsl:querydsl-jpa:${queryDslVersion}")
	kapt("com.querydsl:querydsl-apt:${queryDslVersion}:jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	//Logging
	implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("mysql:mysql-connector-java")
	testRuntimeOnly("com.h2database:h2")
	implementation("com.zaxxer:HikariCP:3.4.5")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("io.github.serpro69:kotlin-faker:1.6.0")
	implementation("com.opencsv:opencsv:5.0")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	testImplementation("org.awaitility:awaitility:3.1.2")
}



querydsl {
	querydslDefault = true
	jpa = true
	library = "com.querydsl:querydsl-apt:${queryDslVersion}"
	querydslSourcesDir = "src/main/querydsl"
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=compatibility")
		jvmTarget = "11"
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}

val ktlint by configurations.creating

dependencies {
	ktlint("com.pinterest:ktlint:0.41.0")
}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
	inputs.files(inputFiles)
	outputs.dir(outputDir)

	description = "Check Kotlin code style."
	classpath = ktlint
	main = "com.pinterest.ktlint.Main"
	args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
	inputs.files(inputFiles)
	outputs.dir(outputDir)

	description = "Fix Kotlin code style deviations."
	classpath = ktlint
	main = "com.pinterest.ktlint.Main"
	args = listOf("-F", "src/**/*.kt")
}


