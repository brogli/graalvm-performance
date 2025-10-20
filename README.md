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

```bash
podman play kube --network demo-network .k8s/receiver-graalvm.yaml
podman play kube --network demo-network .k8s/sender-graalvm.yaml
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

```bash
podman play kube --down .k8s/receiver-graalvm.yaml
podman play kube --down .k8s/sender-graalvm.yaml
```


## todo

- sender: amount of exceptions caught
- startup time metric
- use ManagementFactory.getMemoryMXBean(); to get memory stats
- compare with graalvm

## Prep native build

```bash
sdk install java 23.1.8.r21-nik
native-image --version
```

## Build natively
```bash
./gradlew nativeCompile
```

## Build natively, as OCI container
```bash
./gradlew :receiver:bootBuildImage --imageName=graalvm-performance/receiver-graalvm
./gradlew :sender:bootBuildImage --imageName=graalvm-performance/sender-graalvm
```