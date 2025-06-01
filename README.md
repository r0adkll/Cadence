# Cadence

A toy project for building lightweight game or game like experiences in Multiplatform Jetpack Compose.

## ECS

The core of this project is a simple Entity Component System, ECS, that coordinates and drives the elements you build into the `GameWorld`. For more reading on ECS and using them, check out:

- [Wikipedia](https://en.wikipedia.org/wiki/Entity_component_system)
- [A Simple Entity Component System (ECS) C++](https://austinmorlan.com/posts/entity_component_system/)

## Installation

[![Maven Central](https://img.shields.io/maven-central/v/com.r0adkll.cadence/core.svg)](https://search.maven.org/search?q=g:com.r0adkll.cadence)
[![Sonatype Snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.r0adkll.cadence/core.svg)](https://oss.sonatype.org/content/repositories/snapshots/com/r0adkll/cadence/)

```kotlin
dependencies {
  implementation("com.r0adkll.cadence:core:<latest_version>")
}
```

<details>

<summary>Snapshots</summary>

**settings.gradle.kts**

```kotlin
dependencyResolutionManagement {
  repositories {
    //…
    maven {
      url = Uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
  }
}
```

**build.gradle.kts**

```kotlin
dependencies {
  implementation("com.r0adkll.cadence:core:0.1.0-SNAPSHOT")
}
```

</details>



## Usage

```kotlin
@Composable
fun MyGameComposable() {
  val gameWorld = rememberGameWorld()
  gameWorld.Content(Modifier.fillMaxSize())
}
```

## License

```
 Copyright 2025 Drew Heavner

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     https://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

```
