# AlpsLib Utils

## Head lookup

Head lookup is instance-based. Create a service in your plugin's `onEnable` and register the player-head cache listeners:

```java
HeadLookupService headLookupService = HeadLookupService.createForPlugin(this).registerEvents(this);
```

The provider factory creates either a SilentDevelopment HeadDB provider or an Arcaniax HeadDatabase provider. If neither integration is available, the lookup service has no provider and falls back to a placeholder skull when a head cannot be resolved.

### Soft depends

Head lookup integrations are optional. Depending plugins should declare soft depends on both providers so load order is correct:

```yaml
softdepend: [HeadDB, HeadDatabase]
```

Create `HeadLookupService` during your plugin's `onEnable` (after soft-depended plugins have started). If neither provider is present, lookups fall back to a placeholder skull.

`registerCustomHead(...)` may be called before Arcaniax HeadDatabase has finished loading; those IDs are queued and resolved when the database becomes ready.

### Legacy API

`AlpsHeadUtils` and `AlpsHeadEventListener` remain available as deprecated shims and will be removed in a future major release. Prefer `HeadLookupService` for new code.

```java
// Legacy
AlpsHeadUtils.initialize(this);
getServer().getPluginManager().registerEvents(new AlpsHeadEventListener(this), this);
```
