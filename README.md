## Build Containers

- ./gradlew :sender:jib - Build and push sender image
- ./gradlew :receiver:jib - Build and push receiver image
- ./gradlew :sender:jibDockerBuild - Build sender to local Docker
- ./gradlew :receiver:jibDockerBuild - Build receiver to local Docker


## Run pods

```bash
podman play kube .k8s/receiver.yaml
podman play kube .k8s/sender.yaml
```
F
## Check pods

```bash
podman pod ps
```

## Stop pods

```bash
podman play kube --down .k8s/receiver.yaml
podman play kube --down .k8s/sender.yaml
```