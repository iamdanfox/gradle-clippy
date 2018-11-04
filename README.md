# gradle-clippy

Static analysis to help Gradle plugin authors use latest best practises.

## TaskConfigurationAvoidance

Rather than immediately creating all the tasks you need, [Gradle recommends](https://docs.gradle.org/current/userguide/task_configuration_avoidance.html) _registering_ your task name and configuring it lazily.  This results in a performance improvement because tasks that are not needed are never actually configured.

This BugChecker implements most of the recommendations in the [comparison table](https://docs.gradle.org/current/userguide/task_configuration_avoidance.html#sec:old_vs_new_configuration_api_overview),
for example:

```diff
-project.getTasks().getByName("foo", Closure/Action)
+project.getTasks().named("foo").configure(org.gradle.api.Action)
```

## Goals

- [Don't resolve dependencies at configuration time](https://guides.gradle.org/performance/#don_t_resolve_dependencies_at_configuration_time)
- [Lazily configured tasks using `Provider`, `Property` etc](https://docs.gradle.org/current/userguide/lazy_configuration.html)
- [Restrict the plugin implementation to Gradle's public API](https://guides.gradle.org/designing-gradle-plugins/#restricting_the_plugin_implementation_to_gradle_s_public_api)
- [Prefer Extensions over Conventions](https://guides.gradle.org/implementing-gradle-plugins/#extensions_vs_conventions)
- Incremental tasks using `@Input` and `@Output` annotations
