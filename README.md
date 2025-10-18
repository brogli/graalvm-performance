## Build Containers

```bash
# Build and push sender image
./gradlew :sender:jib
# Build and push receiver image
./gradlew :receiver:jib
# Build sender to local Docker
./gradlew :sender:jibDockerBuild
# Build receiver to local Docker
./gradlew :receiver:jibDockerBuild
```

## Run pods

```bash
podman network create demo-network
```

```bash
podman play kube --network demo-network .k8s/receiver.yaml
podman play kube --network demo-network .k8s/sender.yaml
```

## Check pods

```bash
podman pod ps
```

## Stop pods

```bash
podman play kube --down .k8s/receiver.yaml
podman play kube --down .k8s/sender.yaml
```