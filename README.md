# gradle-clippy

Static analysis to help Gradle plugin authors use latest best practises.

## Goals

- [Don't resolve dependencies at configuration time](https://guides.gradle.org/performance/#don_t_resolve_dependencies_at_configuration_time)
- [Lazily configured tasks using `Provider`, `Property` etc](https://docs.gradle.org/current/userguide/lazy_configuration.html)
- [Task Configuration Avoidance](https://docs.gradle.org/current/userguide/task_configuration_avoidance.html)
    ```diff
    -TaskContainer#getByName(String, Closure/Action)
    +TaskContainer#named(String).configure(Closure/Action)
    ```
- [Restrict the plugin implementation to Gradle's public API](https://guides.gradle.org/designing-gradle-plugins/#restricting_the_plugin_implementation_to_gradle_s_public_api)
- [Prefer Extensions over Conventions](https://guides.gradle.org/implementing-gradle-plugins/#extensions_vs_conventions)
- Incremental tasks using `@Input` and `@Output` annotations
