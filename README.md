# RegistryTools

[![Java CI with Gradle](https://github.com/DrupalDoesNotExists/RegistryTools/actions/workflows/gradle.yml/badge.svg)](https://github.com/DrupalDoesNotExists/RegistryTools/actions/workflows/gradle.yml)
[![](https://jitpack.io/v/DrupalDoesNotExists/RegistryTools.svg)](https://jitpack.io/#DrupalDoesNotExists/RegistryTools)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5b735d81d78e4645be7805eeea154a01)](https://www.codacy.com/gh/DrupalDoesNotExists/RegistryTools/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DrupalDoesNotExists/RegistryTools&amp;utm_campaign=Badge_Grade)
[![Dependency Review](https://github.com/DrupalDoesNotExists/RegistryTools/actions/workflows/dependency-review.yml/badge.svg)](https://github.com/DrupalDoesNotExists/RegistryTools/actions/workflows/dependency-review.yml)

RegistryTools is a simple API wrapper around NMS Registry with Mojang mappings.
It supports

  * Getting and caching registries
  * Registering own entries
  * ResourceKey creation
  * Freezing/unfreezing registries

## Why registries

NMS registries allows You to add custom feature to default Minecraft.
But, they have several differences from the Bukkit API.

**You may need registries for these purposes**

 1. Creating custom biomes
 2. Adding custom structures, block entities, items, sounds
 3. etc

**Default commands uses registry, all your content will be visible!**

## What about reflection

RegistryTools does not use Reflection API in usual sense.
Only thing used is VarHandle mechanism. It works much faster and cleaner.

[Oracle JavaDoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/VarHandle.html)

## Dependencies

RegistryTools isn't independent plugin, you need to shade it.
All dependencies provided via JitPack.

(Example below is for Gradle, be sure to adapt it for you build system)
```groovy
repositories {
    // ...
    maven { url 'https://jitpack.io' }
    // ...
}
```
```groovy
dependencies {
    implementation 'com.github.DrupalDoesNotExists:RegistryTools:<VERSION>:dev'
}
```

Also, you can use version shortcut provided by JitPack if you need only latest version.
```groovy
dependencies {
    implementation 'com.github.DrupalDoesNotExists:RegistryTools:master-SNAPSHOT:dev'
}
```

## Usage

```java
// ...
import ru.catsplace.registrytools.RegistryTools;
// ...

// Custom biome example
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        
        RegistryTools tools = new RegistryTools(this, 1); // instantiate with this plugin as host and 1 as cache size
        
        // ...
        
        Biome biome = createCustomBiome(); // Get biome instance
        Registry<Biome> registry = tools.cachedRegistry(Registry.BIOME_REGISTRY); // Get registry instance
        
        tools.freeze(registry, false); // Unfreeze registry
        tools.register(registry, "mybiome", biome); // Will add biome as myplugin:mybiome
        tools.freeze(registry, true); // Freeze registry again
        
        // ...
        
    }
    
}
```

## TO-DO

At the moment, the project is completed, and the only plans left are updating to new versions and fixing bugs.
However, if necessary, changes related to the API will be made to the project.

## License

All stuff is licensed under [MIT](https://choosealicense.com/licenses/mit/).

## Contributing

You can contribute to the project by opening issues and pull requests.

Please, try to create issues and bug reports that are:

  * *Reproducible*. If it is possible...
    
  * *Detailed*. Include as many details as you can
    
  * *Unique*. Do not copy or duplicate existing issue