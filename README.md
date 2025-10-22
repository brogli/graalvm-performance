## Build Containers

```bash
#jvm
./gradlew :sender:jibDockerBuild
./gradlew :receiver:jibDockerBuild
```

```bash
#graalvm
./gradlew :receiver:bootBuildImage --imageName=graalvm-performance/receiver-graalvm
./gradlew :sender:bootBuildImage --imageName=graalvm-performance/sender-graalvm
```

## Run pods

```bash
#First time only
podman network create demo-network
```

```bash
# jvm
podman play kube --network demo-network .k8s/receiver.yaml
podman play kube --network demo-network .k8s/sender.yaml
```

```bash
# graalvm
podman play kube --network demo-network .k8s/receiver-graalvm.yaml
podman play kube --network demo-network .k8s/sender-graalvm.yaml
```


## Stop pods

```bash
#jvm
podman play kube --down .k8s/receiver.yaml
podman play kube --down .k8s/sender.yaml
```

```bash
#graalvm
podman play kube --down .k8s/receiver-graalvm.yaml
podman play kube --down .k8s/sender-graalvm.yaml
```

## Check pods

```bash
podman pod ps
```

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


todo:
- total size also in other app
- get better total size metric for graalvm