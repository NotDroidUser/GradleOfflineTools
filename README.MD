# Gradle Offline Manager 
#### (something that makes your life easier when you can't go online anywhere to download maven repos)

Gradle Offline Manager is a kit of programs with the same idea, go offline and don't mind if you don't have internet on a week

# Kradle

Kradle makes use of that `.gradle` cache folder
Sometimes you need go offline, but that cache sometimes:
* Don't works in two projects when you need it
* It don't download all the pom/module files
* Sometimes it updates the libraries and there isn't anywhere to be found a jar, klib or aar for the one that your project needs, and library isn't a pom
* You have a sh*t connection and you cant afford waiting 4 hours for every project init and want a solution of that
* Gradle removes some of the actual cache and get you in middle of a trip without roaming/wifi
 
For those cases (and more) was this program is made for

### How i can use the actual repo that is made with this program

You can use putting in your settings.gradle file for the plugins

```groovy
pluginManagement {
    repositories {
        maven {
            name = "offlineRepo"
            url = "C:\\SomePlace\\Somewhere\\youPutYourOfflineRepos\\"
            allowInsecureProtocol = true
        }
    }
}
```

Or if it's .kts:

```kotlin
pluginManagement {
      repositories {
        maven {
          name = "offlineRepo"
          url = uri("C:\\SomePlace\\Somewhere\\youPutYourOfflineRepos\\")
          isAllowInsecureProtocol = true
        }
      }
}
```

And in any other repositories code, it will be considered as offline repo, so if you put the gradle in offline mode it will works with that one (technically)

Or/And the same in in the dependencyResolutionManagement, if you have this one by default, the repos will fail if you add anyone else but their predetermined repos

```groovy
//On this one part
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
```

You can also add the repos via [https://docs.gradle.org/current/userguide/init_scripts.html](the gradle init.d) folder, but as long i tried the repos are applied only to the projects but not the plugins.
If you have the offline gradle doc that link is on $GRADLE_USER_HOME/docs/userguide/init_scripts.html
Also if you have connection, it will download the libraries has missing files (with the repos configured in the configuration)


## MavenSearcher

As a complement of Kradle the original idea of this code, i have made a *"search"* likely the original maven repository one because you always need to know what version you have and also a shorthand for getting the implementation for your project 
It allows you:
* Know what versions you have on the offline repo
* Get the groovy/maven/kts/toml code for less time wasted
* Shows if you have, pom, source, or module file, also detects if you have a missing library via pom 


### Future:
* Download any missing lib part like a doctor for your offline repo 
* Show you some info about their dependencies like maven repo
* * Make a dependency tree of the dependency 
* * Show the path, etc


## This program is licenced under GPLv3
